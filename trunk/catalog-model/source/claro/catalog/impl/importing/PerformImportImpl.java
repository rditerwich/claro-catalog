package claro.catalog.impl.importing;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Iterables.concat;
import static easyenterprise.lib.util.CollectionUtil.unique;
import static easyenterprise.lib.util.MathUtil.orZero;
import static easyenterprise.lib.util.ObjectUtil.orElse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import claro.catalog.CatalogDao;
import claro.catalog.CatalogModelService;
import claro.catalog.command.importing.PerformImport;
import claro.catalog.command.importing.PerformImport.Result;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.catalog.util.PropertyStringConverter;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.Label;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Product;
import claro.jpa.catalog.Property;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportDefinition;
import claro.jpa.importing.ImportProperty;
import claro.jpa.importing.TabularImportDefinition;

import com.google.common.base.Function;

import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.jpa.JpaService;
import easyenterprise.lib.sexpr.DefaultContext;
import easyenterprise.lib.sexpr.SExpr;
import easyenterprise.lib.sexpr.SExprParseException;
import easyenterprise.lib.sexpr.SExprParser;

@SuppressWarnings("serial")
public class PerformImportImpl extends PerformImport implements CommandImpl<Result>{

	private CatalogDao dao;
	private CatalogModel model;
	private CategoryCache cache;
	
	private ImportDefinition importDefinition;
	private Property matchProperty;
	private String matchPropertyLabel;
	private SExpr languageExpression;
	private Map<Property, SExpr> propertyValueExpressions;
	private PrintWriter log;
	private SExpr outputChannelExpression;
	private List<SExpr> categoryExpressions;
	
	@Override
	public Result execute() throws CommandException {
		checkValid();
		this.dao = new CatalogDao(JpaService.getEntityManager());
		this.model = CatalogModelService.getCatalogModel(catalogId);
		this.cache = new CategoryCache();
		
		Result result = new Result();
		result.success = false;
		
		StringWriter stringWriter = new StringWriter();
		log = new PrintWriter(stringWriter);
		try {
			try {
				
				// fetch import definition
				importDefinition = dao.getImportDefinitionById(importDefinitionId);
				
				// match propeprty
				matchProperty = checkMatchProperty(importDefinition);
				matchPropertyLabel = propertyLabel(matchProperty, "");
				
				// expresions
				SExprParser exprParser = new SExprParser();
				languageExpression = exprParser.parse(orElse(importDefinition.getLanguageExpression(), ""));
				outputChannelExpression = exprParser.parse(orElse(importDefinition.getOutputChannelExpression(), ""));
				propertyValueExpressions = parsePropertyValueExpressions(importDefinition, exprParser);
				categoryExpressions = parseCategoryExpressions(importDefinition, exprParser);
				
				// expression context
				DefaultContext exprContext = new DefaultContext();
				
				// get url
				SExpr importUrlExpression = exprParser.parse(importUrl != null ? importUrl : importDefinition.getImportUrlExpression());
				URL url = new URL(importUrlExpression.evaluate(exprContext));
				
				// increase sequence number
				Integer sequence = orZero(importDefinition.getSequenceNr()) + 1;
				importDefinition.setSequenceNr(sequence);
				exprContext.setVariable("sequence", sequence.toString());
				
				if (importDefinition instanceof TabularImportDefinition) {
					importTabularData((TabularImportDefinition) importDefinition, url, exprContext);
				}
				result.success = true;
				return result;
			} catch (CommandException e) {
				e.printStackTrace(log);
				throw e;
			} catch (Exception e) {
				e.printStackTrace(log);
				throw new CommandException(e);
			}
		} finally {
			result.log = stringWriter.getBuffer().toString();
		}
	}

	private void importTabularData(TabularImportDefinition importDefinition, URL url, DefaultContext exprContext) throws Exception {
		
		// separator chars
		String separators = importDefinition.getSeparatorChars();
		if (separators == null) {
			separators = ",;\t";
		}
		
		Map<String, String> nameValues = new HashMap<String, String>();
		
		// determine structure of import file
		Charset charset = Charset.forName(importDefinition.getCharset() != null ? importDefinition.getCharset() : "UTF-8");
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), charset));
		log.println("Opened import file: " + url);
		try {
			List<String> headerFields = Collections.emptyList();
			if (importDefinition.getHeaderLine()) {
				String line = reader.readLine();
				if (line == null) throw new PerformImportException("Missing header-line in file " + url);
				headerFields = parseLine(line, separators);
			}
			log.println("Read header: " + headerFields);
			
			// loop over lines
			nameValues.clear();
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				
				// parse fields
				List<String> fields = parseLine(line, separators);
				log.println("Readline: " + fields);
				
				// add a context variable for each field
				for (int i = 0; i < fields.size(); i++) {
					String name = i < headerFields.size() ? headerFields.get(i) : "";
					nameValues.put(name, fields.get(i));
				}
				importRecord(nameValues, exprContext);
			}
		} finally {
			reader.close();
		}
	}
	
	private void importRecord(Map<String,String> nameValues, DefaultContext originalExprContext) throws Exception {
		
		// create context with values
		int index = 0;
		DefaultContext exprContext = new DefaultContext(originalExprContext);
		for (Entry<String, String> nameValue : nameValues.entrySet()) {
			if (nameValue.getKey() != null && !nameValue.getKey().equals("")) {
				exprContext.setVariable(nameValue.getKey(), nameValue.getValue());
			}
			exprContext.setVariable("" + index++, nameValue.getValue());
		}

		// evaluate properties
		Map<Property, String> propertyValues = new HashMap<Property, String>();
		for (Entry<Property, SExpr> entry : propertyValueExpressions.entrySet()) {
			propertyValues.put(entry.getKey(), entry.getValue().evaluate(exprContext));
		}

		// determine match value
		String matchValue = orElse(propertyValues.get(matchProperty), "");
		
		// determine data language
		String language = languageExpression.evaluate(exprContext).trim();
		if (language.equals("")) language = null;
		
		// determine output channel
		String outputChannelName = outputChannelExpression.evaluate(exprContext).trim();
		OutputChannel outputChannel = null;
		if (!outputChannelName.equals("")) {
			for (OutputChannel oc : model.getCatalog().getOutputChannels()) {
				if (oc.getName().equals(outputChannelName)) {
					outputChannel = oc;
					break;
				}
			}
		}
		
		// string converter
		PropertyStringConverter stringConverter = new PropertyStringConverter();
		if (language != null) {
			stringConverter.setLocale(new Locale(language));
		}
		
		// find categories for this record
		Set<ItemModel> categories = new LinkedHashSet<ItemModel>();
		for (SExpr expr : categoryExpressions) {
			String categoryName = expr.evaluate(exprContext).trim();
			if (!categoryName.equals("")) {
				Category category = cache.get(categoryName);
				if (category == null) {
					throw new Exception("Category not found: " + categoryName);
				}
				categories.add(model.getItem(category.getId()));
			}
		}

		// no category set -> skip product
		if (categories.isEmpty()) {
			log.println("No category specified, skipping product with " + matchPropertyLabel + " " + matchValue);
		}
		log.println("Importing product with " + matchPropertyLabel + " " + matchValue);

		Iterable<ItemModel> items = concat(transform(categories, new Function<ItemModel, Set<ItemModel>>() {
			public Set<ItemModel> apply(ItemModel category) {
				return category.getChildExtent();
			}
		}));
		
		// find an existing product
		Set<Product> visited = new HashSet<Product>();
		Product matchedProduct = null;
		for (ItemModel item : items) {
			if (item.getEntity() instanceof Product) {
				Product product = (Product) item.getEntity();
				if (!visited.contains(product)) {
					visited.add(product);
					PropertyModel property = item.findProperty(importDefinition.getMatchProperty().getId(), true);
					if (property != null) { 
						Object typedValue = property.getValues(null, outputChannel).get(language);
						if (typedValue != null) {
							Object typedMatchValue = stringConverter.fromString(property.getEntity().getType(), matchValue);
							if (equal(typedValue, typedMatchValue)) {
								matchedProduct = product;
								break;
							}
						}
					}
				}
			}
		}
		
		// find or create product
		ItemModel product = null;
		if (matchedProduct != null) {
			product = model.getItem(matchedProduct.getId());
		} else {
			log.println("No existing product found, will create a new one");
			if (!dryRun) {
				product = model.createProduct();
			}
		}
		
		// set item parents
		if (!dryRun) {
			product.setParents(categories);
		}
		
		// set property values
		for (Entry<Property, String> value : propertyValues.entrySet()) {
			PropertyModel property = null;
			if (product != null) {
				property = product.findProperty(value.getKey(), true);
			} else {
				for (ItemModel category : categories) {
					property = category.findProperty(value.getKey(), true);
					if (property != null) {
						break;
					}
				}
			}
			if (property == null) {
				throw new Exception("Property not found: " + propertyLabel(value.getKey(), null));
			}
			// remove value
			if (value.getValue().trim().equals("")) {
				if (!dryRun) {
					property.removeValue(null, outputChannel, language);
				}
			} 
			// set value
			else {
				Object typedValue = stringConverter.fromString(property.getEntity().getType(), value.getValue());
				if (!dryRun) {
					property.setValue(null, outputChannel, language, typedValue);
				}
			}
		}
	}
	
	private static String propertyLabel(Property matchProperty, String string) throws SExprParseException {
		for (Label label : matchProperty.getLabels()) {
			if (label.getLanguage() == null) {
				return label.getLabel();
			}
		}
		return "";
	}

	private static Property checkMatchProperty(ImportDefinition importDefinition) throws Exception {
		if (importDefinition.getMatchProperty() == null) {
			throw new Exception("No match property specified");
		}
		boolean found = false;
		for (ImportProperty ip : importDefinition.getProperties()) {
			if (ip.getProperty().equals(importDefinition.getMatchProperty())) {
				found = true;
				break;
			}
		}
		if (!found) {
			throw new Exception("No rule definined for match property: " + propertyLabel(importDefinition.getMatchProperty(), null));
		}
		return importDefinition.getMatchProperty();
	}

	private static Map<Property, SExpr> parsePropertyValueExpressions(ImportDefinition importDefinition, SExprParser exprParser) throws SExprParseException {
		Map<Property, SExpr> propertyValueExpressions = new HashMap<Property, SExpr>();
		for (ImportProperty ip : unique(importDefinition.getProperties())) {
			SExpr expr = exprParser.parse(orElse(ip.getValueExpression(), ""));
			propertyValueExpressions.put(ip.getProperty(), expr);
		}
		return propertyValueExpressions;
	}
	
	private static List<SExpr> parseCategoryExpressions(ImportDefinition importDefinition, SExprParser exprParser) throws SExprParseException {
		List<SExpr> expressions = new ArrayList<SExpr>();
		for (ImportCategory ic : unique(importDefinition.getCategories())) {
			SExpr expr = exprParser.parse(orElse(ic.getCategoryExpression(), ""));
			expressions.add(expr);
		}
		return expressions;
	}
	
	private static List<String> parseLine(String line, String separators) {
		List<String> result = new ArrayList<String>();
		StringBuilder field = new StringBuilder();
		int len = line.length();
		for (int pos = 0; pos < len; pos++) {
			char c = line.charAt(pos);
			if (c == '\'' || c == '\"') {
				char sep = c;
				for (pos++; pos < len; pos++) {
					c = line.charAt(pos);
					if (c == sep) {
						break;
					}
				}
			} else if (separators.indexOf(c) != -1) {
				result.add(field.toString().trim());
				field.setLength(0);
			} else {
				field.append(c);
			}
		}
		result.add(field.toString());
		return result;
	}
	
	private class CategoryCache {
		private Map<String, Category> cache = new HashMap<String, Category>();
		public Category get(String name) {
			Category category = cache.get(name);
			if (category == null) {
				category = dao.getCategoryByName(model.getCatalog(), name);
				cache.put(name, category);
			}
			return category;
		}
	}
}

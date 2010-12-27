package claro.catalog.impl.importing;

import static easyenterprise.lib.util.CollectionUtil.unique;
import static easyenterprise.lib.util.MathUtil.orZero;
import static java.lang.Math.max;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import claro.catalog.CatalogDao;
import claro.catalog.CatalogModelService;
import claro.catalog.command.importing.PerformImport;
import claro.catalog.command.importing.PerformImport.Result;
import claro.catalog.command.items.FindItems;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.Property;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportDefinition;
import claro.jpa.importing.ImportProperty;
import claro.jpa.importing.TabularImportDefinition;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.jpa.JpaService;
import easyenterprise.lib.sexpr.DefaultContext;
import easyenterprise.lib.sexpr.SExpr;
import easyenterprise.lib.sexpr.SExprParser;

@SuppressWarnings("serial")
public class PerformImportImpl extends PerformImport implements CommandImpl<Result>{

	private CatalogDao dao;
	private CatalogModel model;
	private CategoryCache cache;
	private SExprParser exprParser;
	private DefaultContext exprContext;
	
	private ImportDefinition importDefinition;
	
	@Override
	public Result execute() throws CommandException {
		checkValid();
		this.dao = new CatalogDao(JpaService.getEntityManager());
		this.model = CatalogModelService.getCatalogModel(catalogId);
		this.cache = new CategoryCache();
		this.exprParser = new SExprParser();
		this.exprContext = new DefaultContext();
		Result result = new Result();

		
		// fetch import definition
		importDefinition = dao.getImportDefinitionById(importDefinitionId);
		
		// increase sequence number
		Integer sequence = orZero(importDefinition.getSequenceNr()) + 1;
		importDefinition.setSequenceNr(sequence);
		exprContext.variables.put("sequence", sequence.toString());

		try {
			
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
				// TODO property name
				throw new Exception("No rule definined for match property: " + importDefinition.getMatchProperty());
			}
			
			
			if (importDefinition instanceof TabularImportDefinition) {
				importTabularData((TabularImportDefinition) importDefinition);
			}
		} catch (CommandException e) {
			// TODO Auto-generated catch block
		} catch (Exception e) {
			throw new CommandException(e);
		}
		
		return result;
	}

	private void importTabularData(TabularImportDefinition importDefinition) throws Exception {
		
		// evaluate import url
		String urlExpr = importUrl != null ? importUrl : importDefinition.getImportUrl();
		String url = exprParser.parse(urlExpr).evaluate(exprContext);

		// separator chars
		String separators = importDefinition.getSeparatorChars();
		if (separators == null) {
			separators = ",;\t";
		}
		
		// determine structure of import file
		Charset charset = Charset.forName(importDefinition.getCharset() != null ? importDefinition.getCharset() : "UTF-8");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream(), charset));
		List<String> headerFields = Collections.emptyList();
		if (importDefinition.getHeaderLine()) {
			String line = reader.readLine();
			if (line == null) throw new PerformImportException("Missing header-line in file " + url);
			headerFields = parseLine(line, separators);
		}
		int maxFields = headerFields.size();
		System.out.println("Header: " + headerFields);
		
		// loop over lines
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			
			// parse fields
			List<String> fields = parseLine(line, separators);
			maxFields = max(maxFields, fields.size());
			System.out.println("Line: " + fields);
			
			// add a context variable for each field
			for (int i = 0; i < maxFields; i++) {
				String value = i < fields.size() ? fields.get(i) : "";
				exprContext.setVariable("" + i, value);
				if (i < headerFields.size()) {
					exprContext.setVariable(headerFields.get(i), value);
				}
			}
			

			// match existing product
		}
		
	}
	
	private void importRecord(Map<String,String> nameValues) throws Exception {
		
		// create context with values
		int index = 0;
		DefaultContext context = new DefaultContext(this.exprContext);
		for (Entry<String, String> nameValue : nameValues.entrySet()) {
			context.variables.put(nameValue.getKey(), nameValue.getValue());
			context.variables.put("" + index++, nameValue.getValue());
		}
		
		// find categories for this line
		Set<Category> categories = new HashSet<Category>();
		List<Long> categoryIds = new ArrayList<Long>();
		for (ImportCategory ic : unique(importDefinition.getCategories())) {
			SExpr expr = exprParser.parse(ic.getExpression());
			String categoryName = expr.evaluate(exprContext);
			Category category = cache.get(categoryName);
			categories.add(category);
			categoryIds.add(category.getId());
		}
		
		// evaluate properties
		Map<Property, String> propertyValues = new HashMap<Property, String>();
		for (ImportProperty ip : unique(importDefinition.getProperties())) {
			SExpr expr = exprParser.parse(ip.getExpression());
			String value = expr.evaluate(exprContext);
			propertyValues.put(ip.getProperty(), value);
		}
		
		// find existing products in existing categories
		FindItems findItems = new FindItems();
		findItems.catalogId = catalogId;
		findItems.categoryIds = categoryIds;
		findItems.filter = importDefinition.getMatchProperty() + ":" + "";
	}
	
	private List<String> parseLine(String line, String separators) {
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
				result.add(field.toString());
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
		
		Category find(ItemModel item, String name) {
			if (item.getEntity() instanceof Category) {
				for (PropertyModel property : item.getProperties()) {
				}
			}
			return null;
		}
	}
}

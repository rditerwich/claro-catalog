package claro.catalog.impl.importing;

import static claro.catalog.util.CatalogModelUtil.propertyLabel;
import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Iterables.concat;
import static easyenterprise.lib.util.CollectionUtil.unique;
import static easyenterprise.lib.util.FileUtil.copy;
import static easyenterprise.lib.util.FileUtil.createTempDir;
import static easyenterprise.lib.util.ObjectUtil.orElse;
import static easyenterprise.lib.util.StringUtil.afterLast;
import static easyenterprise.lib.util.ZipUtil.unzip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.fileupload.FileItem;

import claro.catalog.CatalogDao;
import claro.catalog.CatalogModelService;
import claro.catalog.command.importing.PerformImport;
import claro.catalog.command.importing.PerformImport.Result;
import claro.catalog.command.importing.PerformImportException;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.catalog.util.PropertyStringConverter;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Product;
import claro.jpa.catalog.Property;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportFileFormat;
import claro.jpa.importing.ImportJobResult;
import claro.jpa.importing.ImportProducts;
import claro.jpa.importing.ImportProperty;
import claro.jpa.importing.ImportRules;
import claro.jpa.importing.ImportSource;
import claro.jpa.importing.TabularFileFormat;
import claro.jpa.jobs.JobResult;

import com.google.common.base.Function;

import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.Cloner;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.jpa.JpaService;
import easyenterprise.lib.gwt.server.UploadServlet;
import easyenterprise.lib.sexpr.DefaultContext;
import easyenterprise.lib.sexpr.SExpr;
import easyenterprise.lib.sexpr.SExprParseException;
import easyenterprise.lib.sexpr.SExprParser;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.GetComparable;
import easyenterprise.lib.util.Tuple;

@SuppressWarnings("serial")
public class PerformImportImpl extends PerformImport implements CommandImpl<Result>{

	private static View view = new BasicView();

	private CatalogDao dao;
	private CatalogModel model;
	private CategoryCache cache;
	
	private ImportSource importSource;
	private Property matchProperty;
	private String matchPropertyLabel;
	private SExpr languageExpression;
	private Map<Property, SExpr> propertyValueExpressions;
	private PrintWriter log;
	private SExpr outputChannelExpression;
	private List<SExpr> categoryExpressions;

	private ImportProducts importProducts;

	private DefaultContext exprContext;

	private ImportRules rules;
	
	@Override
	public Result execute() throws CommandException {
		checkValid();
		this.dao = new CatalogDao(JpaService.getEntityManager());
		this.model = CatalogModelService.getCatalogModel(catalogId);
		this.cache = new CategoryCache();
		
		List<JobResult> results = new ArrayList<JobResult>();
		
		try {
				
			// fetch import definition
			importSource = dao.getImportSourceById(importSourceId);
			
			// process all import files
			for (Tuple<File, String> importFile : getImportFiles()) {
				
				// create a job
				ImportJobResult jobResult = new ImportJobResult();
				jobResult.setSuccess(false);
				jobResult.setStartTime(new Timestamp(System.currentTimeMillis()));
				jobResult.setEndTime(new Timestamp(System.currentTimeMillis()));
				jobResult.setLog("");
				jobResult.setImportSource(importSource);
				jobResult.setJob(importSource.getJob());
				jobResult.setUrl(importFile.getSecond());
				results.add(jobResult);
				if (!dryRun) {
					importSource.getJob().getResults().add(jobResult);
				}
				
				// start logging
				StringWriter stringWriter = new StringWriter();
				log = new PrintWriter(stringWriter);

				log.println("Importing url " + importFile.getSecond());

				// create an expression context
				exprContext = new DefaultContext();

				// process all rules
				try {
					
					// sort rules by relative url
					List<ImportRules> sortedRules = CollectionUtil.sort(importSource.getRules(), new GetComparable<ImportRules>() {
						public Comparable<?> getComparable(ImportRules object) {
							return object.getRelativeUrl();
						}
					});
					
					// take first if not multi-file
					if (!importSource.getMultiFileImport() && sortedRules.size() > 1) {
						sortedRules = Collections.singletonList(sortedRules.get(0));
					}
					
					for (ImportRules rules : importSource.getRules()) {
						importRules(importFile.getFirst(), rules);
					}
					jobResult.setSuccess(true);
				} catch (CommandException e) {
					log.append("IMPORT FAILED:" + e.getMessage());
					jobResult.setSuccess(false);
					break;
				} catch (Exception e) {
					StringWriter ewriter = new StringWriter();
					e.printStackTrace(new PrintWriter(ewriter));
					log.append("IMPORT ERROR:" + ewriter.getBuffer().toString());
					jobResult.setSuccess(false);
					break;
				} finally {
					jobResult.setLog(stringWriter.getBuffer().toString());
					jobResult.setEndTime(new Timestamp(System.currentTimeMillis()));
				}
					// calculate job statistics 
				if (!dryRun) {
					jobResult.getJob().setLastSuccess(jobResult.getSuccess());
					jobResult.getJob().setLastTime(jobResult.getEndTime());
					importSource.setLastImportedUrl(importFile.getSecond());

					List<JobResult> lastResults = dao.getLastJobResults(jobResult.getJob(), 5);
					int successCount = 0;
					for (JobResult result : lastResults) {
						if (result.getSuccess()) {
							successCount++;
						}
					}
					jobResult.getJob().setHealthPerc(successCount * 100 / results.size() / 5);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new CommandException(e);
		}
		Result result = new Result();
		result.jobResults = Cloner.clone(results, view);
		return result;
	}

	/**
	 * Locates import files (one per rule)
	 * @throws PerformImportException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private List<Tuple<File, String>> getImportFiles() throws PerformImportException, IOException {

		// parse wildcard
		String urlBase = orElse(importSource.getImportUrl(), "").replace('\\', '/');
		String urlEnd = "";
		int wildcartIndex = importSource.getImportUrl().indexOf('*');
		if (wildcartIndex >= 0) {
			urlEnd = urlBase.substring(wildcartIndex + 1);
			urlBase = urlBase.substring(0, wildcartIndex);
		}

		// assume file-location
		if (!urlBase.contains(":")) {
			urlBase = "file://" + urlBase;
		}

		// uploaded file?
		if (uploadFieldName != null) {
			FileItem fileItem = UploadServlet.getUploadedFile(uploadFieldName);
			if (fileItem != null) {
				// copy and unzip file 
				File tempFile = copyToFile(fileItem.getInputStream(), new File(fileItem.getName()).getName());
				return Collections.singletonList(Tuple.create(tryUnzip(tempFile), fileItem.getName()));
			}
		}

		String lastUrl = importSource.getLastImportedUrl();
		
		// sequence?
		if (importSource.getSequentialUrl()) {
			long index = 0;
			if (lastUrl != null) {
				if (lastUrl.startsWith(urlBase) && lastUrl.endsWith(urlEnd)) {
					String indexString = lastUrl.substring(urlBase.length(), lastUrl.length() - urlBase.length() - urlEnd.length());
					try {
						index = Long.parseLong(indexString) + 1;
					} catch (NumberFormatException e) {
					}
				}
			}
			String url = urlBase + index + urlEnd;
			
			// copy and unzip file 
			File tempFile = copyToFile(new URL(url).openStream(), afterLast(url, '/', url));
			return Collections.singletonList(Tuple.create(tryUnzip(tempFile), url));
		} 
		
		// ordered?
		if (importSource.getOrderedUrl()) {
			// must be file protocol in order to scan
			if (!urlBase.startsWith("file://")) {
				throw new PerformImportException("Imports with ordered urls should have a file-based import url.");
			}
		}

		// file based scans allow for a wildcard
		if (urlBase.startsWith("file://")) {
			List<Tuple<File, String>> result = new ArrayList<Tuple<File,String>>();
			File dir = new File(urlBase);
			if (!dir.isDirectory()) {
				dir = dir.getParentFile();
			}
			File[] files = dir != null ? dir.listFiles() : new File[0];
			Arrays.sort(files);
			for (File file : files) {
				String url = file.toURI().toString();
				if (url.startsWith(urlBase) && url.endsWith(urlEnd)) {
					// check ordered url restriction
					if (!importSource.getOrderedUrl() || lastUrl == null || lastUrl.compareTo(url) < 0) {
						result.add(Tuple.create(file, url));
					}
				}
			}
			return result;
		}
		
		// copy and unzip file 
		String url = urlBase + urlEnd;
		File tempFile = copyToFile(new URL(url).openStream(), afterLast(url, '/', url));
		return Collections.singletonList(Tuple.create(tryUnzip(tempFile), url));
	}
	
	private void importRules(File importFile, ImportRules rules) throws Exception {

		// relative file
		if (importSource.getMultiFileImport() && rules.getRelativeUrl() != null) {
			if (!importFile.isDirectory()) importFile = importFile.getParentFile();
			importFile = new File(importFile, rules.getRelativeUrl());
			if (importFile.exists()) {
				log.println("Processing nested file " + rules.getRelativeUrl() + "...");
			} else {
				log.println("Nested file " + rules.getRelativeUrl() + " does not exist, skipping....");
			}
		}
		
		this.rules = rules;
		
		// expressions
		SExprParser exprParser = new SExprParser();
		languageExpression = exprParser.parse(orElse(rules.getLanguageExpression(), ""));
		importProducts = rules.getImportProducts();
		
		if (importProducts != null) {
			// match property
			matchProperty = checkMatchProperty(importProducts);
			matchPropertyLabel = propertyLabel(matchProperty, null, "");
			outputChannelExpression = exprParser.parse(orElse(importProducts.getOutputChannelExpression(), ""));
			propertyValueExpressions = parsePropertyValueExpressions(importProducts, exprParser);
			categoryExpressions = parseCategoryExpressions(importProducts, exprParser);
		}
		
		// file format
		ImportFileFormat fileFormat = rules.getFileFormat();
		if (fileFormat == null) {
			fileFormat = new TabularFileFormat();
		}
		if (fileFormat instanceof TabularFileFormat) {
			importTabularData((TabularFileFormat) fileFormat, importFile);
		} else {
			throw new PerformImportException("Unknown file format: " + fileFormat.getClass().getName());
		}
	}

	private void importTabularData(TabularFileFormat fileFormat, File file) throws Exception {
		
		// separator chars
		String separators = fileFormat.getSeparatorChars();
		if (separators == null) {
			separators = ",";
		}
		
		Map<String, String> nameValues = new HashMap<String, String>();
		
		// determine structure of import file
		Charset charset = Charset.forName(fileFormat.getCharset() != null ? fileFormat.getCharset() : "UTF-8");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
		try {
			List<String> headerFields = Collections.emptyList();
			if (fileFormat.getHeaderLine()) {
				String line = reader.readLine();
				if (line == null) throw new PerformImportException("Missing header-line in file " + file);
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
				importLine(nameValues);
			}
		} finally {
			reader.close();
		}
	}
	
	private void importLine(Map<String,String> nameValues) throws Exception {
		if (importProducts != null) {
			importProduct(nameValues);
		}
	}
	
	private void importProduct(Map<String,String> nameValues) throws Exception {
		
		// create context with values
		int index = 0;
		DefaultContext exprContext = new DefaultContext(this.exprContext);
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
		if (rules.getDefaultCurrency() != null) {
			stringConverter.setDefaultCurrency(rules.getDefaultCurrency());
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
			categories.add(CatalogModelService.getCatalogModel(catalogId).getRootItem());
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
					PropertyModel property = item.findProperty(matchProperty.getId(), true);
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
				throw new Exception("Property not found: " + propertyLabel(value.getKey(), null, ""));
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
	
	private static Property checkMatchProperty(ImportProducts importProducts) throws Exception {
		if (importProducts.getMatchProperty() == null) {
			throw new CommandException("No match property specified");
		}
		boolean found = false;
		for (ImportProperty ip : importProducts.getProperties()) {
			if (ip.getProperty().equals(importProducts.getMatchProperty())) {
				found = true;
				break;
			}
		}
		if (!found) {
			throw new Exception("No rule definined for match property: " + propertyLabel(importProducts.getMatchProperty(), null, ""));
		}
		return importProducts.getMatchProperty();
	}

	private static Map<Property, SExpr> parsePropertyValueExpressions(ImportProducts products, SExprParser exprParser) throws SExprParseException {
		Map<Property, SExpr> propertyValueExpressions = new HashMap<Property, SExpr>();
		for (ImportProperty ip : unique(products.getProperties())) {
			SExpr expr = exprParser.parse(orElse(ip.getValueExpression(), ""));
			propertyValueExpressions.put(ip.getProperty(), expr);
		}
		return propertyValueExpressions;
	}
	
	private static List<SExpr> parseCategoryExpressions(ImportProducts products, SExprParser exprParser) throws SExprParseException {
		List<SExpr> expressions = new ArrayList<SExpr>();
		for (ImportCategory ic : unique(products.getCategories())) {
			SExpr expr = exprParser.parse(orElse(ic.getCategoryExpression(), ""));
			expressions.add(expr);
		}
		return expressions;
	}

	private static File copyToFile(InputStream is, String fileName) throws IOException {
		File tempFile = File.createTempFile("CatalogImport", fileName);
		tempFile.deleteOnExit();
		copy(is, new FileOutputStream(tempFile));
		return tempFile;
	}
	
	private static File tryUnzip(File file) {
		File tempDir;
		try {
			tempDir = createTempDir("CatalogImport", file.getName());
			unzip(new FileInputStream(file), tempDir);
		} catch (Throwable e) {
			return file;
		}
		return tempDir;
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

package claro.catalog.impl.importing;

import static java.lang.Math.max;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import claro.catalog.CatalogDao;
import claro.catalog.CatalogModelService;
import claro.catalog.command.importing.PerformImport;
import claro.catalog.command.importing.PerformImport.Result;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.jpa.catalog.Category;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportDefinition;
import claro.jpa.importing.TabularImportDefinition;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.jpa.JpaService;
import easyenterprise.lib.sexpr.DefaultContext;
import easyenterprise.lib.sexpr.SExpr;
import easyenterprise.lib.sexpr.SExprEvaluationException;
import easyenterprise.lib.sexpr.SExprParseException;
import easyenterprise.lib.sexpr.SExprParser;

public class PerformImportImpl extends PerformImport implements CommandImpl<Result>{

	private CatalogDao dao;
	private CatalogModel model;
	private CategoryCache cache;
	private SExprParser exprParser;
	private DefaultContext exprContext;
	
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
		ImportDefinition importDefinition = dao.getImportDefinitionById(importDefinitionId);
		exprContext.variables.put("sequence", importDefinition.getSequenceNr().toString());

		
		try {
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

		// determine structure of import file
		Charset charset = Charset.forName(importDefinition.getCharset() != null ? importDefinition.getCharset() : "UTF-8");
		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream(), charset));
		List<String> headerFields = Collections.emptyList();
		if (importDefinition.getHeaderLine()) {
			String line = reader.readLine();
			if (line == null) throw new PerformImportException("Missing header-line in file " + url);
			headerFields = parseLine(line);
		}
		int maxFields = headerFields.size();
		
		// loop over lines
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			System.out.println("Reading line: " + line);
			
			// parse fields
			List<String> fields = parseLine(line);
			maxFields = max(maxFields, fields.size());
			
			// add a context variable for each field
			for (int i = 0; i < maxFields; i++) {
				String value = i < fields.size() ? fields.get(i) : "";
				exprContext.setVariable("" + i, value);
				if (i < headerFields.size()) {
					exprContext.setVariable(headerFields.get(i), value);
				}
			}
			
			// find categories for this line
			List<Category> categories = new ArrayList<Category>();
			for (ImportCategory ic : importDefinition.getCategories()) {
				SExpr expr = exprParser.parse(ic.getExpression());
				String categoryName = expr.evaluate(exprContext);
				categories.add(cache.get(categoryName));
			}
			
			// match existing product
		}
		
	}
	
	private List<String> parseLine(String line) {
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
			} else if (c == ',') {
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

package claro.catalog.impl.importing;

import static easyenterprise.lib.command.CommandValidationException.validate;

import java.util.HashSet;
import java.util.Set;

import claro.catalog.CatalogDao;
import claro.catalog.CatalogDaoService;
import claro.catalog.CatalogModelService;
import claro.catalog.command.importing.DeleteImportedData;
import claro.catalog.model.CatalogModel;
import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.PropertyValue;
import claro.jpa.importing.ImportSource;
import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;

public class DeleteImportedDataImpl extends DeleteImportedData implements CommandImpl<DeleteImportedData.Result>{

	private static final long serialVersionUID = 1L;
	static View view = new BasicView("job", "rules/fileFormat", "rules/importProducts/matchProperty", "rules/importProducts/categories/categoryExpression", "rules/importProducts/properties/property", "rules/importProducts/properties/importProducts");

	@Override
	public Result execute() throws CommandException {
		checkValid();
		
		Result result = new Result();
		CatalogDao dao = CatalogDaoService.getCatalogDao();
		ImportSource importSource = dao.getImportSourceById(importSourceId);
		validate(importSource != null);
		Set<PropertyValue> values = new HashSet<PropertyValue>(dao.getPropertyValuesBySource(importSource));
		Set<Item> items = new HashSet<Item>();
		for (PropertyValue value : values) {
			dao.getEntityManager().remove(value);
			items.add(value.getItem());
		}
		
		// remove items with no properties left
		HashSet<Catalog> catalogs = new HashSet<Catalog>();
		for (Item item : items) {
			item.getCatalog();
			boolean hasProperties = false;
			for (PropertyValue value : item.getPropertyValues()) {
				if (values.contains(value)) continue;
				hasProperties = true;
				break;
			}
			if (!hasProperties) {
				CatalogModel catalogModel = CatalogModelService.getCatalogModel(item.getCatalog().getId());
				catalogModel.removeItem(item.getId());
			}
		}
		
		// invalidate catalogs
		for (Catalog catalog : catalogs) {
			CatalogModel catalogModel = CatalogModelService.getCatalogModel(catalog.getId());
			if (catalogModel != null) {
				catalogModel.flushCache();
			}
		}
		return result;
	}

}

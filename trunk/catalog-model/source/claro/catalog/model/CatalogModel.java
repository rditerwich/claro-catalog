package claro.catalog.model;

import java.util.HashMap;
import java.util.Map;

import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyType;

public class CatalogModel {

	final Catalog catalog;
	final Map<Long, ItemModel> items = new HashMap<Long, ItemModel>();
	final Property nameProperty;
	final Property variantProperty;
	final Property articleNumberProperty;
	final Property descriptionProperty;
	final Property imageProperty;
	final Property smallImageProperty;
	final Property priceProperty;
	final Property supplierProperty;
  final Property supplierArticleNumberProperty;

	public static void access(CatalogDao dao, Runnable runnable) {
		CatalogAccess.access(dao, runnable);
	}

	public CatalogModel(Long id) {
		CatalogDao dao = CatalogAccess.getDao();
		this.catalog = dao.findOrCreateCatalog(id);
		Category root = dao.findOrCreateRootCategory(catalog);
		nameProperty = dao.findOrCreateProperty(root, "Name", PropertyType.String);
		variantProperty = dao.findOrCreateProperty(root, "Variant", PropertyType.String);
		articleNumberProperty = dao.findOrCreateProperty(root, "ArticleNumber", PropertyType.String);
		descriptionProperty = dao.findOrCreateProperty(root, "Description", PropertyType.String);
		imageProperty = dao.findOrCreateProperty(root, "Image", PropertyType.Media);
		smallImageProperty = dao.findOrCreateProperty(root, "SmallImage", PropertyType.Media);
		priceProperty = dao.findOrCreateProperty(root, "Price", PropertyType.Money);
		supplierProperty = dao.findOrCreateProperty(root, "Supplier", PropertyType.String);
		supplierArticleNumberProperty = dao.findOrCreateProperty(root, "SupplierArticleNumber", PropertyType.String);
  }
	
	public synchronized ItemModel getItem(Long id) {
		ItemModel itemData = items.get(id);
		if (itemData == null) {
			itemData = new ItemModel(this, id);
			items.put(id, itemData);
		}
		return itemData;
	}
	
	void invalidate(ItemModel... items) {
		for (ItemModel item : items) {
			CatalogAccess.getInvalidItems().add(item);
		}
	}
	
	void invalidate(Iterable<ItemModel> items) {
		for (ItemModel item : items) {
			CatalogAccess.getInvalidItems().add(item);
		}
	}
}

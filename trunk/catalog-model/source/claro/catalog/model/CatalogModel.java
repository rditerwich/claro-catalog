package claro.catalog.model;

import java.util.HashMap;
import java.util.Map;

import claro.catalog.data.RootProperties;
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

	public static void startOperation(CatalogDao dao) {
		CatalogAccess.startOperation(dao);
	}
	
	public static void endOperation() {
		CatalogAccess.endOperation();
	}

	public CatalogModel(Long id) {
		CatalogDao dao = CatalogAccess.getDao();
		this.catalog = dao.findOrCreateCatalog(id);
		Category root = dao.findOrCreateRootCategory(catalog);
		nameProperty = dao.findOrCreateProperty(root, RootProperties.NAME, PropertyType.String);
		variantProperty = dao.findOrCreateProperty(root, RootProperties.VARIANT, PropertyType.String);
		articleNumberProperty = dao.findOrCreateProperty(root, RootProperties.ARTICLENUMBER, PropertyType.String);
		descriptionProperty = dao.findOrCreateProperty(root, RootProperties.DESCRIPTION, PropertyType.String);
		imageProperty = dao.findOrCreateProperty(root, RootProperties.IMAGE, PropertyType.Media);
		smallImageProperty = dao.findOrCreateProperty(root, RootProperties.SMALLIMAGE, PropertyType.Media);
		priceProperty = dao.findOrCreateProperty(root, RootProperties.PRICE, PropertyType.Money);
		supplierProperty = dao.findOrCreateProperty(root, RootProperties.SUPPLIER, PropertyType.String);
		supplierArticleNumberProperty = dao.findOrCreateProperty(root, RootProperties.SUPPLIER_ARTICLENUMBER, PropertyType.String);
  }
	
	public ItemModel getRootItem() throws ItemNotFoundException {
		return getItem(catalog.getRoot().getId());
	}
	
	public synchronized ItemModel getItem(Long id) throws ItemNotFoundException {
		ItemModel itemData = items.get(id);
		if (itemData == null) {
			throw new ItemNotFoundException(id);
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

package claro.catalog.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import claro.catalog.CatalogDao;
import claro.catalog.data.RootProperties;
import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.Product;
import claro.jpa.catalog.PropertyGroup;
import claro.jpa.catalog.PropertyType;

public class CatalogModel {

	public final Catalog catalog;
	public final PropertyGroup generalPropertyGroup;
	public final PropertyModel nameProperty;
	public final PropertyModel variantProperty;
	public final PropertyModel articleNumberProperty;
	public final PropertyModel descriptionProperty;
	public final PropertyModel imageProperty;
	public final PropertyModel smallImageProperty;
	public final PropertyModel priceProperty;
	public final PropertyModel supplierProperty;
	public final PropertyModel supplierArticleNumberProperty;
	final Map<Long, ItemModel> items = new HashMap<Long, ItemModel>();

	public static void startOperation(CatalogDao dao) {
		CatalogAccess.startOperation(dao);
	}
	
	public static void endOperation() {
		CatalogAccess.endOperation();
	}

	public CatalogModel(Long id) {
		this.catalog = findOrCreateCatalog(id);
		ItemModel root = findOrCreateRootCategory();
		this.generalPropertyGroup = root.findOrCreatePropertyGroup(RootProperties.GENERALGROUP, null);
		this.nameProperty = root.findOrCreateProperty(RootProperties.NAME, null, PropertyType.String, generalPropertyGroup);
		this.variantProperty = root.findOrCreateProperty(RootProperties.VARIANT, null, PropertyType.String, generalPropertyGroup);
		this.articleNumberProperty = root.findOrCreateProperty(RootProperties.ARTICLENUMBER, null, PropertyType.String, generalPropertyGroup);
		this.descriptionProperty = root.findOrCreateProperty(RootProperties.DESCRIPTION, null, PropertyType.String, generalPropertyGroup);
		this.imageProperty = root.findOrCreateProperty(RootProperties.IMAGE, null, PropertyType.Media, generalPropertyGroup);
		this.smallImageProperty = root.findOrCreateProperty(RootProperties.SMALLIMAGE, null, PropertyType.Media, generalPropertyGroup);
		this.priceProperty = root.findOrCreateProperty(RootProperties.PRICE, null, PropertyType.Money, generalPropertyGroup);
		this.supplierProperty = root.findOrCreateProperty(RootProperties.SUPPLIER, null, PropertyType.String, generalPropertyGroup);
		this.supplierArticleNumberProperty = root.findOrCreateProperty(RootProperties.SUPPLIER_ARTICLENUMBER, null, PropertyType.String, generalPropertyGroup);
  }
	
	public Catalog getCatalog() {
		return catalog;
	}
	
	public ItemModel getRootItem() throws ItemNotFoundException {
		return getItem(catalog.getRoot().getId());
	}
	
	public synchronized ItemModel getItem(Long id) throws ItemNotFoundException {
		ItemModel itemData = items.get(id);
		if (itemData == null) {
			Item item = CatalogAccess.getDao().getItem(id);
			if (item != null) {
				itemData = new ItemModel(this, id);
				items.put(id, itemData);
			} else {
				throw new ItemNotFoundException(id);
			}
		}
		return itemData;
	}

	public List<ItemModel> getItems(Collection<Long> ids) throws ItemNotFoundException {
		List<ItemModel> result = new ArrayList<ItemModel>();
		for (Long id : ids) {
			result.add(getItem(id));
		}
		return result;
	}
	
	private Catalog findOrCreateCatalog(Long id) {
		EntityManager em = CatalogAccess.getEntityManager();
		Catalog catalog = em.find(Catalog.class, id);
		if (catalog == null) {
			catalog = new Catalog();
			catalog.setId(id);
			catalog.setName(""); 
			em.persist(catalog);
			em.flush(); // Force id generation, because there is an interdependency between catalog and the root category.
		}
		return catalog;
  }

	private ItemModel findOrCreateRootCategory() {
	  ItemModel root;
	  if (catalog.getRoot() == null) {
	  	root = createCategory();
			catalog.setRoot((Category) root.getEntity());
	  } else {
	  	root = getItem(catalog.getRoot().getId());
	  }
	  return root;
  }	
	
	public ItemModel createProduct() {
		Product product = new Product();
		product.setCatalog(catalog);
		catalog.getItems().add(product);
		CatalogAccess.getDao().getEntityManager().persist(product);
		return getItem(product.getId());
	}
	
	public ItemModel createCategory() {
		Category category = new Category();
		category.setCatalog(catalog);
		catalog.getItems().add(category);
		CatalogAccess.getDao().getEntityManager().persist(category);
		return getItem(category.getId());
	}
	
	void invalidate(ItemModel... items) {
		for (ItemModel item : items) {
			item.doInvalidate();
		}
	}
	
	void invalidate(Iterable<ItemModel> items) {
		if (items != null) {
			for (ItemModel item : items) {
				item.doInvalidate();
			}
		}
	}
}

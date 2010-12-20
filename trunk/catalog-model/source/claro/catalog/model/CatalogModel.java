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
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.PropertyType;
import claro.jpa.catalog.StagingArea;
import easyenterprise.lib.util.Paging;
import easyenterprise.lib.util.SMap;

public class CatalogModel {

	public final Catalog catalog;
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
		this.nameProperty = root.findOrCreateProperty(RootProperties.NAME, null, PropertyType.String);
		this.variantProperty = root.findOrCreateProperty(RootProperties.VARIANT, null, PropertyType.String);
		this.articleNumberProperty = root.findOrCreateProperty(RootProperties.ARTICLENUMBER, null, PropertyType.String);
		this.descriptionProperty = root.findOrCreateProperty(RootProperties.DESCRIPTION, null, PropertyType.String);
		this.imageProperty = root.findOrCreateProperty(RootProperties.IMAGE, null, PropertyType.Media);
		this.smallImageProperty = root.findOrCreateProperty(RootProperties.SMALLIMAGE, null, PropertyType.Media);
		this.priceProperty = root.findOrCreateProperty(RootProperties.PRICE, null, PropertyType.Money);
		this.supplierProperty = root.findOrCreateProperty(RootProperties.SUPPLIER, null, PropertyType.String);
		this.supplierArticleNumberProperty = root.findOrCreateProperty(RootProperties.SUPPLIER_ARTICLENUMBER, null, PropertyType.String);
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
	
	// TODO Extract initial item list (selection through categories).
	// TODO productsOnly -> prods, cats, all
	// TODO sorting.
	public synchronized List<ItemModel> findItems(StagingArea stagingArea, OutputChannel outputChannel, String uiLanguage, String language, String filter, boolean productsOnly, Paging paging) {
		List<ItemModel> result = new ArrayList<ItemModel>(paging.shouldPage()?paging.getPageSize() : 20);
		
		// Split of filter:
		String[] filterCriteria = filter.toLowerCase().split(" ");
		List<String> simpleCriteria = new ArrayList<String>();
		List<PropertyCriterium> propertyCriteria = new ArrayList<PropertyCriterium>();
		for (String criterium : filterCriteria) {
			String[] splitCriterium = criterium.split(":");
			if (splitCriterium.length == 2) {
				if (splitCriterium[1] != null && splitCriterium[1].trim().length() > 0) {
					propertyCriteria.add(new PropertyCriterium(splitCriterium[0], splitCriterium[1]));
				}
			} else {
				if (criterium != null && criterium.trim().length() > 0) {
					simpleCriteria.add(criterium);
				}
			}
		}
		
		// For each item, obtain model, and filter.
		int offset = 0;
		int results = 0;
		for (Item item : CatalogAccess.getDao().findItems(catalog, outputChannel, productsOnly)) {
			// Do we have enough results already?
			if (paging.shouldPage() && results >= paging.getPageSize()) {
				break;
			}

			// Obtain model
			ItemModel itemModel = getItem(item.getId());

			// add unfiltered items
			if (acceptItem(itemModel, simpleCriteria, propertyCriteria, stagingArea, outputChannel, uiLanguage, language) 
			&& (!paging.shouldPage() || ++offset > paging.getPageStart())) {
				result.add(itemModel);
				results++;
			}
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
	
	private boolean acceptItem(ItemModel item, List<String> simpleCriteria, List<PropertyCriterium> propertyCriteria, StagingArea stagingArea, OutputChannel outputChannel, String uiLanguage, String language) {
		List<String> unsatisfiedSimpleCriteria = new ArrayList<String>(simpleCriteria);
		List<PropertyCriterium> unsatisfiedPropertyCriteria = new ArrayList<PropertyCriterium>(propertyCriteria);
		for (PropertyModel propertyModel : item.getPropertyExtent()) {
			
			// Obtain value for property:
			SMap<String, Object> effectiveValues = propertyModel.getEffectiveValues(stagingArea, outputChannel);
			Object objectValue = effectiveValues.tryGet(language, null);
			if (objectValue != null) {
				String value = objectValue.toString().toLowerCase();

				// Try simple criteria
				for (String criterium : new ArrayList<String>(unsatisfiedSimpleCriteria)) {
					if (value.contains(criterium)) {
						unsatisfiedSimpleCriteria.remove(criterium);
					}
				}
				
				// try property criteria:
				String propertyLabel = propertyModel.getPropertyInfo().labels.tryGet(uiLanguage, null);
				if (propertyLabel != null) {
					String lowerPropertyLabel = propertyLabel.toLowerCase();
					for (PropertyCriterium criterium : new ArrayList<PropertyCriterium>(unsatisfiedPropertyCriteria)) {
						if (lowerPropertyLabel.contains(criterium.property)) {
							if (value.contains(criterium.value)) {
								unsatisfiedPropertyCriteria.remove(criterium);
							}
						}
					}
				}
			}
			
			// Early out:
			if (unsatisfiedSimpleCriteria.isEmpty() && unsatisfiedPropertyCriteria.isEmpty()) {
				break;
			}
		}
		
		return unsatisfiedSimpleCriteria.isEmpty() && unsatisfiedPropertyCriteria.isEmpty();
	}

	public ItemModel createCategory() {
		Category category = new Category();
		category.setCatalog(catalog);
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
	
	private class PropertyCriterium {
		String property;
		String value;
		public PropertyCriterium(String property, String value) {
			this.property = property;
			this.value = value;
		}
	}
}

package claro.catalog.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import claro.catalog.CatalogDao;
import claro.catalog.data.RootProperties;
import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyType;
import claro.jpa.catalog.PropertyValue;
import claro.jpa.catalog.StagingArea;
import easyenterprise.lib.util.Paging;
import easyenterprise.lib.util.SMap;

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
	
	private class PropertyCriterium {
		String property;
		String value;
		public PropertyCriterium(String property, String value) {
			this.property = property;
			this.value = value;
		}
	}
}

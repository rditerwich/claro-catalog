package claro.catalog.impl.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;

import claro.catalog.CatalogModelService;
import claro.catalog.command.items.FindItems;
import claro.catalog.command.items.ItemType;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Product;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.StagingArea;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.util.SMap;

public class FindItemsImpl extends FindItems implements CommandImpl<FindItems.Result> {

	private static final long serialVersionUID = 1L;
	
	// Parameter entities
	private StagingArea stagingArea;
	private OutputChannel outputChannel;
	private CatalogModel catalogModel;
	private List<Property> orderBy;
	private List<Category> categories;

	@Override
	public FindItems.Result execute() throws CommandException {
		FindItems.Result result = new FindItems.Result();
		
		catalogModel = CatalogModelService.getCatalogModel(catalogId);
		EntityManager entityManager = catalogModel.dao.getEntityManager();
		
		// Initialize parameter entities:
		stagingArea = null;
		if (stagingAreaId != null) {  
			stagingArea = entityManager.find(StagingArea.class, stagingAreaId);
			if (stagingArea == null) {
				throw new CommandException("Area not found");
			}
		}

		outputChannel = null;
		if (outputChannelId != null) {
			outputChannel = entityManager.find(OutputChannel.class, outputChannelId);
			if (outputChannel == null) {
				throw new CommandException("Channel not found");
			}
		}


		if (categoryIds != null) {
			categories = new ArrayList<Category>();
			for (Long categoryId : categoryIds) {
				Category category = entityManager.find(Category.class, categoryId);
				if (category != null) {
					categories.add(category);
				} else {
					throw new CommandException("Category with id " + categoryId + " not found");
				}
			}
		}

		if (orderByIds != null) {
			orderBy = new ArrayList<Property>();
			for (Long propertyId : orderByIds) {
				Property property = entityManager.find(Property.class, propertyId);
				if (property != null) {
					orderBy.add(property);
				} else {
					throw new CommandException("Property with id " + propertyId + " not found");
				}
			}
		}
		
		// Compile product list:
		List<ItemModel> productCandidates = findItems();
		
		// Collect effective values:
		result.items = SMap.empty();
		for (ItemModel candidate : productCandidates) {
			SMap<PropertyInfo, SMap<String, Object>> propertyValues = ItemUtil.effectivePropertyValues(candidate, stagingArea, outputChannel);
			result.items = result.items.add(candidate.getItemId(), propertyValues);
		}
		
		return result;
	}


	public List<ItemModel> findItems() {
		Set<ItemModel> candidates = findCategoryItems(categories);
		
		candidates = filterByClass(candidates);
		
		List<ItemModel> result = filterItems(candidates);
		
		if (orderBy != null) {
			Collections.sort(result, new ItemOrderComparator(orderBy, stagingArea, outputChannel, language));
		}
		
		if (paging.shouldPage()) {
			return result.subList(paging.getPageStart(), paging.getPageStart() + paging.getPageSize());
		}
		
		return result;
		
	}
	
	
	
	private Set<ItemModel> filterByClass(Set<ItemModel> candidates) {
		
		// No filtering necessary if all items are requested:
		if (resultType == ItemType.item) {
			return candidates;
		}
		
		Set<ItemModel> result = new LinkedHashSet<ItemModel>();

		Class<? extends Item> resultClass = resultClass();
		for (ItemModel item : candidates) {
			if (item.getItemClass().equals(resultClass)) {
				result.add(item);
			}
		}
		
		return result;
	}


	private Set<ItemModel> findCategoryItems(List<Category> categories) {
		Set<ItemModel> result = new LinkedHashSet<ItemModel>();
		if (categories == null || categories.isEmpty()) {
			return catalogModel.getRootItem().getChildExtent();
		}

		boolean first = true;
		// TODO More efficiently?
		for (Category category : categories) {
			ItemModel categoryModel = catalogModel.getItem(category.getId());
			if (first) {
				result.addAll(categoryModel.getChildExtent());
			} else {
				result.retainAll(categoryModel.getChildExtent());
			}
		}
		
		return result;
	}
	
	private Class<? extends Item> resultClass() {
		switch (resultType) {
		case catagory: return Category.class;
		case product: return Product.class;
		default: return Item.class;
		}
	}
	
	private List<ItemModel> filterItems(Set<ItemModel> candidates) {
		List<ItemModel> result = new ArrayList<ItemModel>();
		
		if (filter != null) {
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
			for (ItemModel itemModel : candidates) {
				// add unfiltered items
				if (acceptItem(itemModel, simpleCriteria, propertyCriteria)) {
					result.add(itemModel);
				}
			}
		} else {
			result.addAll(candidates);
		}
		
		return result;
	}
	
	
	private boolean acceptItem(ItemModel item, List<String> simpleCriteria, List<PropertyCriterium> propertyCriteria) {
		List<String> unsatisfiedSimpleCriteria = new ArrayList<String>(simpleCriteria);
		List<PropertyCriterium> unsatisfiedPropertyCriteria = new ArrayList<PropertyCriterium>(propertyCriteria);
		for (Entry<PropertyGroupInfo, PropertyModel> property : item.getPropertyExtent()) {
			
			// Obtain value for property:
			SMap<String, Object> effectiveValues = property.getValue().getEffectiveValues(stagingArea, outputChannel);
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
				String propertyLabel = property.getValue().getPropertyInfo().labels.tryGet(uiLanguage, null);
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


	
	
	private class PropertyCriterium {
		String property;
		String value;
		public PropertyCriterium(String property, String value) {
			this.property = property;
			this.value = value;
		}
	}

}

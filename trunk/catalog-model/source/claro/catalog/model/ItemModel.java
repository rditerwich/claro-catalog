package claro.catalog.model;

import static com.google.common.base.Objects.equal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import claro.catalog.CatalogDao;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.EnumValue;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.Label;
import claro.jpa.catalog.ParentChild;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyGroup;
import claro.jpa.catalog.PropertyGroupAssignment;
import claro.jpa.catalog.PropertyType;
import claro.jpa.catalog.PropertyValue;

import com.google.common.collect.ImmutableSet;

import easyenterprise.lib.command.jpa.JpaService;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.SMap;

public class ItemModel {

	final CatalogModel catalog;
	final Long itemId;
	private Class<? extends Item> itemClass;
	private Set<ItemModel> parents;
	private Set<ItemModel> parentExtent;
	private Set<ItemModel> children;
	private Set<ItemModel> childExtent;
	private SMap<PropertyGroupInfo, PropertyModel> properties;
	private SMap<PropertyGroupInfo, PropertyModel> propertyExtent;
	private Set<PropertyModel> danglingProperties;
	
	ItemModel(CatalogModel catalog, Long id) {
		this.catalog = catalog;
		this.itemId = id;
  }

	public Long getItemId() {
		return itemId;
	}
	
	public Class<? extends Item> getItemClass() {
		synchronized (catalog) {
			if (this.itemClass == null) {
				this.itemClass = getEntity().getClass();
			}
			return this.itemClass;
		}
	}

	/**
	 * Returns the parents of this item, in order (linked hash map).
	 * @return
	 */
	public Set<ItemModel> getParents() {
		synchronized (catalog) {
			if (parents == null) {
				LinkedHashSet<ItemModel> parents = new LinkedHashSet<ItemModel>();
				List<ParentChild> parentChildren = new ArrayList<ParentChild>(getEntity().getParents());
				Collections.sort(parentChildren, new Comparator<ParentChild>() {
					public int compare(ParentChild o1, ParentChild o2) {
						int i1 = o1.getIndex() == null ? 0 : o1.getIndex();
						int i2 = o2.getIndex() == null ? 0 : o2.getIndex();
						return i1 < i2 ? -1 : (i1 > i2 ? 1 : 0);
					}
				});
				for (ParentChild parentChild : parentChildren) {
					if (parentChild.getParent() != null && !equal(parentChild.getParent().getId(), itemId)) {
						ItemModel parentItem = catalog.getItem(parentChild.getParent().getId());
						// prevent cycles
						if (!parentItem.getParentExtent().contains(this)) {
							parents.add(parentItem);
						}
					}
				}
				this.parents = ImmutableSet.copyOf(parents);
			}
			return parents;
		}
	}
	
	public void setParents(Collection<ItemModel> parents) {
		List<Item> items = new ArrayList<Item>();
		for (ItemModel parent : parents) {
			items.add(parent.getEntity());
		}
		CatalogDao dao = CatalogDao.get();
		if (dao.setItemParents(getEntity(), items)) {
			Set<ItemModel> invalidItems = new HashSet<ItemModel>();
			invalidItems.addAll(getChildExtent());
			invalidItems.add(this);
			invalidItems.addAll(getParentExtent());
			for (ItemModel parent : parents) {
				invalidItems.add(parent);
				invalidItems.addAll(parent.getParentExtent());
			}
			catalog.invalidate(invalidItems);
		}
		
	}

	public Item getEntity() {
	  return CatalogDao.get().getItem(itemId);
  }
	
	/**
	 * Returns the parents of this item, and their parents transitively.
	 * The returned set is ordered (linked hash set) according to inheritance rules.
	 * @return
	 */
	
	public Set<ItemModel> getParentExtent() {
		synchronized (catalog) {
			if (parentExtent == null) {
				LinkedHashSet<ItemModel> parentExtent = new LinkedHashSet<ItemModel>();
				LinkedHashSet<ItemModel> seen = new LinkedHashSet<ItemModel>();
				ArrayList<ItemModel> queue = new ArrayList<ItemModel>();
				queue.add(this);
				seen.add(this);
				
				ItemModel item;
				while (queue.size() > 0) {
					item = queue.remove(0);
					
					for (ItemModel parent : item.getParents()) {
						if (seen.add(parent)) {
							parentExtent.add(parent);
							queue.add(parent);
						}
					}
					
				}
				this.parentExtent = ImmutableSet.copyOf(parentExtent);
			}
			return parentExtent;
		}
	}

	/**
	 * Returns the children of this item.
	 * @return
	 */
	public Set<ItemModel> getChildren() {
		synchronized (catalog) {
			if (children == null) {
				HashSet<ItemModel> children = new HashSet<ItemModel>();
				List<ParentChild> parentChildren = new ArrayList<ParentChild>(getEntity().getChildren());
				for (ParentChild parentChild : parentChildren) {
					if (parentChild.getChild() != null && !equal(parentChild.getChild().getId(), itemId)) {
						children.add(catalog.getItem(parentChild.getChild().getId()));
					}
				}
				this.children = ImmutableSet.copyOf(children);
			}
			return children;
		}
	}
	
	/**
	 * Returns the children of this item, in order (linked hash map).
	 * @return
	 */
	public Set<ItemModel> getChildExtent() {
		synchronized (catalog) {
			if (childExtent == null) {
				LinkedHashSet<ItemModel> childExtent = new LinkedHashSet<ItemModel>();
				LinkedHashSet<ItemModel> seen = new LinkedHashSet<ItemModel>();
				ArrayList<ItemModel> queue = new ArrayList<ItemModel>();
				queue.add(this);
				seen.add(this);
				
				ItemModel item;
				while (queue.size() > 0) {
					item = queue.remove(0);
					
					for (ItemModel child : item.getChildren()) {
						if (seen.add(child)) {
							childExtent.add(child);
							queue.add(child);
						}
					}
					
				}
				this.childExtent = ImmutableSet.copyOf(childExtent);
			}
			return childExtent;
		}
	}
	
	public PropertyGroupAssignment findGroupAssignment(Property property) {
		Set<ItemModel> items = new LinkedHashSet<ItemModel>();
		items.add(this);
		items.addAll(getParentExtent());
		for (ItemModel item : items) {
			if (item.getItemClass().equals(Category.class)) {
				Category category = (Category) item.getEntity();
				for (PropertyGroupAssignment groupAssignment : category.getPropertyGroupAssignments()) {
					if (groupAssignment.getProperty().equals(property)) {
						return groupAssignment;
					}
				}
			}
		}
		return null;
	}
	
	private PropertyGroupInfo findGroupInfo(PropertyGroupAssignment groupAssignment) {
		PropertyGroup group;
		if (groupAssignment != null) {
			group = groupAssignment.getPropertyGroup();
		} else {
			group = catalog.generalPropertyGroup;
		}
		
		return catalog.findOrCreatePropertyGroupInfo(group);
	}
	
	public SMap<PropertyGroupInfo, PropertyModel> getProperties() {
		synchronized (catalog) {
			if (properties == null) {
				properties = SMap.empty();
				for (Property property : getEntity().getProperties()) {
					PropertyGroupAssignment groupAssignment = findGroupAssignment(property);
					PropertyModel propertyRoot = PropertyModel.createRoot(property.getId(), false, this, groupAssignment != null? groupAssignment.getCategory().getId() : null);
					properties = properties.add(findGroupInfo(groupAssignment), propertyRoot);
				}
			}
			return properties;
		}		
	}

	/**
	 * Find property with a specific id.
	 * @param propertyId
	 * @param extent look in parent items?
	 * @return Property model or null.
	 */
	public PropertyModel findProperty(Long propertyId, boolean extent) {
		for (Entry<PropertyGroupInfo, PropertyModel> property : extent ? getPropertyExtent() : getProperties()) {
			if (equal(propertyId, property.getValue().getPropertyId())) {
				return property.getValue();
			}
		}
		return null;
	}
	
	/**
	 * Find property with a specific id.
	 * @param propertyId
	 * @param extent look in parent items?
	 * @return Property model or null.
	 */
	public PropertyModel findProperty(Property property, boolean extent) {
		return findProperty(property.getId(), extent);
	}
	
	/**
	 * @param name Name of the property (label)
	 * @param language Language of label to look (use null for default name)
	 * @param extent look in parent items?
	 * @return Property model or null.
	 */
	public PropertyModel findProperty(String propertyLabel, String language, boolean extent) {
		for (Entry<PropertyGroupInfo, PropertyModel> property : extent ? getPropertyExtent() : getProperties()) {
			if (equal(propertyLabel, property.getValue().getPropertyInfo().labels.get(language))) {
				return property.getValue();
			}
		}
		return null;
	}
	
	public PropertyModel findOrCreateProperty(String propertyLabel, String language, PropertyType type, PropertyGroup group) {
		return findOrCreateProperty(propertyLabel, language, type, group, null);
	}
	public PropertyModel findOrCreateProperty(String propertyLabel, String language, PropertyType type, PropertyGroup group, Object initialValue) {
		PropertyModel property = findProperty(propertyLabel, language, false);
		if (property == null) {
			property = createProperty(SMap.create(language, propertyLabel), type, group);
			property.setValue(null, null, language, initialValue);
		}
		return property;
	}
	
	public PropertyModel createProperty(SMap<String, String> initialLabels, PropertyType type, PropertyGroup group) {
		return createProperty(initialLabels, type, false, null, group);
	}
	
	public boolean mergeProperty(PropertyModel property, SMap<String, String> newLabels, PropertyType newType, boolean newIsMany, SMap<Integer, SMap<String, String>> newEnumValues) {
		synchronized (catalog) {
			boolean changed = false;
			
			// Labels
			for (Entry<String, String> newLabel : CollectionUtil.notNull(newLabels)) {
				String existingLabel = property.getPropertyInfo().labels.get(newLabel.getKey());
				if (existingLabel == null) {
					// New label 
					Label label = new Label();
					label.setLanguage(newLabel.getKey());
					label.setLabel(newLabel.getValue());
					label.setProperty(property.getEntity());
					property.getEntity().getLabels().add(label);
					JpaService.getEntityManager().persist(label);

					changed = true;
				} else if (!existingLabel.equals(newLabel.getValue())) {
					// Update label. TODO
					changed = true;
				}
			}
			
			// Type
			if (newType != null && !property.getPropertyInfo().getType().equals(newType)) {
				property.getEntity().setType(newType);
				
				changed = true;
			}
			
			// IsMany
			if (property.getPropertyInfo().isMany != newIsMany) {
				property.getEntity().setIsMany(newIsMany);
				changed = true;
			}
			
			// Enums
			if (newEnumValues != null) {
				// TODO.
			}
			
			return changed;
		}
	}
	public PropertyModel createProperty(SMap<String, String> initialLabels, PropertyType type, boolean isMany, SMap<Integer, SMap<String, String>> enumValues, PropertyGroup group) {
		synchronized (catalog) {
			Property property = new Property();
			
			property.setType(type);
			property.setIsMany(isMany);
			property.setCategoryProperty(false);
			
			for (String lanuage : CollectionUtil.notNull(initialLabels).getKeys()) {
				Label label = new Label();
				label.setLanguage(lanuage);
				label.setLabel(initialLabels.get(lanuage));
				label.setProperty(property);
				property.getLabels().add(label);
			}
			JpaService.getEntityManager().persist(property);
			assert property.getId() != null;
			Item entity = getEntity();
			entity.getProperties().add(property);
			property.setItem(entity);
			
			if (enumValues != null) {
				for (Entry<Integer, SMap<String, String>> enumValue : enumValues) {
					EnumValue newValue = new EnumValue();
					newValue.setProperty(property);
					newValue.setValue(enumValue.getKey());
					
					// Labels
					for (Entry<String, String> langValue : enumValue.getValue()) {
						Label label = new Label();
						label.setLanguage(langValue.getKey());
						label.setLabel(langValue.getValue());
						label.setEnumValue(newValue);
						
					}
					JpaService.getEntityManager().persist(newValue);
				}
			}
			
			if (group != null) {
				assert entity instanceof Category;
				CatalogDao.get().createGroupAssignment(property, group, (Category) entity);
			}
			invalidateChildExtent(true);
			return PropertyModel.createRoot(property.getId(), false, this, group != null? entity.getId() : null);
		}
	}

	public SMap<PropertyGroupInfo, PropertyModel> getPropertyExtent() {
		synchronized (catalog) {
			if (propertyExtent == null) {
				propertyExtent = SMap.empty();
				for (ItemModel parent : getParentExtent()) {
					for (Entry<PropertyGroupInfo, PropertyModel> property : parent.getProperties()) {
						PropertyGroupAssignment groupAssignment = findGroupAssignment(property.getValue().getEntity());
						PropertyModel propertyRoot = PropertyModel.create(property.getValue(), this, groupAssignment != null? groupAssignment.getCategory().getId() : null);
						propertyExtent = propertyExtent.add(findGroupInfo(groupAssignment), propertyRoot);
					}
				}
				
				// Add local properties
				for (Entry<PropertyGroupInfo, PropertyModel> property : getProperties()) {
					PropertyGroupAssignment groupAssignment = findGroupAssignment(property.getValue().getEntity());
					propertyExtent = propertyExtent.add(findGroupInfo(groupAssignment), property.getValue());
				}
			}
			return propertyExtent;
		}		
	}
	
	@Override
	public String toString() {
		return "" + itemId;
	}
	
	public Set<PropertyModel> getDanglingProperties() {
		synchronized (catalog) {
			if (danglingProperties == null) {
				HashSet<PropertyModel> properties = new HashSet<PropertyModel>();
				Set<Property> propertyEntities = PropertyModel.getEntities(getPropertyExtent());
				for (PropertyValue value : getEntity().getPropertyValues()) {
					if (!propertyEntities.contains(value.getProperty())) {
						properties.add(PropertyModel.createRoot(value.getProperty().getId(), true, this, null));  // TODO Possibly a special dangling properties group?
					}
				}
				danglingProperties = ImmutableSet.copyOf(properties);
			}
			return danglingProperties;
		}		
	}
	
	

	public void removeGroupAssignments(SMap<PropertyInfo, PropertyGroupInfo> groupsToRemove) {
		synchronized (catalog) {
			for (Entry<PropertyInfo, PropertyGroupInfo> group : groupsToRemove) {
				PropertyModel property = findProperty(group.getKey().propertyId, true);
				if (property == null) {
					throw new PropertyNotFoundException(group.getKey().propertyId);
				}
				PropertyGroupAssignment groupAssignment = findGroupAssignment(property.getEntity());
				if (groupAssignment != null) {
					// Invalidating child extent of group assignment.  This could be refined to invalidating only the propertyMOdels of the child extent.
					ItemModel groupAssignmentCategory = catalog.getItem(groupAssignment.getCategory().getId());
					groupAssignmentCategory.invalidateChildExtent(true);
					
					CatalogDao.get().removeGroupAssignment(groupAssignment);
					
				}
			}
		}		
	}

	public void assignGroups(SMap<PropertyInfo, PropertyGroupInfo> groupsToSet) {
		// TODO What if properties are already assigned to these groups?  Set anyway? Now: check is made... not assigned again. 
		synchronized (catalog) {
			if (getItemClass() != Category.class) {
				throw new UnsupportedOperationException();
			}
			for (Entry<PropertyInfo, PropertyGroupInfo> group : groupsToSet) {
				PropertyModel property = findProperty(group.getKey().propertyId, true);
				if (property == null) {
					throw new PropertyNotFoundException(group.getKey().propertyId);
				}
				PropertyGroupAssignment groupAssignment = findGroupAssignment(property.getEntity());
				if (groupAssignment == null || !groupAssignment.getPropertyGroup().getId().equals(group.getValue().propertyGroupId)) {
					PropertyGroup groupEntity = JpaService.getEntityManager().find(PropertyGroup.class, group.getValue().propertyGroupId);
					CatalogDao.get().createGroupAssignment(property.getEntity(), groupEntity, (Category)getEntity());
					
					invalidateChildExtent(true);
				}
			}
		}
	}
	
	public void removeProperties(List<Long> propertiesToRemove) {
		synchronized (catalog) {
			for (Long propertyId : propertiesToRemove) {
				PropertyModel property = findProperty(propertyId, false);  // Can only remove my own properties.
				if (property == null) {
					throw new PropertyNotFoundException(propertyId);
				}
				
				CatalogDao.get().removeProperty(property.getEntity());

				invalidateChildExtent(true);
			}
		}
	}
	
	public void setProperties(List<PropertyInfo> propertiesToSet) {
		synchronized (catalog) {
			boolean changed = false;
			for (PropertyInfo propertyInfo : propertiesToSet) {
				// Existing property?
				if (propertyInfo.propertyId != null) {
					PropertyModel property = findProperty(propertyInfo.propertyId, false);  // Can only remove my own properties.
					if (property == null) {
						throw new PropertyNotFoundException(propertyInfo.propertyId);
					}
					
					// Merge property info.
					if (mergeProperty(property, propertyInfo.labels, propertyInfo.getType(), propertyInfo.isMany, propertyInfo.enumValues)) {
						changed = true;
					}
				}
				
				// New property.
				else {
					PropertyModel createdProperty = createProperty(propertyInfo.labels, propertyInfo.getType(), propertyInfo.isMany, propertyInfo.enumValues, null);
					propertyInfo.propertyId = createdProperty.getPropertyId(); // TODO Isn't this a bit too magical??
					changed = true;
				}
				
				
			}
			if (changed) {
				invalidateChildExtent(true);
			}
		}
		
	}
	
	void invalidateChildExtent(boolean includeSelf) {
		// TODO Do we need synchronization?
		
		catalog.invalidate(childExtent);
		if (includeSelf) {
			catalog.invalidate(this);
		}
	}

	/**
	 * Only called from CatalogAccess, do not call!
	 */
	void doInvalidate() {
		synchronized (catalog) {
			parents = null;
			children = null;
			parentExtent = null;
			childExtent = null;
			properties = null;
			propertyExtent = null;
			danglingProperties = null;
		}		
	}
}

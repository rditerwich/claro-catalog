package claro.catalog.model;

import static com.google.common.base.Objects.equal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import claro.catalog.CatalogDao;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.Label;
import claro.jpa.catalog.ParentChild;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyType;
import claro.jpa.catalog.PropertyValue;

import com.google.common.collect.ImmutableSet;

import easyenterprise.lib.util.SMap;

public class ItemModel {

	final CatalogModel catalog;
	final Long itemId;
	private Set<ItemModel> parents;
	private Set<ItemModel> parentExtent;
	private Set<ItemModel> children;
	private Set<ItemModel> childExtent;
	private Set<PropertyModel> properties;
	private Set<PropertyModel> propertyExtent;
	private Set<PropertyModel> danglingProperties;
	
	ItemModel(CatalogModel catalog, Long id) {
		this.catalog = catalog;
		this.itemId = id;
  }

	public Long getItemId() {
		return itemId;
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
		CatalogDao dao = CatalogAccess.getDao();
		if (dao.setItemParents(getEntity(), items)) {
			catalog.invalidate(getChildExtent());
			catalog.invalidate(getParentExtent());
			for (ItemModel parent : parents) {
				catalog.invalidate(parent.getChildExtent());
				catalog.invalidate(parent.getParentExtent());
			}
		}
		
	}

	public Item getEntity() {
	  return CatalogAccess.getDao().getItem(itemId);
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

				getParentExtent(this, parentExtent);
				
				this.parentExtent = ImmutableSet.copyOf(parentExtent);
			}
			return parentExtent;
		}
	}
	
	private void getParentExtent(ItemModel item, LinkedHashSet<ItemModel> parentExtent) {
		for (ItemModel parent : item.getParents()) {
			if (parentExtent.add(parent)) {
				getParentExtent(parent, parentExtent);
			}
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
				LinkedHashSet<ItemModel> children = new LinkedHashSet<ItemModel>();
				getChildExtent(this, children);
				childExtent = ImmutableSet.copyOf(children);
			}
			return childExtent;
		}
  }
	
	private void getChildExtent(ItemModel item, LinkedHashSet<ItemModel> childExtent) {
		for (ItemModel child : item.getChildren()) {
			if (childExtent.add(child)) {
				getChildExtent(child, childExtent);
			}
		}
	}
	
	public Set<PropertyModel> getProperties() {
		synchronized (catalog) {
			if (properties == null) {
				HashSet<PropertyModel> props = new HashSet<PropertyModel>();
				for (Property property : getEntity().getProperties()) {
					props.add(PropertyModel.createRoot(property.getId(), false, this));
				}
				properties = ImmutableSet.copyOf(props);
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
		for (PropertyModel property : extent ? getPropertyExtent() : getProperties()) {
			if (equal(propertyId, property.getPropertyId())) {
				return property;
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
		for (PropertyModel property : extent ? getPropertyExtent() : getProperties()) {
			if (equal(propertyLabel, property.getPropertyInfo().labels.get(language))) {
				return property;
			}
		}
		return null;
	}
	
	public PropertyModel findOrCreateProperty(String propertyLabel, String language, PropertyType type) {
		PropertyModel property = findProperty(propertyLabel, language, false);
		if (property == null) {
			property = createProperty(SMap.create(language, propertyLabel), type);
		}
		return property;
	}
	
	public PropertyModel createProperty(SMap<String, String> initialLabels, PropertyType type) {
		synchronized (catalog) {
			Property property = new Property();
			property.setType(type);
			property.setIsMany(false);
			property.setCategoryProperty(false);
			for (String lanuage : initialLabels.getKeys()) {
				Label label = new Label();
				label.setLanguage(lanuage);
				label.setLabel(initialLabels.get(lanuage));
				label.setProperty(property);
				property.getLabels().add(label);
			}
			CatalogAccess.getDao().getEntityManager().persist(property);
			assert property.getId() != null;
			getEntity().getProperties().add(property);
			property.setItem(getEntity());
			catalog.invalidate(this);
			catalog.invalidate(childExtent);
			return PropertyModel.createRoot(property.getId(), false, this);
		}
	}
	
	public Set<PropertyModel> getPropertyExtent() {
		synchronized (catalog) {
			if (propertyExtent == null) {
				HashSet<PropertyModel> properties = new HashSet<PropertyModel>();
				for (ItemModel parent : getParentExtent()) {
					for (PropertyModel root : parent.getProperties()) {
						properties.add(PropertyModel.create(root, this));
					}
				}
				properties.addAll(getProperties());
				propertyExtent = ImmutableSet.copyOf(properties);
			}
			return propertyExtent;
		}		
	}
	
	public Set<PropertyModel> getDanglingProperties() {
		synchronized (catalog) {
			if (danglingProperties == null) {
				HashSet<PropertyModel> properties = new HashSet<PropertyModel>();
				Set<Property> propertyEntities = PropertyModel.getEntities(getPropertyExtent());
				for (PropertyValue value : getEntity().getPropertyValues()) {
					if (!propertyEntities.contains(value.getProperty())) {
						properties.add(PropertyModel.createRoot(value.getProperty().getId(), true, this));
					}
				}
				danglingProperties = ImmutableSet.copyOf(properties);
			}
			return danglingProperties;
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

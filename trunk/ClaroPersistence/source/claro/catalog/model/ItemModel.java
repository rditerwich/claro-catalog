package claro.catalog.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import claro.jpa.catalog.Item;
import claro.jpa.catalog.ParentChild;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyValue;

public class ItemModel {

	final CatalogModel catalog;
	final CatalogDao dao;
	final Long id;
	private Set<ItemModel> parents;
	private Set<ItemModel> parentExtent;
	private Set<ItemModel> children;
	private Set<ItemModel> childExtent;
	private Set<PropertyModel> properties;
	private Set<PropertyModel> propertyExtent;
	private Set<PropertyModel> danglingProperties;
	
	ItemModel(CatalogModel catalog, Long id) {
		this.catalog = catalog;
		this.dao = catalog.dao;
		this.id = id;
  }
	
	void invalidate() {
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

	/**
	 * Returns the parents of this item, in order (linked hash map).
	 * @return
	 */
	public Set<ItemModel> getParents() {
		synchronized (catalog) {
			if (parents == null) {
				parents = new LinkedHashSet<ItemModel>();
				List<ParentChild> parentChildren = new ArrayList<ParentChild>(getEntity().getParents());
				Collections.sort(parentChildren, new Comparator<ParentChild>() {
					public int compare(ParentChild o1, ParentChild o2) {
						int i1 = o1.getIndex() == null ? 0 : o1.getIndex();
						int i2 = o2.getIndex() == null ? 0 : o2.getIndex();
						return i1 < i2 ? -1 : (i1 > i2 ? 1 : 0);
					}
				});
				for (ParentChild parentChild : parentChildren) {
					if (parentChild.getParent() != null) {
						children.add(catalog.getItem(parentChild.getParent().getId()));
					}
				}
			}
			return children;
		}
	}
	
	public void setParents(List<Long> parentIds) {
		catalog.checkUpdating();
		if (dao.setItemParents(getEntity(), dao.getItems(parentIds))) {
			catalog.invalidate(getChildExtent());
			catalog.invalidate(getParentExtent());
		}
	}

	private Item getEntity() {
	  return dao.getItem(id);
  }
	
	/**
	 * Returns the parents of this item, and their parents transitively.
	 * The returned set is ordered (linked hash set) according to inheritance rules.
	 * @return
	 */
	public Set<ItemModel> getParentExtent() {
		synchronized (catalog) {
			if (parentExtent == null) {
				parentExtent = new LinkedHashSet<ItemModel>();
				LinkedHashSet<ItemModel> queue = new LinkedHashSet<ItemModel>();
				queue.add(this);
				for (ItemModel item : queue) {
					for (ItemModel parent : item.getParents()) {
						if (!queue.contains(parent)) {
							parentExtent.add(parent);
							queue.add(parent);
						}
					}
				}
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
				children = new HashSet<ItemModel>(getEntity().getChildren().size());
				List<ParentChild> parentChildren = new ArrayList<ParentChild>(getEntity().getChildren());
				for (ParentChild parentChild : parentChildren) {
					if (parentChild.getChild() != null) {
						children.add(catalog.getItem(parentChild.getChild().getId()));
					}
				}
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
				childExtent = new LinkedHashSet<ItemModel>();
				LinkedHashSet<ItemModel> queue = new LinkedHashSet<ItemModel>();
				queue.add(this);
				for (ItemModel item : queue) {
					for (ItemModel child : item.getChildren()) {
						if (!queue.contains(child)) {
							childExtent.add(child);
							queue.add(child);
						}
					}
				}
			}
			return childExtent;
		}
  }
	
	public Set<PropertyModel> getProperties() {
		synchronized (catalog) {
			if (properties == null) {
				properties = new HashSet<PropertyModel>();
				for (Property property : getEntity().getProperties()) {
					properties.add(new PropertyModel(this, this, property));
				}
				
			}
			return properties;
		}		
	}
	
	public Set<PropertyModel> getPropertyExtent() {
		synchronized (catalog) {
			if (propertyExtent == null) {
				propertyExtent = new HashSet<PropertyModel>();
				for (ItemModel parent : getParentExtent()) {
					for (Property property : parent.getEntity().getProperties()) {
						propertyExtent.add(new PropertyModel(this, parent, property));
					}
				}
				propertyExtent.addAll(getProperties());
				
			}
			return propertyExtent;
		}		
	}
	
	public Set<PropertyModel> getDanglingProperties() {
		synchronized (catalog) {
			if (danglingProperties == null) {
				Set<Property> entities = PropertyModel.getEntities(getPropertyExtent());
				for (PropertyValue value : getEntity().getPropertyValues()) {
					if (!entities.contains(value.getProperty())) {
						danglingProperties.add(new PropertyModel(this, null, value.getProperty()));
					}
				}
			}
			return danglingProperties;
		}		
	}
	

}

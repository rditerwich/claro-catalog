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

public class ItemModel {

	private final CatalogModel catalog;
	private Item entity;
	private Set<ItemModel> parents;
	private Set<ItemModel> parentExtent;
	private Set<ItemModel> children;
	private Set<ItemModel> childExtent;
	private Set<PropertyModel> definedProperties;
	private Set<PropertyModel> properties;
	
	ItemModel(CatalogModel catalog, Long id) {
		this.catalog = catalog;
		entity = catalog.em.find(Item.class, id);
		if (entity == null) {
			entity = new Item();
			entity.setId(id);
		}
  }
	
	void invalidate() {
		synchronized (catalog) {
			parents = null;
			children = null;
			parentExtent = null;
			childExtent = null;
			definedProperties = null;
			properties = null;
		}		
	}

	/**
	 * Returns the parents of this item, in order (linked hash map).
	 * @return
	 */
	public Set<ItemModel> getParents() {
		synchronized (catalog) {
			if (parents == null) {
				parents = new LinkedHashSet<ItemModel>(entity.getParents().size());
				List<ParentChild> parentChildren = new ArrayList<ParentChild>(entity.getParents());
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
		List<Long> currentParentIds = new ArrayList<Long>();
		for (ItemModel item : getParents()) {
			currentParentIds.add(item.entity.getId());
		}
		if (!parentIds.equals(currentParentIds)) {
			catalog.invalidate(getParentExtent());
			catalog.invalidate(getChildExtent());
			
		}
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
				children = new HashSet<ItemModel>(entity.getChildren().size());
				List<ParentChild> parentChildren = new ArrayList<ParentChild>(entity.getChildren());
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
	
	public Set<PropertyModel> getDefinedProperties() {
		synchronized (catalog) {
			if (definedProperties == null) {
				
			}
		}		
	}
	
	public Set<PropertyModel> getProperties() {
		
	}
}

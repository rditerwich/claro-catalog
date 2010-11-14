package claro.catalog;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import claro.catalog.CatalogUtil.ItemInfo;
import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.ImportSource;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.ParentChild;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyValue;
import claro.jpa.catalog.Status;
import claro.jpa.catalog.Supplier;

public class CatalogService {

	public static Long DONT_CARE = new Long(-1);
	
	private final Catalog catalog;
	private final OutputChannel outputChannel;
	
	private Map<Item, ItemInfo> itemInfo = new HashMap<Item, ItemInfo>();
	
	private ItemInfo itemInfo(Item item) {
		ItemInfo info = itemInfo.get(item);
		if (info == null) {
			info = new ItemInfo();
			itemInfo.put(item, info);
		}
		return info;
	}

	public CatalogService(Catalog catalog, OutputChannel outputChannel) {
		this.catalog = catalog;
		this.outputChannel = outputChannel;
  }

	
	public PropertyValue getPropertyValue(Item item, Property property, String language) {
		
	}
	
	private static class ItemInfo {
		private Item item;
		private List<Item> children;
		private List<Item> parents;
		private List<PropertyValues> propertyValues;
		
		public List<Item> getChildren(Item item) {
			if (children == null) {
				children = new ArrayList<Item>();
				for (ParentChild parentChild : sort(item.getChildren())) {
					if (parentChild.getChild() != null && eq(parentChild.getChild().getCatalog(), item.getCatalog())) {
						children.add(parentChild.getChild());
					}
				}
			}
			return children;
		}
		public List<Item> getParents(Item item) {
			if (parents == null) {
				parents = new ArrayList<Item>();
				for (ParentChild parentChild : sort(item.getChildren())) {
					if (parentChild.getParent() != null && eq(parentChild.getParent().getCatalog(), item.getCatalog())) {
						parents.add(parentChild.getParent());
					}
				}
			}
			return parents;
		}
		
		public List<PropertyValues> getPropertyValues() {
			if (propertyValues == null) {
				propertyValues = new ArrayList<CatalogService.PropertyValues>();
				for (PropertyValue value : item.getPropertyValues()) {
					
				}
			}
	    return propertyValues;
    }
		
		public PropertyValues findPropertyValues(Property property, String language, ImportSource importSource, Supplier supplier, OutputChannel outputChannel, Status status) {
			for (PropertyValues values : getPropertyValues()) {
				if (eq(property, values.property)) {
					if ()
				}
			}
		}
		
		private static <T> boolean eq(T o1, T o2) {
			if (o1 == o2) {
				return true;
			}
			return o1 != null && o1.equals(o2); 
		}
  }

	private static class PropertyValues implements Comparable<PropertyValues> {
		
		final Property property;
		final String language;
		final ImportSource importSource;
		final Supplier supplier;
		final OutputChannel outputChannel;
		final Status status;
		final List<PropertyValue> values;
		
		public PropertyValues(PropertyValue value) {
			this.property = value.getProperty();
			this.language = value.getLanguage();
			this.importSource = value.getImportSource();
			this.supplier = value.getSupplier();
			this.outputChannel = value.getOutputChannel();
			this.status = value.getStatus();
			this.values = new ArrayList<PropertyValue>();
			values.add(value);
		}
		
		public int compareTo(PropertyValues other) {
			int result;
			if ((result = compare(property, other.property)) == 0) {
				if ((result = compare(language, other.language)) == 0) {
				}				
			}
		  return result;
		}
	}
	
	private static int compare(Property p1, Property p2) {
		long id1 = p1 != null && p1.getId() != null ? p1.getId() : 0;
		long id2 = p2 != null && p2.getId() != null ? p2.getId() : 0;
		if (id1 < id2) return -1;
		if (id1 > id2) return 1;
		return 0;
	}
	
	private static int compare(String s1, String s2) {
		if (s1 == s2) return 0;
		if (s1 == null) return 1;
		if (s2 == null) return -1;
		return s1.compareTo(s2);
	}
}

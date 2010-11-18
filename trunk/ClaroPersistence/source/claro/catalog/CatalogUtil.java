package claro.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import claro.jpa.catalog.ImportSource;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.ParentChild;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyValue;
import claro.jpa.catalog.Supplier;

public class CatalogUtil {
	
	public class ItemInfo {
		private List<Item> children;
		private List<Item> parents;
		
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
  }
	
	private Map<Item, ItemInfo> itemInfo = new HashMap<Item, ItemInfo>();
	
	private ItemInfo itemInfo(Item item) {
		ItemInfo info = itemInfo.get(item);
		if (info == null) {
			info = new ItemInfo();
			itemInfo.put(item, info);
		}
		return info;
	}
	
	private static final Integer ZERO = new Integer(0);
	
	public static List<ParentChild> sort(Collection<ParentChild> parentChilds) {
		List<ParentChild> result = new ArrayList<ParentChild>(parentChilds);
		Collections.sort(result, new Comparator<ParentChild>() {
			public int compare(ParentChild o1, ParentChild o2) {
	      return (o1.getIndex() != null ? o1.getIndex() : ZERO).compareTo(o2.getIndex() != null ? o2.getIndex() : ZERO); 
      }
		});
		return result;
	}
	
	public static List<Item> getChildren(Item item) {
		List<Item> result = new ArrayList<Item>();
		for (ParentChild parentChild : sort(item.getChildren())) {
			if (parentChild.getChild() != null && eq(parentChild.getChild().getCatalog(), item.getCatalog())) {
				result.add(parentChild.getChild());
			}
		}
		return result;
	}
		
	public static List<Item> getParents(Item item) {
		List<Item> result = new ArrayList<Item>();
		for (ParentChild parentChild : sort(item.getParents())) {
			if (parentChild.getParent() != null && eq(parentChild.getParent().getCatalog(), item.getCatalog())) {
				result.add(parentChild.getParent());
			}
		}
		return result;
	}
	
	public static List<Item> getChildExtent(Item item, boolean includeSelf) {
		List<Item> result = new ArrayList<Item>();
		LinkedHashSet<Item> work = new LinkedHashSet<Item>();
		work.add(item);
		if (includeSelf) {
			result.add(item);
		}
		for (Item next : work) {
			for (Item child : getChildren(next)) {
				if (!work.contains(child)) {
					result.add(child);
					work.add(child);
				}
			}
		}
		return result;
	}
	
	public static List<Item> getParentExtent(Item item, boolean includeSelf) {
		List<Item> result = new ArrayList<Item>();
		LinkedHashSet<Item> work = new LinkedHashSet<Item>();
		work.add(item);
		if (includeSelf) {
			result.add(item);
		}
		for (Item next : work) {
			for (Item child : getParents(next)) {
				if (!work.contains(child)) {
					result.add(child);
					work.add(child);
				}
			}
		}
		return result;
	}
	
	public static PropertyValue getValue(Item item, Property property, Supplier supplier, ImportSource importSource, String language, OutputChannel outputChannel) {
		return getValue(item, property, supplier != null ? supplier.getId() : null, importSource != null ? importSource.getId() : null, language, outputChannel != null ? outputChannel.getId() : null);
	}
	
	/**
	 * A value variant: value for a specific language, output channel, supplier and status
	 * @param item
	 * @param property
	 * @param supplier
	 * @param importSource
	 * @param language
	 * @param outputChannel
	 * @return
	 */
	public static PropertyValue getValue(Item item, Property property, Long supplier, Long importSource, String language, Long outputChannel, Boolean published) {
		

		// filter values
		List<PropertyValue> values = new ArrayList<PropertyValue>();
		for (PropertyValue value : item.getPropertyValues()) {
			if (eq(property, value.getProperty())) {
				if (supplier == null || (value.getSupplier() != null && equals(supplier, value.getSupplier().getId()))) {
					if (importSource == null || (value.getImportSource() != null && equals(importSource, value.getImportSource().getId()))) {
						if (value.getLanguage() == null || equals(language, value.getLanguage())) {
							if (value.getOutputChannel() == null || equals(outputChannel, value.getOutputChannel())) {
								values.add(value);
							}
						}
					}
				}
			}
		}
		
		// filter values with a supplier 
		if (supplier == null) {
			for (int i = 0; i < values.size(); i++) {
				if (values.get(i).getSupplier() != null) {
					values.set(i, null);
				}
			}
		}
		
		// remove values for which there is a higher-priority import source
		if (importSource == null) {
			for (int i = 0; i < values.size(); i++) {
				if (values.get(i) != null) {
					for (int j = 0; j < values.size(); j++) {
						if (values.get(j) != null) {
							if (equals(values.get(i).getProperty(), values.get(j).getProperty())) {
								if (equals(values.get(i).getSupplier(), values.get(j).getSupplier())) {
									if (equals(values.get(i).getLanguage(), values.get(j).getLanguage())) {
										int priorityI = values.get(i).getImportSource() != null ? values.get(i).getImportSource().getPriority() : Integer.MAX_VALUE; 
										int priorityJ = values.get(j).getImportSource() != null ? values.get(j).getImportSource().getPriority() : Integer.MAX_VALUE;
										if (priorityI > priorityJ) {
											values.set(j, null);
										}
									}
								}
							}
						}
					}
				}				
			}
		}
		
		// remove values for which there is a language-specific value
		for (int i = 0; i < values.size(); i++) {
			if (values.get(i) != null) {
				for (int j = 0; j < values.size(); j++) {
					if (values.get(j) != null) {
						if (equals(values.get(i).getProperty(), values.get(j).getProperty())) {
							if (values.get(i).getLanguage() != null && values.get(j).getLanguage() == null) {
								values.set(j, null);
							}
						}
					}
				}
			}				
		}
		
		// remove values for which there is a output channel specific value
		for (int i = 0; i < values.size(); i++) {
			if (values.get(i) != null) {
				for (int j = 0; j < values.size(); j++) {
					if (values.get(j) != null) {
						if (equals(values.get(i).getProperty(), values.get(j).getProperty())) {
							if (values.get(i).getOutputChannel() != null && values.get(j).getOutputChannel() == null) {
								values.set(j, null);
							}
						}
					}
				}
			}
		}
		
		// is there a value left?
		for (PropertyValue value : values) {
			if (value != null) {
				return value;
			}
		}

		for (Item parent : getParentExtent(item, false)) {
			PropertyValue value = getValue(parent, property, supplier, importSource, language, outputChannel, published);
			if (value != null) {
				return value;
			}
		}
		
		// not found in any parent
		return null;
	}
	private static <T> boolean eq(T o1, T o2) {
		if (o1 == o2) {
			return true;
		}
		return o1 != null && o1.equals(o2); 
	}
	
}

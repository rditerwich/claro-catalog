
package claro.catalog.model;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyValue;

public class PropertyModel {

	final ItemModel item;
	final ItemModel ownerItem;
	final Property entity;

	public PropertyModel(ItemModel item, ItemModel ownerItem, Property property) {
		this.item = item;
		this.ownerItem = ownerItem;
		this.entity = property;
  }
	
	public ItemModel getItem() {
	  return item;
  }
	
	public ItemModel getOwnerItem() {
	  return ownerItem;
  }

	public Property getEntity() {
		return entity;
	}
	
	public boolean isDangling() {
		return ownerItem == null;
	}

	public Map<String, PropertyValue> values;
	
	public static Set<Property> getEntities(Collection<PropertyModel> properties) {
		Set<Property> result = new LinkedHashSet<Property>();
		for (PropertyModel property : properties) {
			result.add(property.getEntity());
		}
		return result;
	}

}

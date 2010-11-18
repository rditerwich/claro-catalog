
package claro.catalog.model;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import claro.jpa.catalog.ImportSource;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyValue;

public class PropertyModel {

	final ItemModel item;
	final ItemModel ownerItem;
	final Long propertyId;
	private PropertyValues values;
	private PropertyValues effectiveValues;
	private Map<ImportSource, PropertyValues> importSourceValues;

	public PropertyModel(ItemModel item, ItemModel ownerItem, Long propertyId) {
		this.item = item;
		this.ownerItem = ownerItem;
		this.propertyId = propertyId;
  }
	
	public Long getPropertyId() {
		return propertyId;
	}
	
	public ItemModel getItem() {
	  return item;
  }
	
	public ItemModel getOwnerItem() {
	  return ownerItem;
  }

	public Property getEntity() {
		return CatalogAccess.getDao().getProperty(propertyId);
	}
	
	public boolean isDangling() {
		return ownerItem == null;
	}

	public PropertyValues getValues() {
		synchronized (item.catalog) {
			if (values == null) {
				values = PropertyValues.EMPTY;
				Item item = this.item.getEntity();
				for (PropertyValue value : item.getPropertyValues()) {
					if (value.getImportSource() == null && value.getOutputChannel() == null && value.getAlternate() == null) {
						values = values.add(value.getLanguage(), getValue(value));
					}
				}
			}
			return values;
		}
	}
	
	public static Set<Property> getEntities(Collection<PropertyModel> properties) {
		Set<Property> result = new LinkedHashSet<Property>();
		for (PropertyModel property : properties) {
			result.add(property.getEntity());
		}
		return result;
	}

	private static Object getValue(PropertyValue value) {
		switch (value.getProperty().getType()) {
		case String: return value.getStringValue(); 
		}
		return null;
	}
}

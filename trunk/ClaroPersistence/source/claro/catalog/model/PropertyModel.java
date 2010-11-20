
package claro.catalog.model;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import claro.catalog.util.SMap;
import claro.jpa.catalog.ImportSource;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyValue;

public class PropertyModel {

	final ItemModel item;
	final ItemModel ownerItem;
	final Long propertyId;
	private SMap<String, Object> values;
	private SMap<String, Object> effectiveValues;
	private SMap<ImportSource, SMap<String, Object>> importSourceValues;

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

	public SMap<String, Object> getValues() {
		synchronized (item.catalog) {
			if (values == null) {
				values = SMap.empty();
				Item item = this.item.getEntity();
				for (PropertyValue value : item.getPropertyValues()) {
					if (value.getImportSource() == null && value.getOutputChannel() == null && value.getAlternate() == null) {
						values = values.add(value.getLanguage(), getTypedValue(value));
					}
				}
			}
			return values;
		}
	}
	
	public SMap<String, Object> getEffectiveValues() {
		synchronized (item.catalog) {
			if (effectiveValues == null) {
				effectiveValues = SMap.empty();
				Item item = this.item.getEntity();
				for (PropertyValue value : item.getPropertyValues()) {
					if (value.getImportSource() == null && value.getOutputChannel() == null && value.getAlternate() == null) {
						effectiveValues = effectiveValues.add(value.getLanguage(), getTypedValue(value));
					}
				}
			}
			return effectiveValues;
		}
	}
	
	public SMap<ImportSource, SMap<String, Object>> getImportSourceValues() {
		synchronized (item.catalog) {
			if (importSourceValues == null) {
				importSourceValues = SMap.empty();
				Item item = this.item.getEntity();
				for (PropertyValue value : item.getPropertyValues()) {
					if (value.getImportSource() == null && value.getOutputChannel() == null && value.getAlternate() == null) {
					}
				}
			}
			return importSourceValues;
		}
	}
	
	
	
	public static Set<Property> getEntities(Collection<PropertyModel> properties) {
		Set<Property> result = new LinkedHashSet<Property>();
		for (PropertyModel property : properties) {
			result.add(property.getEntity());
		}
		return result;
	}

	private static Object getTypedValue(PropertyValue value) {
		switch (value.getProperty().getType()) {
		case String: return value.getStringValue(); 
		}
		return null;
	}
}

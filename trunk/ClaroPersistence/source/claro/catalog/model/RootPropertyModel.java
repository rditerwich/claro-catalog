package claro.catalog.model;

import claro.jpa.catalog.EnumValue;
import claro.jpa.catalog.Label;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyType;
import easyenterprise.lib.util.SMap;

class RootPropertyModel extends PropertyModel {

	final ItemModel ownerItem;
	final Long propertyId;
	SMap<String, String> labels;
	SMap<Integer, SMap<String, String>> enumValues;

	public RootPropertyModel(ItemModel ownerItem, Long propertyId) {
		this.ownerItem = ownerItem;
		this.propertyId = propertyId;
		Property entity = getEntity();
		for (Label label : entity.getLabels()) {
			labels = labels.add(label.getLanguage(), label.getLabel());
		}
		if (entity.getType() == PropertyType.Enum) {
			for (EnumValue enumValue : entity.getEnumValues()) {
				SMap<String,String> enumLabels = SMap.empty();
				for (Label label : enumValue.getLabels()) {
					enumLabels = enumLabels.add(label.getLanguage(), label.getLabel());
				}
				enumValues = enumValues.add(enumValue.getValue(), enumLabels);
			}
		}
  }
	
	@Override
	public Long getPropertyId() {
	  return propertyId;
	}
	
	@Override
	public ItemModel getItem() {
	  return ownerItem;
	}
	
	@Override
	public ItemModel getOwnerItem() {
	  return ownerItem;
	}

	@Override
	public SMap<String, String> getLabels() {
	  return labels;
	}
	
	@Override
	public SMap<Integer, SMap<String, String>> getEnumValues() {
	  return enumValues;
  }
}


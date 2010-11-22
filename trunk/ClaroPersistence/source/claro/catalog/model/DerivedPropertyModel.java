package claro.catalog.model;

import easyenterprise.lib.util.SMap;

class DerivedPropertyModel extends PropertyModel {
	
	private final RootPropertyModel root;
	private final ItemModel item;
	
	DerivedPropertyModel(RootPropertyModel root, ItemModel item) {
		this.root = root;
		this.item = item;
	}
	
	@Override
	public Long getPropertyId() {
	  return root.propertyId;
	}
	
	@Override
	public ItemModel getItem() {
	  return item;
	}
	
	@Override
	public ItemModel getOwnerItem() {
	  return root.ownerItem;
	}
	
	@Override
	public SMap<String, String> getLabels() {
	  return root.getLabels();
	}
	
	public SMap<Integer, SMap<String, String>> getEnumValues() {
		return root.getEnumValues();
  }

}
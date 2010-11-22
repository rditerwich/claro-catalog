package claro.catalog.model;

class DanglingPropertyModel extends RootPropertyModel {
	
	DanglingPropertyModel(ItemModel item, Long propertyId) {
		super(item, propertyId);
	}
	
	@Override
	public boolean isDangling() {
	  return true;
	}
}
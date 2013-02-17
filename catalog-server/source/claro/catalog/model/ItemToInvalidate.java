package claro.catalog.model;

public class ItemToInvalidate {

	final ItemModel item;
	final boolean self;
	final boolean childExtent;
	final boolean parentExtent;
	
	public ItemToInvalidate(ItemModel item, boolean self, boolean childExtent, boolean parentExtent) {
		this.item = item;
		this.self = self;
		this.childExtent = childExtent;
		this.parentExtent = parentExtent;
	}
}


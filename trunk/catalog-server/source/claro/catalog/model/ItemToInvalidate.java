package claro.catalog.model;

public class ItemToInvalidate {

	final long itemId;
	final boolean self;
	final boolean childExtent;
	final boolean parentExtent;
	
	public ItemToInvalidate(long itemId, boolean self, boolean childExtent, boolean parentExtent) {
		this.itemId = itemId;
		this.self = self;
		this.childExtent = childExtent;
		this.parentExtent = parentExtent;
	}
}


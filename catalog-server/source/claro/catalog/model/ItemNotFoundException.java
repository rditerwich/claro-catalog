package claro.catalog.model;

public class ItemNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;
	private final Long itemId;

  public ItemNotFoundException(Long itemId) {
  	super("Item with id " + itemId + " does not exist.");
		this.itemId = itemId;
  }
  
  public Long getItemId() {
	  return itemId;
  }
}

package claro.catalog.model;

public class PropertyNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;
	private final Long propertyId;

  public PropertyNotFoundException(Long propertyId) {
  	super("Property with id " + propertyId + " does not exist.");
		this.propertyId = propertyId;
  }
  
  public Long getPropertyId() {
	  return propertyId;
  }
}

package claro.catalog.manager.presentation.client;

public class ObjectUtil {

	public static <T> boolean equals(T o1, T o2) {
		return o1 == o2 || (o1 != null && o1.equals(o2));
	}
	
	@SuppressWarnings("unchecked")
	public static <T> int compare(T o1, T o2) {
		if (o1 == o2) {
			return 0;
		}
		if (o1 == null) {
			return -1;
		}
		if (o2 == null) {
			return 1;
		}
		return ((Comparable) o1).compareTo(o2);
	}
	
    public static int hashCode(Object... values) {
        int result = 0;
        for (Object value : values) {
        	result = (result == 0 ? 17 : 31 * result) + (value == null ? 0 : value.hashCode());
        }
        return result;
    }
    
    public static int hashCode(Iterable<Object> values) {
    	int result = 17;
    	for (Object value : values) {
    		result = 37 * result + (value == null? 0 : value.hashCode());
    	}
    	return result;
    }
    
    public String toString(Object object) {
    	return object != null ? object.toString() : "null";
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T unsafeCast(Object object) {
        return (T) object;
    }

}

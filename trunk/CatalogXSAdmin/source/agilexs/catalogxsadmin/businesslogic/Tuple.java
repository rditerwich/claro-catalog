package agilexs.catalogxsadmin.businesslogic;

import java.io.Serializable;

public class Tuple<T, U> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private T first;
    private U second;
    
    public static <T, U> Tuple<T, U> create(T first, U second) {
        return new Tuple<T, U>(first, second);
    }
    
    public Tuple() {
    }
    
    public Tuple(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }

    
    public String toString() {
    	return "(" + first + ", " + second + ")";
    }
}

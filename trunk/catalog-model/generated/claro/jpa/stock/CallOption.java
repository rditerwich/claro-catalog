package claro.jpa.stock;

import java.io.Serializable;
import java.lang.Override;

@SuppressWarnings("serial")
public class CallOption extends Option implements Serializable {

    @Override
    public boolean equals(Object other) {
        if (other instanceof CallOption) {
            CallOption otherCallOption = (CallOption) other;
            return super.equals(otherCallOption);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result;
    }

}
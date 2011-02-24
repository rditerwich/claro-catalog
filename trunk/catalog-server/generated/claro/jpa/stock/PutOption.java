package claro.jpa.stock;

import java.io.Serializable;
import java.lang.Override;

@SuppressWarnings("serial")
public class PutOption extends Option implements Serializable {

    @Override
    public boolean equals(Object other) {
        if (other instanceof PutOption) {
            PutOption otherPutOption = (PutOption) other;
            return super.equals(otherPutOption);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result;
    }

}
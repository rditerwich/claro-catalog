package claro.jpa.stock;

import java.lang.Override;

public class PutOption extends Option {

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
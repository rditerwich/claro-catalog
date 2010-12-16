package claro.jpa.stock;

import java.lang.Override;

public class CallOption extends Option {

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
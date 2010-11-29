package claro.jpa.importing;

import java.lang.Override;
import java.lang.String;

public class Constant extends ImportingExpression {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Constant) {
            Constant otherConstant = (Constant) other;
            return super.equals(otherConstant);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result;
    }

}
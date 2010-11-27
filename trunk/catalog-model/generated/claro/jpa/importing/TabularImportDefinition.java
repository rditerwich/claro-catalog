package claro.jpa.importing;

import java.lang.Override;

public class TabularImportDefinition extends ImportDefinition {

    @Override
    public boolean equals(Object other) {
        if (other instanceof TabularImportDefinition) {
            TabularImportDefinition otherTabularImportDefinition = (TabularImportDefinition) other;
            return super.equals(otherTabularImportDefinition);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result;
    }

}
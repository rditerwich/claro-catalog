package claro.jpa.importing;

import java.lang.Boolean;
import java.lang.Override;

public class TabularImportDefinition extends ImportDefinition {
    private Boolean headerLine;

    public Boolean getHeaderLine() {
        return headerLine;
    }

    public void setHeaderLine(Boolean value) {
        this.headerLine = value;
    }

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
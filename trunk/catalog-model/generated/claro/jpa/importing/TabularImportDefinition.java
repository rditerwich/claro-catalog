package claro.jpa.importing;

import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Override;

@SuppressWarnings("serial")
public class TabularImportDefinition extends ImportDefinition implements Serializable {
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
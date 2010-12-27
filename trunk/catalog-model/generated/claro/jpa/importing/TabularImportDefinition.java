package claro.jpa.importing;

import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Override;
import java.lang.String;

@SuppressWarnings("serial")
public class TabularImportDefinition extends ImportDefinition implements Serializable {
    private Boolean headerLine;
    private String charset;
    private String separatorChars;

    public Boolean getHeaderLine() {
        return headerLine;
    }

    public void setHeaderLine(Boolean value) {
        this.headerLine = value;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String value) {
        this.charset = value;
    }

    public String getSeparatorChars() {
        return separatorChars;
    }

    public void setSeparatorChars(String value) {
        this.separatorChars = value;
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
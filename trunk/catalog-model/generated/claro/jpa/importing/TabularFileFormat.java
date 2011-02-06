package claro.jpa.importing;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TabularFileFormat extends ImportFileFormat implements Serializable {
    private Boolean headerLine = false;
    private String charset;
    private String separatorChars;

    public Boolean getHeaderLine() {
        return headerLine;
    }

    public void setHeaderLine(Boolean value) {
        if (value == null) {
            value = false;
        }
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
        if (other instanceof TabularFileFormat) {
            TabularFileFormat otherTabularFileFormat = (TabularFileFormat) other;
            return super.equals(otherTabularFileFormat);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result;
    }

}
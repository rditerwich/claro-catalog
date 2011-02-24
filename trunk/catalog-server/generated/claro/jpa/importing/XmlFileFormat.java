package claro.jpa.importing;

import java.io.Serializable;
import java.lang.Override;

@SuppressWarnings("serial")
public class XmlFileFormat extends ImportFileFormat implements Serializable {

    @Override
    public boolean equals(Object other) {
        if (other instanceof XmlFileFormat) {
            XmlFileFormat otherXmlFileFormat = (XmlFileFormat) other;
            return super.equals(otherXmlFileFormat);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result;
    }

}
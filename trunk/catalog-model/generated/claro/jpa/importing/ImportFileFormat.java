package claro.jpa.importing;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;

@SuppressWarnings("serial")
public class ImportFileFormat implements Serializable {
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ImportFileFormat) {
            ImportFileFormat otherImportFileFormat = (ImportFileFormat) other;
            if (this.id == null) {
                if (otherImportFileFormat.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherImportFileFormat.id)) {
                return false;
            }

            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + (id  == null? 0 : id .hashCode());
        return result;
    }

}
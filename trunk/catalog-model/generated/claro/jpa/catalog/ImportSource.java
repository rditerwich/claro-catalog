package claro.jpa.catalog;

import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;

public class ImportSource {
    private Long id;
    private Integer priority;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer value) {
        this.priority = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ImportSource) {
            ImportSource otherImportSource = (ImportSource) other;
            if (this.id == null) {
                if (otherImportSource.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherImportSource.id)) {
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
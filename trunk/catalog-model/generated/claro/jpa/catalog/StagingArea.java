package claro.jpa.catalog;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;

@SuppressWarnings("serial")
public class StagingArea implements Serializable {
    private Long id = 0l;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        if (value == null) {
            value = 0l;
        }
        this.id = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof StagingArea) {
            StagingArea otherStagingArea = (StagingArea) other;
            if (this.id == null) {
                if (otherStagingArea.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherStagingArea.id)) {
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
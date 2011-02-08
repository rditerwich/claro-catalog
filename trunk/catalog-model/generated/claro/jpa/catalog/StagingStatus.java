package claro.jpa.catalog;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;

@SuppressWarnings("serial")
public class StagingStatus implements Serializable {
    private Long id;
    private Catalog catalog;
    private StagingArea stagingArea;
    private Integer updateSequenceNr = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog value) {
        this.catalog = value;
    }

    public StagingArea getStagingArea() {
        return stagingArea;
    }

    public void setStagingArea(StagingArea value) {
        this.stagingArea = value;
    }

    public Integer getUpdateSequenceNr() {
        return updateSequenceNr;
    }

    public void setUpdateSequenceNr(Integer value) {
        if (value == null) {
            value = 0;
        }
        this.updateSequenceNr = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof StagingStatus) {
            StagingStatus otherStagingStatus = (StagingStatus) other;
            if (this.id == null) {
                if (otherStagingStatus.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherStagingStatus.id)) {
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
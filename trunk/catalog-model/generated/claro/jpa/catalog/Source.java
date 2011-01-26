package claro.jpa.catalog;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;

@SuppressWarnings("serial")
public class Source implements Serializable {
    private Long id = 0l;
    private Integer priority;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        if (value == null) {
            value = 0l;
        }
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
        if (other instanceof Source) {
            Source otherSource = (Source) other;
            if (this.id == null) {
                if (otherSource.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherSource.id)) {
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
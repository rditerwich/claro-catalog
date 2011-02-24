package claro.jpa.stock;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;

@SuppressWarnings("serial")
public class Underlying implements Serializable {
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Underlying) {
            Underlying otherUnderlying = (Underlying) other;
            if (this.id == null) {
                if (otherUnderlying.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherUnderlying.id)) {
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
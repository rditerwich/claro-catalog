package claro.jpa.stock;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;

@SuppressWarnings("serial")
public class OptionId implements Serializable {
    private Long id2;
    private OptionIdType type;
    private String id;

    public Long getId2() {
        return id2;
    }

    public void setId2(Long value) {
        this.id2 = value;
    }

    public OptionIdType getType() {
        return type;
    }

    public void setType(OptionIdType value) {
        this.type = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof OptionId) {
            OptionId otherOptionId = (OptionId) other;
            if (this.id2 == null) {
                if (otherOptionId.id2 != null) {
                    return false;
                }
            } 
            else if (!this.id2.equals(otherOptionId.id2)) {
                return false;
            }

            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + (id2  == null? 0 : id2 .hashCode());
        return result;
    }

}
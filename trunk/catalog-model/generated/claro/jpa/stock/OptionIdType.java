package claro.jpa.stock;

import java.lang.Long;
import java.lang.Override;
import java.lang.String;

public class OptionIdType {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof OptionIdType) {
            OptionIdType otherOptionIdType = (OptionIdType) other;
            if (this.id == null) {
                if (otherOptionIdType.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherOptionIdType.id)) {
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
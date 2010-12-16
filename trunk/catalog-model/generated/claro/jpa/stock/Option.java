package claro.jpa.stock;

import java.lang.Double;
import java.lang.Long;
import java.lang.Override;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class Option {
    private Long id;
    private Date expirationDate;
    private Double strike;
    private Collection<OptionId> ids;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date value) {
        this.expirationDate = value;
    }

    public Double getStrike() {
        return strike;
    }

    public void setStrike(Double value) {
        this.strike = value;
    }

    public Collection<OptionId> getIds() {
        if (ids == null) {
            ids = new ArrayList<OptionId>();
        }
        return ids;
    }

    public void setIds(Collection<OptionId> value) {
        this.ids = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Option) {
            Option otherOption = (Option) other;
            if (this.id == null) {
                if (otherOption.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherOption.id)) {
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
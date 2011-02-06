package claro.jpa.stock;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class Option implements Serializable {
    private Long id;
    private Date expirationDate;
    private Double strike = 0.0;
    private List<OptionId> ids;

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
        if (value == null) {
            value = 0.0;
        }
        this.strike = value;
    }

    public List<OptionId> getIds() {
        if (ids == null) {
            ids = new ArrayList<OptionId>();
        }
        return ids;
    }

    public void setIds(List<OptionId> value) {
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
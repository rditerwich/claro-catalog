package claro.jpa.shop;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import claro.jpa.catalog.Template;

@SuppressWarnings("serial")
public class Promotion implements Serializable {
    private Long id = 0l;
    private Date startDate;
    private Date endDate;
    private Shop shop;
    private List<Template> templates;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        if (value == null) {
            value = 0l;
        }
        this.id = value;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date value) {
        this.startDate = value;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date value) {
        this.endDate = value;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop value) {
        this.shop = value;
    }

    public List<Template> getTemplates() {
        if (templates == null) {
            templates = new ArrayList<Template>();
        }
        return templates;
    }

    public void setTemplates(List<Template> value) {
        this.templates = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Promotion) {
            Promotion otherPromotion = (Promotion) other;
            if (this.id == null) {
                if (otherPromotion.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherPromotion.id)) {
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
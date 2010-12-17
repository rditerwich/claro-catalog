package claro.jpa.shop;

import java.lang.Long;
import java.lang.Override;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import claro.jpa.catalog.Template;

public class Promotion {
    private Long id;
    private Date startDate;
    private Date endDate;
    private Shop shop;
    private Collection<Template> templates;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
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

    public Collection<Template> getTemplates() {
        if (templates == null) {
            templates = new ArrayList<Template>();
        }
        return templates;
    }

    public void setTemplates(Collection<Template> value) {
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
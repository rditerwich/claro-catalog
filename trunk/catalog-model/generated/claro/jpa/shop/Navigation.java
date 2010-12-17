package claro.jpa.shop;

import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.util.ArrayList;
import java.util.Collection;
import claro.jpa.catalog.Category;

public class Navigation {
    private Long id;
    private Category category;
    private Integer index;
    private Collection<Navigation> subNavigation;
    private Shop parentShop;
    private Navigation parentNavigation;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category value) {
        this.category = value;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer value) {
        this.index = value;
    }

    public Collection<Navigation> getSubNavigation() {
        if (subNavigation == null) {
            subNavigation = new ArrayList<Navigation>();
        }
        return subNavigation;
    }

    public void setSubNavigation(Collection<Navigation> value) {
        this.subNavigation = value;
    }

    public Shop getParentShop() {
        return parentShop;
    }

    public void setParentShop(Shop value) {
        this.parentShop = value;
    }

    public Navigation getParentNavigation() {
        return parentNavigation;
    }

    public void setParentNavigation(Navigation value) {
        this.parentNavigation = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Navigation) {
            Navigation otherNavigation = (Navigation) other;
            if (this.id == null) {
                if (otherNavigation.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherNavigation.id)) {
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
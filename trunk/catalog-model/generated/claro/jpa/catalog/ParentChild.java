package claro.jpa.catalog;

import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;

public class ParentChild {
    private Long id;
    private Item parent;
    private Item child;
    private Integer index;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Item getParent() {
        return parent;
    }

    public void setParent(Item value) {
        this.parent = value;
    }

    public Item getChild() {
        return child;
    }

    public void setChild(Item value) {
        this.child = value;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer value) {
        this.index = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ParentChild) {
            ParentChild otherParentChild = (ParentChild) other;
            if (this.id == null) {
                if (otherParentChild.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherParentChild.id)) {
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
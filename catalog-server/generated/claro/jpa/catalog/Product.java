package claro.jpa.catalog;

import java.io.Serializable;
import java.lang.Override;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Product extends Item implements Serializable {
    private List<Template> templates;

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
        if (other instanceof Product) {
            Product otherProduct = (Product) other;
            return super.equals(otherProduct);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result;
    }

}
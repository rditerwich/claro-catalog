package claro.jpa.catalog;

import java.lang.Override;
import java.util.ArrayList;
import java.util.Collection;

public class Product extends Item {
    private Collection<Template> templates;

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
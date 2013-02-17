package claro.jpa.importing;

import java.util.ArrayList;
import java.util.Collection;

public class ImportingExpression {
    private Long id;
    private ImportingExpressionType type;
    private String value;
    private String value2;
    private Collection<ImportingExpression> children;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public ImportingExpressionType getType() {
        return type;
    }

    public void setType(ImportingExpressionType value) {
        this.type = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value) {
        this.value2 = value;
    }

    public Collection<ImportingExpression> getChildren() {
        if (children == null) {
            children = new ArrayList<ImportingExpression>();
        }
        return children;
    }

    public void setChildren(Collection<ImportingExpression> value) {
        this.children = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ImportingExpression) {
            ImportingExpression otherImportingExpression = (ImportingExpression) other;
            if (this.id == null) {
                if (otherImportingExpression.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherImportingExpression.id)) {
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
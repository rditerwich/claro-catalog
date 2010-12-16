package claro.jpa.importing;

import java.lang.Long;
import java.lang.Override;
import java.lang.String;

public class ImportCategory {
    private Long id;
    private String expression;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String value) {
        this.expression = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ImportCategory) {
            ImportCategory otherImportCategory = (ImportCategory) other;
            if (this.id == null) {
                if (otherImportCategory.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherImportCategory.id)) {
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
package claro.jpa.importing;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;

@SuppressWarnings("serial")
public class ImportCategory implements Serializable {
    private Long id;
    private ImportSource importSource;
    private String categoryExpression;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public ImportSource getImportSource() {
        return importSource;
    }

    public void setImportSource(ImportSource value) {
        this.importSource = value;
    }

    public String getCategoryExpression() {
        return categoryExpression;
    }

    public void setCategoryExpression(String value) {
        this.categoryExpression = value;
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
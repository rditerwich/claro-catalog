package claro.jpa.importing;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;

@SuppressWarnings("serial")
public class ImportRules implements Serializable {
    private Long id;
    private String relativeUrl;
    private ImportFileFormat fileFormat;
    private String languageExpression;
    private String defaultCurrency;
    private ImportProducts importProducts;
    private ImportSource importSource;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getRelativeUrl() {
        return relativeUrl;
    }

    public void setRelativeUrl(String value) {
        this.relativeUrl = value;
    }

    public ImportFileFormat getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(ImportFileFormat value) {
        this.fileFormat = value;
    }

    public String getLanguageExpression() {
        return languageExpression;
    }

    public void setLanguageExpression(String value) {
        this.languageExpression = value;
    }

    public String getDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(String value) {
        this.defaultCurrency = value;
    }

    public ImportProducts getImportProducts() {
        return importProducts;
    }

    public void setImportProducts(ImportProducts value) {
        this.importProducts = value;
    }

    public ImportSource getImportSource() {
        return importSource;
    }

    public void setImportSource(ImportSource value) {
        this.importSource = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ImportRules) {
            ImportRules otherImportRules = (ImportRules) other;
            if (this.id == null) {
                if (otherImportRules.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherImportRules.id)) {
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
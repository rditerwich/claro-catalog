package claro.jpa.catalog;

import java.lang.Long;
import java.lang.Override;
import java.lang.String;

public class Language {
    private Long id;
    private String name;
    private String displayName;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String value) {
        this.displayName = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Language) {
            Language otherLanguage = (Language) other;
            if (this.id == null) {
                if (otherLanguage.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherLanguage.id)) {
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
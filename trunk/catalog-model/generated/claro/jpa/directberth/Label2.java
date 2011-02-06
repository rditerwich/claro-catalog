package claro.jpa.directberth;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Label2 implements Serializable {
    private Long id;
    private String language;
    private String label = "";

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String value) {
        this.language = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String value) {
        if (value == null) {
            value = "";
        }
        this.label = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Label2) {
            Label2 otherLabel2 = (Label2) other;
            if (this.id == null) {
                if (otherLabel2.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherLabel2.id)) {
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
package claro.jpa.catalog;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;

@SuppressWarnings("serial")
public class Template implements Serializable {
    private Long id = 0l;
    private String name = "";
    private String language;
    private String templateXml = "";
    private Item item;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        if (value == null) {
            value = 0l;
        }
        this.id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        if (value == null) {
            value = "";
        }
        this.name = value;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String value) {
        this.language = value;
    }

    public String getTemplateXml() {
        return templateXml;
    }

    public void setTemplateXml(String value) {
        if (value == null) {
            value = "";
        }
        this.templateXml = value;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item value) {
        this.item = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Template) {
            Template otherTemplate = (Template) other;
            if (this.id == null) {
                if (otherTemplate.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherTemplate.id)) {
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
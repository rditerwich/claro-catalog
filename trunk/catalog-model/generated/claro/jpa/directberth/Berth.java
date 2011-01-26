package claro.jpa.directberth;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;

@SuppressWarnings("serial")
public class Berth implements Serializable {
    private Long id = 0l;
    private String name = "";
    private Label2 description2;
    private Label2 profile;

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

    public Label2 getDescription2() {
        return description2;
    }

    public void setDescription2(Label2 value) {
        this.description2 = value;
    }

    public Label2 getProfile() {
        return profile;
    }

    public void setProfile(Label2 value) {
        this.profile = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Berth) {
            Berth otherBerth = (Berth) other;
            if (this.id == null) {
                if (otherBerth.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherBerth.id)) {
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
package claro.jpa.party;

import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;

@SuppressWarnings("serial")
public class User implements Serializable {
    private Long id = 0l;
    private String email = "";
    private String password = "";
    private Party party;
    private Boolean isCatalogUser = false;
    private String uiLanguage = "";

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        if (value == null) {
            value = 0l;
        }
        this.id = value;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        if (value == null) {
            value = "";
        }
        this.email = value;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String value) {
        if (value == null) {
            value = "";
        }
        this.password = value;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party value) {
        this.party = value;
    }

    public Boolean getIsCatalogUser() {
        return isCatalogUser;
    }

    public void setIsCatalogUser(Boolean value) {
        if (value == null) {
            value = false;
        }
        this.isCatalogUser = value;
    }

    public String getUiLanguage() {
        return uiLanguage;
    }

    public void setUiLanguage(String value) {
        if (value == null) {
            value = "";
        }
        this.uiLanguage = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof User) {
            User otherUser = (User) other;
            if (this.id == null) {
                if (otherUser.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherUser.id)) {
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
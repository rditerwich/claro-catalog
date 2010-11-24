package claro.jpa.party;

import java.lang.Boolean;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;

public class User {
    private Long id;
    private String email;
    private String password;
    private Party party;
    private Boolean isCatalogUser;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String value) {
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
        this.isCatalogUser = value;
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
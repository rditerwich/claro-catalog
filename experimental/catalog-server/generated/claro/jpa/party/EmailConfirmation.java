package claro.jpa.party;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;

@SuppressWarnings("serial")
public class EmailConfirmation implements Serializable {
    private Long id;
    private String email = "";
    private String confirmationKey = "";
    private Long expirationTime = 0l;

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
        if (value == null) {
            value = "";
        }
        this.email = value;
    }

    public String getConfirmationKey() {
        return confirmationKey;
    }

    public void setConfirmationKey(String value) {
        if (value == null) {
            value = "";
        }
        this.confirmationKey = value;
    }

    public Long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Long value) {
        if (value == null) {
            value = 0l;
        }
        this.expirationTime = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof EmailConfirmation) {
            EmailConfirmation otherEmailConfirmation = (EmailConfirmation) other;
            if (this.id == null) {
                if (otherEmailConfirmation.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherEmailConfirmation.id)) {
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
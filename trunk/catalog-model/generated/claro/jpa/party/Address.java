package claro.jpa.party;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;

@SuppressWarnings("serial")
public class Address implements Serializable {
    private Long id = 0l;
    private String address1 = "";
    private String address2;
    private String town = "";
    private String postalCode = "";
    private String country = "";

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        if (value == null) {
            value = 0l;
        }
        this.id = value;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String value) {
        if (value == null) {
            value = "";
        }
        this.address1 = value;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String value) {
        this.address2 = value;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String value) {
        if (value == null) {
            value = "";
        }
        this.town = value;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String value) {
        if (value == null) {
            value = "";
        }
        this.postalCode = value;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String value) {
        if (value == null) {
            value = "";
        }
        this.country = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Address) {
            Address otherAddress = (Address) other;
            if (this.id == null) {
                if (otherAddress.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherAddress.id)) {
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
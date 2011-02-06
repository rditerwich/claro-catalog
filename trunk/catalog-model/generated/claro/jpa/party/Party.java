package claro.jpa.party;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Party implements Serializable {
    private Long id;
    private String name = "";
    private String phoneNumber = "";
    private String website;
    private Address address;
    private Address shippingAddress;
    private Address deliveryAddress;
    private String billingName;
    private Address billingAddress;

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
        if (value == null) {
            value = "";
        }
        this.name = value;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String value) {
        if (value == null) {
            value = "";
        }
        this.phoneNumber = value;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String value) {
        this.website = value;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address value) {
        this.address = value;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address value) {
        this.shippingAddress = value;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address value) {
        this.deliveryAddress = value;
    }

    public String getBillingName() {
        return billingName;
    }

    public void setBillingName(String value) {
        this.billingName = value;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address value) {
        this.billingAddress = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Party) {
            Party otherParty = (Party) other;
            if (this.id == null) {
                if (otherParty.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherParty.id)) {
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
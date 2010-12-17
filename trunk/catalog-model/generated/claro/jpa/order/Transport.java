package claro.jpa.order;

import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;

public class Transport {
    private Long id;
    private String desciption;
    private String transportCompany;
    private Integer deliveryTime;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String value) {
        this.desciption = value;
    }

    public String getTransportCompany() {
        return transportCompany;
    }

    public void setTransportCompany(String value) {
        this.transportCompany = value;
    }

    public Integer getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Integer value) {
        this.deliveryTime = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Transport) {
            Transport otherTransport = (Transport) other;
            if (this.id == null) {
                if (otherTransport.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherTransport.id)) {
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
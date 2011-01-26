package claro.jpa.order;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;

@SuppressWarnings("serial")
public class Transport implements Serializable {
    private Long id = 0l;
    private String desciption = "";
    private String transportCompany = "";
    private Integer deliveryTime = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        if (value == null) {
            value = 0l;
        }
        this.id = value;
    }

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String value) {
        if (value == null) {
            value = "";
        }
        this.desciption = value;
    }

    public String getTransportCompany() {
        return transportCompany;
    }

    public void setTransportCompany(String value) {
        if (value == null) {
            value = "";
        }
        this.transportCompany = value;
    }

    public Integer getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Integer value) {
        if (value == null) {
            value = 0;
        }
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
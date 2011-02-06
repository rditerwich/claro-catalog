package claro.jpa.order;

import java.io.Serializable;
import java.util.Date;

import claro.jpa.party.User;

@SuppressWarnings("serial")
public class OrderHistory implements Serializable {
    private Long id;
    private User user;
    private OrderStatus newStatus;
    private String comment = "";
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User value) {
        this.user = value;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(OrderStatus value) {
        this.newStatus = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String value) {
        if (value == null) {
            value = "";
        }
        this.comment = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date value) {
        this.date = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof OrderHistory) {
            OrderHistory otherOrderHistory = (OrderHistory) other;
            if (this.id == null) {
                if (otherOrderHistory.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherOrderHistory.id)) {
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
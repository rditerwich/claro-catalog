package claro.jpa.order;

import java.lang.Double;
import java.lang.Long;
import java.lang.Override;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import claro.jpa.party.Address;
import claro.jpa.party.User;
import claro.jpa.shop.Shop;

public class Order {
    private Long id;
    private Shop shop;
    private Date orderDate;
    private Collection<ProductOrder> productOrders;
    private Address deliveryAddress;
    private Transport transport;
    private User user;
    private OrderStatus status;
    private Double amountPaid;
    private Collection<OrderHistory> history;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop value) {
        this.shop = value;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date value) {
        this.orderDate = value;
    }

    public Collection<ProductOrder> getProductOrders() {
        if (productOrders == null) {
            productOrders = new ArrayList<ProductOrder>();
        }
        return productOrders;
    }

    public void setProductOrders(Collection<ProductOrder> value) {
        this.productOrders = value;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address value) {
        this.deliveryAddress = value;
    }

    public Transport getTransport() {
        return transport;
    }

    public void setTransport(Transport value) {
        this.transport = value;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User value) {
        this.user = value;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus value) {
        this.status = value;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(Double value) {
        this.amountPaid = value;
    }

    public Collection<OrderHistory> getHistory() {
        if (history == null) {
            history = new ArrayList<OrderHistory>();
        }
        return history;
    }

    public void setHistory(Collection<OrderHistory> value) {
        this.history = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Order) {
            Order otherOrder = (Order) other;
            if (this.id == null) {
                if (otherOrder.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherOrder.id)) {
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
package claro.jpa.order;

import java.util.Date;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import claro.jpa.party.Address;
import claro.jpa.party.User;
import claro.jpa.shop.Shop;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-02-06T20:51:27")
@StaticMetamodel(Order.class)
public class Order_ { 

    public static volatile ListAttribute<Order, OrderHistory> history;
    public static volatile SingularAttribute<Order, Long> id;
    public static volatile SingularAttribute<Order, Shop> shop;
    public static volatile SingularAttribute<Order, Transport> transport;
    public static volatile SingularAttribute<Order, OrderStatus> status;
    public static volatile SingularAttribute<Order, Date> orderDate;
    public static volatile SingularAttribute<Order, Double> amountPaid;
    public static volatile SingularAttribute<Order, User> user;
    public static volatile SingularAttribute<Order, Address> deliveryAddress;
    public static volatile ListAttribute<Order, ProductOrder> productOrders;

}
package claro.jpa.order;

import claro.jpa.order.OrderHistory;
import claro.jpa.order.OrderStatus;
import claro.jpa.order.ProductOrder;
import claro.jpa.order.Transport;
import claro.jpa.party.Address;
import claro.jpa.party.User;
import claro.jpa.shop.Shop;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated("EclipseLink-2.1.1.v20100817-r8050 @ Sat Dec 18 13:29:15 CET 2010")
@StaticMetamodel(Order.class)
public class Order_ { 

    public static volatile CollectionAttribute<Order, OrderHistory> history;
    public static volatile SingularAttribute<Order, Long> id;
    public static volatile SingularAttribute<Order, Shop> shop;
    public static volatile SingularAttribute<Order, Transport> transport;
    public static volatile SingularAttribute<Order, OrderStatus> status;
    public static volatile SingularAttribute<Order, Date> orderDate;
    public static volatile SingularAttribute<Order, Double> amountPaid;
    public static volatile SingularAttribute<Order, User> user;
    public static volatile SingularAttribute<Order, Address> deliveryAddress;
    public static volatile CollectionAttribute<Order, ProductOrder> productOrders;

}
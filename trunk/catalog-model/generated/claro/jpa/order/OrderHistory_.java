package claro.jpa.order;

import claro.jpa.order.OrderStatus;
import claro.jpa.party.User;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated("EclipseLink-2.1.1.v20100817-r8050 @ Tue Dec 28 13:44:08 CET 2010")
@StaticMetamodel(OrderHistory.class)
public class OrderHistory_ { 

    public static volatile SingularAttribute<OrderHistory, OrderStatus> newStatus;
    public static volatile SingularAttribute<OrderHistory, Long> id;
    public static volatile SingularAttribute<OrderHistory, Date> date;
    public static volatile SingularAttribute<OrderHistory, String> comment;
    public static volatile SingularAttribute<OrderHistory, User> user;

}
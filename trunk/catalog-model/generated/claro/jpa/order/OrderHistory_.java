package claro.jpa.order;

import claro.jpa.order.OrderStatus;
import claro.jpa.party.User;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110129-r8903", date="2011-01-31T21:55:48")
@StaticMetamodel(OrderHistory.class)
public class OrderHistory_ { 

    public static volatile SingularAttribute<OrderHistory, OrderStatus> newStatus;
    public static volatile SingularAttribute<OrderHistory, Long> id;
    public static volatile SingularAttribute<OrderHistory, Date> date;
    public static volatile SingularAttribute<OrderHistory, String> comment;
    public static volatile SingularAttribute<OrderHistory, User> user;

}
package claro.jpa.order;

import claro.jpa.order.OrderStatus;
import claro.jpa.party.User;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101231-r8757", date="2011-01-04T15:49:50")
@StaticMetamodel(OrderHistory.class)
public class OrderHistory_ { 

    public static volatile SingularAttribute<OrderHistory, OrderStatus> newStatus;
    public static volatile SingularAttribute<OrderHistory, Long> id;
    public static volatile SingularAttribute<OrderHistory, Date> date;
    public static volatile SingularAttribute<OrderHistory, String> comment;
    public static volatile SingularAttribute<OrderHistory, User> user;

}
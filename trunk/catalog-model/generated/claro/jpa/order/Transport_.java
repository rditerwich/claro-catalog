package claro.jpa.order;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101231-r8757", date="2011-01-03T16:23:23")
@StaticMetamodel(Transport.class)
public class Transport_ { 

    public static volatile SingularAttribute<Transport, Long> id;
    public static volatile SingularAttribute<Transport, String> desciption;
    public static volatile SingularAttribute<Transport, Integer> deliveryTime;
    public static volatile SingularAttribute<Transport, String> transportCompany;

}
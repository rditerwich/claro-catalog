package claro.jpa.order;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated("EclipseLink-2.1.1.v20100817-r8050 @ Tue Dec 28 13:44:08 CET 2010")
@StaticMetamodel(Transport.class)
public class Transport_ { 

    public static volatile SingularAttribute<Transport, Long> id;
    public static volatile SingularAttribute<Transport, String> desciption;
    public static volatile SingularAttribute<Transport, Integer> deliveryTime;
    public static volatile SingularAttribute<Transport, String> transportCompany;

}
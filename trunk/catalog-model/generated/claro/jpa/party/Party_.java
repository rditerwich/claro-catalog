package claro.jpa.party;

import claro.jpa.party.Address;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated("EclipseLink-2.1.1.v20100817-r8050 @ Mon Dec 27 14:34:05 CET 2010")
@StaticMetamodel(Party.class)
public class Party_ { 

    public static volatile SingularAttribute<Party, Long> id;
    public static volatile SingularAttribute<Party, String> phoneNumber;
    public static volatile SingularAttribute<Party, Address> address;
    public static volatile SingularAttribute<Party, String> website;
    public static volatile SingularAttribute<Party, String> name;
    public static volatile SingularAttribute<Party, Address> billingAddress;
    public static volatile SingularAttribute<Party, Address> shippingAddress;
    public static volatile SingularAttribute<Party, Address> deliveryAddress;
    public static volatile SingularAttribute<Party, String> billingName;

}
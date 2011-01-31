package claro.jpa.party;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110129-r8903", date="2011-01-31T11:53:17")
@StaticMetamodel(Address.class)
public class Address_ { 

    public static volatile SingularAttribute<Address, Long> id;
    public static volatile SingularAttribute<Address, String> postalCode;
    public static volatile SingularAttribute<Address, String> address1;
    public static volatile SingularAttribute<Address, String> address2;
    public static volatile SingularAttribute<Address, String> town;
    public static volatile SingularAttribute<Address, String> country;

}
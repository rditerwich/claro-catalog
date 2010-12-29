package claro.jpa.party;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated("EclipseLink-2.1.1.v20100817-r8050 @ Tue Dec 28 13:44:08 CET 2010")
@StaticMetamodel(Address.class)
public class Address_ { 

    public static volatile SingularAttribute<Address, Long> id;
    public static volatile SingularAttribute<Address, String> postalCode;
    public static volatile SingularAttribute<Address, String> address1;
    public static volatile SingularAttribute<Address, String> address2;
    public static volatile SingularAttribute<Address, String> town;
    public static volatile SingularAttribute<Address, String> country;

}
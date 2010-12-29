package claro.jpa.party;

import claro.jpa.party.Party;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated("EclipseLink-2.1.1.v20100817-r8050 @ Tue Dec 28 13:44:08 CET 2010")
@StaticMetamodel(User.class)
public class User_ { 

    public static volatile SingularAttribute<User, Long> id;
    public static volatile SingularAttribute<User, String> email;
    public static volatile SingularAttribute<User, Party> party;
    public static volatile SingularAttribute<User, Boolean> isCatalogUser;
    public static volatile SingularAttribute<User, String> password;

}
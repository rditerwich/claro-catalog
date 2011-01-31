package claro.jpa.party;

import claro.jpa.party.Party;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110129-r8903", date="2011-01-31T15:11:35")
@StaticMetamodel(User.class)
public class User_ { 

    public static volatile SingularAttribute<User, Long> id;
    public static volatile SingularAttribute<User, String> uiLanguage;
    public static volatile SingularAttribute<User, String> email;
    public static volatile SingularAttribute<User, Party> party;
    public static volatile SingularAttribute<User, Boolean> isCatalogUser;
    public static volatile SingularAttribute<User, String> password;

}
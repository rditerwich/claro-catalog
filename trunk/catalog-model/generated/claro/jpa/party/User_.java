package claro.jpa.party;

import claro.jpa.party.Party;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101224-r8754", date="2010-12-29T16:49:28")
@StaticMetamodel(User.class)
public class User_ { 

    public static volatile SingularAttribute<User, Long> id;
    public static volatile SingularAttribute<User, String> email;
    public static volatile SingularAttribute<User, Party> party;
    public static volatile SingularAttribute<User, Boolean> isCatalogUser;
    public static volatile SingularAttribute<User, String> password;

}
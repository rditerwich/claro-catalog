package claro.jpa.party;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-02-07T14:18:39")
@StaticMetamodel(EmailConfirmation.class)
public class EmailConfirmation_ { 

    public static volatile SingularAttribute<EmailConfirmation, String> confirmationKey;
    public static volatile SingularAttribute<EmailConfirmation, Long> id;
    public static volatile SingularAttribute<EmailConfirmation, Long> expirationTime;
    public static volatile SingularAttribute<EmailConfirmation, String> email;

}
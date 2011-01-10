package claro.jpa.party;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110107-r8775", date="2011-01-07T13:52:08")
@StaticMetamodel(EmailConfirmation.class)
public class EmailConfirmation_ { 

    public static volatile SingularAttribute<EmailConfirmation, String> confirmationKey;
    public static volatile SingularAttribute<EmailConfirmation, Long> id;
    public static volatile SingularAttribute<EmailConfirmation, Long> expirationTime;
    public static volatile SingularAttribute<EmailConfirmation, String> email;

}
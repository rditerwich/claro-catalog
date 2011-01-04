package claro.jpa.party;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101231-r8757", date="2011-01-04T15:49:50")
@StaticMetamodel(EmailConfirmation.class)
public class EmailConfirmation_ { 

    public static volatile SingularAttribute<EmailConfirmation, String> confirmationKey;
    public static volatile SingularAttribute<EmailConfirmation, Long> id;
    public static volatile SingularAttribute<EmailConfirmation, Long> expirationTime;
    public static volatile SingularAttribute<EmailConfirmation, String> email;

}
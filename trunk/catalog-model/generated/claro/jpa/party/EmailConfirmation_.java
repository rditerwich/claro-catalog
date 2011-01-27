package claro.jpa.party;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110121-r8858", date="2011-01-26T15:50:12")
@StaticMetamodel(EmailConfirmation.class)
public class EmailConfirmation_ { 

    public static volatile SingularAttribute<EmailConfirmation, String> confirmationKey;
    public static volatile SingularAttribute<EmailConfirmation, Long> id;
    public static volatile SingularAttribute<EmailConfirmation, Long> expirationTime;
    public static volatile SingularAttribute<EmailConfirmation, String> email;

}
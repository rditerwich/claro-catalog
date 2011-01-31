package claro.jpa.stock;

import claro.jpa.stock.OptionId;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110129-r8903", date="2011-01-31T11:53:17")
@StaticMetamodel(Option.class)
public class Option_ { 

    public static volatile SingularAttribute<Option, Long> id;
    public static volatile SingularAttribute<Option, Double> strike;
    public static volatile SingularAttribute<Option, Date> expirationDate;
    public static volatile ListAttribute<Option, OptionId> ids;

}
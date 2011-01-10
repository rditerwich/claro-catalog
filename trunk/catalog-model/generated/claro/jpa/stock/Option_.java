package claro.jpa.stock;

import claro.jpa.stock.OptionId;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110107-r8775", date="2011-01-07T13:52:08")
@StaticMetamodel(Option.class)
public class Option_ { 

    public static volatile SingularAttribute<Option, Long> id;
    public static volatile SingularAttribute<Option, Double> strike;
    public static volatile SingularAttribute<Option, Date> expirationDate;
    public static volatile CollectionAttribute<Option, OptionId> ids;

}
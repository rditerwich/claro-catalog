package claro.jpa.stock;

import claro.jpa.stock.OptionId;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101224-r8754", date="2010-12-29T16:49:28")
@StaticMetamodel(Option.class)
public class Option_ { 

    public static volatile SingularAttribute<Option, Long> id;
    public static volatile SingularAttribute<Option, Double> strike;
    public static volatile SingularAttribute<Option, Date> expirationDate;
    public static volatile CollectionAttribute<Option, OptionId> ids;

}
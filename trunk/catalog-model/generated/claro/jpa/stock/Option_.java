package claro.jpa.stock;

import claro.jpa.stock.OptionId;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated("EclipseLink-2.1.1.v20100817-r8050 @ Wed Dec 22 00:06:38 CET 2010")
@StaticMetamodel(Option.class)
public class Option_ { 

    public static volatile SingularAttribute<Option, Long> id;
    public static volatile SingularAttribute<Option, Double> strike;
    public static volatile SingularAttribute<Option, Date> expirationDate;
    public static volatile CollectionAttribute<Option, OptionId> ids;

}
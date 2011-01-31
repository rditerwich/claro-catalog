package claro.jpa.shop;

import claro.jpa.catalog.Template;
import claro.jpa.shop.Shop;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110129-r8903", date="2011-01-31T07:17:29")
@StaticMetamodel(Promotion.class)
public class Promotion_ { 

    public static volatile SingularAttribute<Promotion, Long> id;
    public static volatile SingularAttribute<Promotion, Shop> shop;
    public static volatile SingularAttribute<Promotion, Date> startDate;
    public static volatile ListAttribute<Promotion, Template> templates;
    public static volatile SingularAttribute<Promotion, Date> endDate;

}
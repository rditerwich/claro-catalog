package claro.jpa.shop;

import claro.jpa.catalog.Template;
import claro.jpa.shop.Shop;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110107-r8775", date="2011-01-07T13:52:08")
@StaticMetamodel(Promotion.class)
public class Promotion_ { 

    public static volatile SingularAttribute<Promotion, Long> id;
    public static volatile SingularAttribute<Promotion, Shop> shop;
    public static volatile SingularAttribute<Promotion, Date> startDate;
    public static volatile CollectionAttribute<Promotion, Template> templates;
    public static volatile SingularAttribute<Promotion, Date> endDate;

}
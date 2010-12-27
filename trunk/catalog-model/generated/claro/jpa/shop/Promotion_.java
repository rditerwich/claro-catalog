package claro.jpa.shop;

import claro.jpa.catalog.Template;
import claro.jpa.shop.Shop;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated("EclipseLink-2.1.1.v20100817-r8050 @ Wed Dec 22 00:06:38 CET 2010")
@StaticMetamodel(Promotion.class)
public class Promotion_ { 

    public static volatile SingularAttribute<Promotion, Long> id;
    public static volatile SingularAttribute<Promotion, Shop> shop;
    public static volatile SingularAttribute<Promotion, Date> startDate;
    public static volatile CollectionAttribute<Promotion, Template> templates;
    public static volatile SingularAttribute<Promotion, Date> endDate;

}
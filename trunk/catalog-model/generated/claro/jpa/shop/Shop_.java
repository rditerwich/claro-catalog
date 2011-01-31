package claro.jpa.shop;

import claro.jpa.catalog.OutputChannel_;
import claro.jpa.catalog.Template;
import claro.jpa.shop.Navigation;
import claro.jpa.shop.Promotion;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110129-r8903", date="2011-01-31T07:17:29")
@StaticMetamodel(Shop.class)
public class Shop_ extends OutputChannel_ {

    public static volatile SingularAttribute<Shop, String> urlPrefix;
    public static volatile ListAttribute<Shop, Template> templates;
    public static volatile ListAttribute<Shop, Navigation> navigation;
    public static volatile ListAttribute<Shop, Promotion> promotions;

}
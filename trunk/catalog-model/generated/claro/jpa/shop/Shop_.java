package claro.jpa.shop;

import claro.jpa.catalog.OutputChannel_;
import claro.jpa.catalog.Template;
import claro.jpa.shop.Navigation;
import claro.jpa.shop.Promotion;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101231-r8757", date="2010-12-31T12:33:30")
@StaticMetamodel(Shop.class)
public class Shop_ extends OutputChannel_ {

    public static volatile SingularAttribute<Shop, String> urlPrefix;
    public static volatile CollectionAttribute<Shop, Template> templates;
    public static volatile CollectionAttribute<Shop, Navigation> navigation;
    public static volatile CollectionAttribute<Shop, Promotion> promotions;

}
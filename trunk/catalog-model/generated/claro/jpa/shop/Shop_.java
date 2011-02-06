package claro.jpa.shop;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import claro.jpa.catalog.OutputChannel_;
import claro.jpa.catalog.Template;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-02-06T20:51:27")
@StaticMetamodel(Shop.class)
public class Shop_ extends OutputChannel_ {

    public static volatile SingularAttribute<Shop, String> urlPrefix;
    public static volatile ListAttribute<Shop, Template> templates;
    public static volatile ListAttribute<Shop, Navigation> navigation;
    public static volatile ListAttribute<Shop, Promotion> promotions;

}
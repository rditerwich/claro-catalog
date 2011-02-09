package claro.jpa.shop;

import claro.jpa.catalog.OutputChannel_;
import claro.jpa.shop.Navigation;
import claro.jpa.shop.Promotion;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-02-09T11:20:28")
@StaticMetamodel(Shop.class)
public class Shop_ extends OutputChannel_ {

    public static volatile SingularAttribute<Shop, String> languages;
    public static volatile SingularAttribute<Shop, String> urlPrefix;
    public static volatile ListAttribute<Shop, Navigation> navigation;
    public static volatile ListAttribute<Shop, Promotion> promotions;

}
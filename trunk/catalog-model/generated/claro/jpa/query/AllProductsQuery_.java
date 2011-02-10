package claro.jpa.query;

import claro.jpa.catalog.Category;
import claro.jpa.shop.Shop;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-02-10T00:44:11")
@StaticMetamodel(AllProductsQuery.class)
public class AllProductsQuery_ extends Query_ {

    public static volatile SingularAttribute<AllProductsQuery, Shop> shop;
    public static volatile SingularAttribute<AllProductsQuery, Category> category;
    public static volatile SingularAttribute<AllProductsQuery, String> stringValue;

}
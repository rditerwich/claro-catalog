package claro.jpa.query;

import claro.jpa.catalog.Category;
import claro.jpa.shop.Shop;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110129-r8903", date="2011-01-31T13:44:53")
@StaticMetamodel(AllProductsQuery.class)
public class AllProductsQuery_ extends Query_ {

    public static volatile SingularAttribute<AllProductsQuery, Shop> shop;
    public static volatile SingularAttribute<AllProductsQuery, Category> category;
    public static volatile SingularAttribute<AllProductsQuery, String> stringValue;

}
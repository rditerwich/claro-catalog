package claro.jpa.query;

import claro.jpa.catalog.Category;
import claro.jpa.shop.Shop;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated("EclipseLink-2.1.1.v20100817-r8050 @ Sat Dec 18 13:29:15 CET 2010")
@StaticMetamodel(AllProductsQuery.class)
public class AllProductsQuery_ extends Query_ {

    public static volatile SingularAttribute<AllProductsQuery, Shop> shop;
    public static volatile SingularAttribute<AllProductsQuery, Category> category;
    public static volatile SingularAttribute<AllProductsQuery, String> stringValue;

}
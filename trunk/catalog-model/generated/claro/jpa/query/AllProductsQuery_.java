package claro.jpa.query;

import claro.jpa.catalog.Category;
import claro.jpa.shop.Shop;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110107-r8775", date="2011-01-07T13:52:08")
@StaticMetamodel(AllProductsQuery.class)
public class AllProductsQuery_ extends Query_ {

    public static volatile SingularAttribute<AllProductsQuery, Shop> shop;
    public static volatile SingularAttribute<AllProductsQuery, Category> category;
    public static volatile SingularAttribute<AllProductsQuery, String> stringValue;

}
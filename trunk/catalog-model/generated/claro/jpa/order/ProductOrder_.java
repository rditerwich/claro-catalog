package claro.jpa.order;

import claro.jpa.catalog.Product;
import claro.jpa.order.Order;
import claro.jpa.shop.Promotion;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110121-r8858", date="2011-01-21T14:45:02")
@StaticMetamodel(ProductOrder.class)
public class ProductOrder_ { 

    public static volatile SingularAttribute<ProductOrder, Product> product;
    public static volatile SingularAttribute<ProductOrder, Long> id;
    public static volatile SingularAttribute<ProductOrder, Double> price;
    public static volatile SingularAttribute<ProductOrder, Order> order;
    public static volatile SingularAttribute<ProductOrder, Promotion> promotion;
    public static volatile SingularAttribute<ProductOrder, Integer> volume;
    public static volatile SingularAttribute<ProductOrder, String> priceCurrency;

}
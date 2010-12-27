package claro.jpa.shop;

import claro.jpa.catalog.Product;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated("EclipseLink-2.1.1.v20100817-r8050 @ Wed Dec 22 00:06:38 CET 2010")
@StaticMetamodel(VolumeDiscountPromotion.class)
public class VolumeDiscountPromotion_ extends Promotion_ {

    public static volatile SingularAttribute<VolumeDiscountPromotion, Product> product;
    public static volatile SingularAttribute<VolumeDiscountPromotion, Double> price;
    public static volatile SingularAttribute<VolumeDiscountPromotion, String> priceCurrency;
    public static volatile SingularAttribute<VolumeDiscountPromotion, Integer> volumeDiscount;

}
package claro.jpa.shop;

import claro.jpa.catalog.Product;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110114-r8831", date="2011-01-14T13:00:08")
@StaticMetamodel(VolumeDiscountPromotion.class)
public class VolumeDiscountPromotion_ extends Promotion_ {

    public static volatile SingularAttribute<VolumeDiscountPromotion, Product> product;
    public static volatile SingularAttribute<VolumeDiscountPromotion, Double> price;
    public static volatile SingularAttribute<VolumeDiscountPromotion, String> priceCurrency;
    public static volatile SingularAttribute<VolumeDiscountPromotion, Integer> volumeDiscount;

}
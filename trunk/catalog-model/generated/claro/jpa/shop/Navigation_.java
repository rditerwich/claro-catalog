package claro.jpa.shop;

import claro.jpa.catalog.Category;
import claro.jpa.shop.Navigation;
import claro.jpa.shop.Shop;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101231-r8757", date="2011-01-03T16:23:23")
@StaticMetamodel(Navigation.class)
public class Navigation_ { 

    public static volatile CollectionAttribute<Navigation, Navigation> subNavigation;
    public static volatile SingularAttribute<Navigation, Long> id;
    public static volatile SingularAttribute<Navigation, Category> category;
    public static volatile SingularAttribute<Navigation, Integer> index;
    public static volatile SingularAttribute<Navigation, Navigation> parentNavigation;
    public static volatile SingularAttribute<Navigation, Shop> parentShop;

}
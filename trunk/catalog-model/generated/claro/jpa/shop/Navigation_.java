package claro.jpa.shop;

import claro.jpa.catalog.Category;
import claro.jpa.shop.Navigation;
import claro.jpa.shop.Shop;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-02-10T12:30:54")
@StaticMetamodel(Navigation.class)
public class Navigation_ { 

    public static volatile ListAttribute<Navigation, Navigation> subNavigation;
    public static volatile SingularAttribute<Navigation, Long> id;
    public static volatile SingularAttribute<Navigation, Category> category;
    public static volatile SingularAttribute<Navigation, Integer> index;
    public static volatile SingularAttribute<Navigation, Navigation> parentNavigation;
    public static volatile SingularAttribute<Navigation, Shop> parentShop;

}
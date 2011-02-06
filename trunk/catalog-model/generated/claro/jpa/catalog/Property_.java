package claro.jpa.catalog;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-02-06T20:51:27")
@StaticMetamodel(Property.class)
public class Property_ { 

    public static volatile SingularAttribute<Property, Long> id;
    public static volatile ListAttribute<Property, Label> labels;
    public static volatile SingularAttribute<Property, Item> item;
    public static volatile ListAttribute<Property, EnumValue> enumValues;
    public static volatile SingularAttribute<Property, PropertyType> type;
    public static volatile SingularAttribute<Property, Boolean> isMany;
    public static volatile SingularAttribute<Property, Boolean> categoryProperty;

}
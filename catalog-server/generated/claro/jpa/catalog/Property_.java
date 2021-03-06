package claro.jpa.catalog;

import claro.jpa.catalog.EnumValue;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.Label;
import claro.jpa.catalog.PropertyType;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2013-02-22T11:24:28")
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
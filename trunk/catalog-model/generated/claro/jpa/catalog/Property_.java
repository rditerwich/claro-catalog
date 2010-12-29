package claro.jpa.catalog;

import claro.jpa.catalog.EnumValue;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.Label;
import claro.jpa.catalog.PropertyGroup;
import claro.jpa.catalog.PropertyType;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101224-r8754", date="2010-12-29T16:49:28")
@StaticMetamodel(Property.class)
public class Property_ { 

    public static volatile SingularAttribute<Property, Long> id;
    public static volatile CollectionAttribute<Property, PropertyGroup> propertyGroups;
    public static volatile CollectionAttribute<Property, Label> labels;
    public static volatile SingularAttribute<Property, Item> item;
    public static volatile CollectionAttribute<Property, EnumValue> enumValues;
    public static volatile SingularAttribute<Property, PropertyType> type;
    public static volatile SingularAttribute<Property, Boolean> isMany;
    public static volatile SingularAttribute<Property, Boolean> categoryProperty;

}
package claro.jpa.catalog;

import claro.jpa.catalog.Item;
import claro.jpa.catalog.Label;
import claro.jpa.catalog.Property;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101231-r8757", date="2010-12-31T12:33:30")
@StaticMetamodel(PropertyGroup.class)
public class PropertyGroup_ { 

    public static volatile SingularAttribute<PropertyGroup, Long> id;
    public static volatile CollectionAttribute<PropertyGroup, Label> labels;
    public static volatile SingularAttribute<PropertyGroup, Item> item;
    public static volatile CollectionAttribute<PropertyGroup, Property> properties;

}
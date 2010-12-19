package claro.jpa.catalog;

import claro.jpa.catalog.Item;
import claro.jpa.catalog.Label;
import claro.jpa.catalog.Property;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated("EclipseLink-2.1.1.v20100817-r8050 @ Sat Dec 18 13:29:15 CET 2010")
@StaticMetamodel(PropertyGroup.class)
public class PropertyGroup_ { 

    public static volatile SingularAttribute<PropertyGroup, Long> id;
    public static volatile CollectionAttribute<PropertyGroup, Label> labels;
    public static volatile SingularAttribute<PropertyGroup, Item> item;
    public static volatile CollectionAttribute<PropertyGroup, Property> properties;

}
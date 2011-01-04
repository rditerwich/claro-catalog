package claro.jpa.catalog;

import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.ParentChild;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyGroup;
import claro.jpa.catalog.PropertyValue;
import claro.jpa.catalog.Template;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101231-r8757", date="2011-01-04T15:49:50")
@StaticMetamodel(Item.class)
public class Item_ { 

    public static volatile SingularAttribute<Item, Long> id;
    public static volatile CollectionAttribute<Item, PropertyValue> propertyValues;
    public static volatile CollectionAttribute<Item, PropertyGroup> propertyGroups;
    public static volatile CollectionAttribute<Item, ParentChild> parents;
    public static volatile CollectionAttribute<Item, Template> templates;
    public static volatile SingularAttribute<Item, Catalog> catalog;
    public static volatile CollectionAttribute<Item, ParentChild> children;
    public static volatile CollectionAttribute<Item, Property> properties;

}
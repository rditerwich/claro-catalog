package claro.jpa.catalog;

import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.ParentChild;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyValue;
import claro.jpa.catalog.Template;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-02-10T12:30:54")
@StaticMetamodel(Item.class)
public class Item_ { 

    public static volatile SingularAttribute<Item, Long> id;
    public static volatile ListAttribute<Item, PropertyValue> propertyValues;
    public static volatile ListAttribute<Item, ParentChild> parents;
    public static volatile ListAttribute<Item, Template> templates;
    public static volatile SingularAttribute<Item, Catalog> catalog;
    public static volatile ListAttribute<Item, ParentChild> children;
    public static volatile ListAttribute<Item, Property> properties;

}
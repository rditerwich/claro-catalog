package claro.jpa.catalog;

import claro.jpa.catalog.Category;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyGroup;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110121-r8858", date="2011-01-26T15:50:12")
@StaticMetamodel(PropertyGroupAssignment.class)
public class PropertyGroupAssignment_ { 

    public static volatile SingularAttribute<PropertyGroupAssignment, Long> id;
    public static volatile SingularAttribute<PropertyGroupAssignment, Category> category;
    public static volatile SingularAttribute<PropertyGroupAssignment, PropertyGroup> propertyGroup;
    public static volatile SingularAttribute<PropertyGroupAssignment, Property> property;

}
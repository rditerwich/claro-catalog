package claro.jpa.catalog;

import claro.jpa.catalog.PropertyGroupAssignment;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101231-r8757", date="2011-01-04T19:59:48")
@StaticMetamodel(Category.class)
public class Category_ extends Item_ {

    public static volatile SingularAttribute<Category, Boolean> containsProducts;
    public static volatile CollectionAttribute<Category, PropertyGroupAssignment> propertyGroupAssignments;

}
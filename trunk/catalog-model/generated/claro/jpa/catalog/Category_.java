package claro.jpa.catalog;

import claro.jpa.catalog.PropertyGroupAssignment;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110121-r8858", date="2011-01-26T12:59:44")
@StaticMetamodel(Category.class)
public class Category_ extends Item_ {

    public static volatile SingularAttribute<Category, Boolean> containsProducts;
    public static volatile ListAttribute<Category, PropertyGroupAssignment> propertyGroupAssignments;

}
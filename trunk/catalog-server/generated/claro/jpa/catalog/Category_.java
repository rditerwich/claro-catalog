package claro.jpa.catalog;

import claro.jpa.catalog.PropertyGroupAssignment;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-02-14T12:16:55")
@StaticMetamodel(Category.class)
public class Category_ extends Item_ {

    public static volatile SingularAttribute<Category, Boolean> containsProducts;
    public static volatile ListAttribute<Category, PropertyGroupAssignment> propertyGroupAssignments;

}
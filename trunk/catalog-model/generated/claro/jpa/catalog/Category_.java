package claro.jpa.catalog;

import claro.jpa.catalog.PropertyGroupAssignment;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110114-r8831", date="2011-01-14T13:00:08")
@StaticMetamodel(Category.class)
public class Category_ extends Item_ {

    public static volatile SingularAttribute<Category, Boolean> containsProducts;
    public static volatile CollectionAttribute<Category, PropertyGroupAssignment> propertyGroupAssignments;

}
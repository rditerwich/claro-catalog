package claro.jpa.catalog;

import claro.jpa.catalog.EnumValue;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyGroup;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110114-r8831", date="2011-01-14T13:00:08")
@StaticMetamodel(Label.class)
public class Label_ { 

    public static volatile SingularAttribute<Label, Long> id;
    public static volatile SingularAttribute<Label, PropertyGroup> propertyGroup;
    public static volatile SingularAttribute<Label, Property> property;
    public static volatile SingularAttribute<Label, String> label;
    public static volatile SingularAttribute<Label, String> language;
    public static volatile SingularAttribute<Label, EnumValue> enumValue;

}
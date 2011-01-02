package claro.jpa.catalog;

import claro.jpa.catalog.EnumValue;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyGroup;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101231-r8757", date="2010-12-31T12:33:30")
@StaticMetamodel(Label.class)
public class Label_ { 

    public static volatile SingularAttribute<Label, Long> id;
    public static volatile SingularAttribute<Label, PropertyGroup> propertyGroup;
    public static volatile SingularAttribute<Label, Property> property;
    public static volatile SingularAttribute<Label, String> label;
    public static volatile SingularAttribute<Label, String> language;
    public static volatile SingularAttribute<Label, EnumValue> enumValue;

}
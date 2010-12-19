package claro.jpa.catalog;

import claro.jpa.catalog.EnumValue;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyGroup;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated("EclipseLink-2.1.1.v20100817-r8050 @ Sun Dec 19 14:57:38 CET 2010")
@StaticMetamodel(Label.class)
public class Label_ { 

    public static volatile SingularAttribute<Label, Long> id;
    public static volatile SingularAttribute<Label, PropertyGroup> propertyGroup;
    public static volatile SingularAttribute<Label, Property> property;
    public static volatile SingularAttribute<Label, String> label;
    public static volatile SingularAttribute<Label, String> language;
    public static volatile SingularAttribute<Label, EnumValue> enumValue;

}
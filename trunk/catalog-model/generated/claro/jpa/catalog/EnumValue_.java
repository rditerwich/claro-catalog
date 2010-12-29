package claro.jpa.catalog;

import claro.jpa.catalog.Label;
import claro.jpa.catalog.Property;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated("EclipseLink-2.1.1.v20100817-r8050 @ Tue Dec 28 13:44:08 CET 2010")
@StaticMetamodel(EnumValue.class)
public class EnumValue_ { 

    public static volatile SingularAttribute<EnumValue, Long> id;
    public static volatile CollectionAttribute<EnumValue, Label> labels;
    public static volatile SingularAttribute<EnumValue, Integer> value;
    public static volatile SingularAttribute<EnumValue, Property> property;

}
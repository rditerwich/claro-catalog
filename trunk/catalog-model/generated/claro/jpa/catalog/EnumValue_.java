package claro.jpa.catalog;

import claro.jpa.catalog.Label;
import claro.jpa.catalog.Property;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-02-07T14:18:39")
@StaticMetamodel(EnumValue.class)
public class EnumValue_ { 

    public static volatile SingularAttribute<EnumValue, Long> id;
    public static volatile ListAttribute<EnumValue, Label> labels;
    public static volatile SingularAttribute<EnumValue, Integer> value;
    public static volatile SingularAttribute<EnumValue, Property> property;

}
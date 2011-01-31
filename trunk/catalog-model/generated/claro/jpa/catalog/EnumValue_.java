package claro.jpa.catalog;

import claro.jpa.catalog.Label;
import claro.jpa.catalog.Property;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110129-r8903", date="2011-01-31T11:53:17")
@StaticMetamodel(EnumValue.class)
public class EnumValue_ { 

    public static volatile SingularAttribute<EnumValue, Long> id;
    public static volatile ListAttribute<EnumValue, Label> labels;
    public static volatile SingularAttribute<EnumValue, Integer> value;
    public static volatile SingularAttribute<EnumValue, Property> property;

}
package claro.jpa.catalog;

import claro.jpa.catalog.Label;
import claro.jpa.catalog.Property;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101224-r8754", date="2010-12-29T16:49:28")
@StaticMetamodel(EnumValue.class)
public class EnumValue_ { 

    public static volatile SingularAttribute<EnumValue, Long> id;
    public static volatile CollectionAttribute<EnumValue, Label> labels;
    public static volatile SingularAttribute<EnumValue, Integer> value;
    public static volatile SingularAttribute<EnumValue, Property> property;

}
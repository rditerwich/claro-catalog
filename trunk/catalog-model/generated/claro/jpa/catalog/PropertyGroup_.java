package claro.jpa.catalog;

import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.Label;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-02-14T12:16:55")
@StaticMetamodel(PropertyGroup.class)
public class PropertyGroup_ { 

    public static volatile SingularAttribute<PropertyGroup, Long> id;
    public static volatile SingularAttribute<PropertyGroup, Catalog> catalog;
    public static volatile ListAttribute<PropertyGroup, Label> labels;

}
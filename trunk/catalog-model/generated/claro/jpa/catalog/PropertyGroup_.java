package claro.jpa.catalog;

import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.Label;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110107-r8775", date="2011-01-07T13:52:08")
@StaticMetamodel(PropertyGroup.class)
public class PropertyGroup_ { 

    public static volatile SingularAttribute<PropertyGroup, Long> id;
    public static volatile SingularAttribute<PropertyGroup, Catalog> catalog;
    public static volatile CollectionAttribute<PropertyGroup, Label> labels;

}
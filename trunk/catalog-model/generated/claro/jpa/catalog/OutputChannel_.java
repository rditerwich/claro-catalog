package claro.jpa.catalog;

import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.Property;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101224-r8754", date="2010-12-30T14:34:55")
@StaticMetamodel(OutputChannel.class)
public class OutputChannel_ { 

    public static volatile CollectionAttribute<OutputChannel, Property> excludedProperties;
    public static volatile SingularAttribute<OutputChannel, Long> id;
    public static volatile CollectionAttribute<OutputChannel, Item> excludedItems;
    public static volatile SingularAttribute<OutputChannel, Catalog> catalog;
    public static volatile SingularAttribute<OutputChannel, String> name;
    public static volatile SingularAttribute<OutputChannel, String> defaultLanguage;

}
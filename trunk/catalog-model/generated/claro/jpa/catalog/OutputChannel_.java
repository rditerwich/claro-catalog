package claro.jpa.catalog;

import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.Property;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-02-10T10:05:27")
@StaticMetamodel(OutputChannel.class)
public class OutputChannel_ { 

    public static volatile ListAttribute<OutputChannel, Property> excludedProperties;
    public static volatile SingularAttribute<OutputChannel, Long> id;
    public static volatile ListAttribute<OutputChannel, Item> excludedItems;
    public static volatile SingularAttribute<OutputChannel, Catalog> catalog;
    public static volatile SingularAttribute<OutputChannel, String> name;
    public static volatile SingularAttribute<OutputChannel, String> defaultLanguage;

}
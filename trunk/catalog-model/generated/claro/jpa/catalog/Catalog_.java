package claro.jpa.catalog;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-02-06T20:51:27")
@StaticMetamodel(Catalog.class)
public class Catalog_ { 

    public static volatile SingularAttribute<Catalog, Long> id;
    public static volatile ListAttribute<Catalog, Language> languages;
    public static volatile ListAttribute<Catalog, PropertyGroup> propertyGroups;
    public static volatile ListAttribute<Catalog, Template> templates;
    public static volatile ListAttribute<Catalog, Item> items;
    public static volatile SingularAttribute<Catalog, Category> root;
    public static volatile SingularAttribute<Catalog, String> name;
    public static volatile ListAttribute<Catalog, OutputChannel> outputChannels;

}
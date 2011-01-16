package claro.jpa.catalog;

import claro.jpa.catalog.Category;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.Language;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.PropertyGroup;
import claro.jpa.catalog.Template;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110114-r8831", date="2011-01-14T13:00:08")
@StaticMetamodel(Catalog.class)
public class Catalog_ { 

    public static volatile SingularAttribute<Catalog, Long> id;
    public static volatile ListAttribute<Catalog, Language> languages;
    public static volatile CollectionAttribute<Catalog, PropertyGroup> propertyGroups;
    public static volatile CollectionAttribute<Catalog, Template> templates;
    public static volatile CollectionAttribute<Catalog, Item> items;
    public static volatile SingularAttribute<Catalog, Category> root;
    public static volatile SingularAttribute<Catalog, String> name;
    public static volatile CollectionAttribute<Catalog, OutputChannel> outputChannels;

}
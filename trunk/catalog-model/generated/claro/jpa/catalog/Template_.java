package claro.jpa.catalog;

import claro.jpa.catalog.Item;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated("EclipseLink-2.1.1.v20100817-r8050 @ Mon Dec 27 14:34:05 CET 2010")
@StaticMetamodel(Template.class)
public class Template_ { 

    public static volatile SingularAttribute<Template, Long> id;
    public static volatile SingularAttribute<Template, String> templateXml;
    public static volatile SingularAttribute<Template, Item> item;
    public static volatile SingularAttribute<Template, String> name;
    public static volatile SingularAttribute<Template, String> language;

}
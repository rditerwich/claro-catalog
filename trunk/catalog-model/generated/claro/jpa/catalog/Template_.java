package claro.jpa.catalog;

import claro.jpa.catalog.Item;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110107-r8775", date="2011-01-07T13:52:08")
@StaticMetamodel(Template.class)
public class Template_ { 

    public static volatile SingularAttribute<Template, Long> id;
    public static volatile SingularAttribute<Template, String> templateXml;
    public static volatile SingularAttribute<Template, Item> item;
    public static volatile SingularAttribute<Template, String> name;
    public static volatile SingularAttribute<Template, String> language;

}
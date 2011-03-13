package claro.jpa.catalog;

import claro.jpa.catalog.Item;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-03-11T13:19:18")
@StaticMetamodel(ParentChild.class)
public class ParentChild_ { 

    public static volatile SingularAttribute<ParentChild, Item> child;
    public static volatile SingularAttribute<ParentChild, Long> id;
    public static volatile SingularAttribute<ParentChild, Integer> index;
    public static volatile SingularAttribute<ParentChild, Item> parent;

}
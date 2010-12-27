package claro.jpa.catalog;

import claro.jpa.catalog.Item;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated("EclipseLink-2.1.1.v20100817-r8050 @ Wed Dec 22 00:06:38 CET 2010")
@StaticMetamodel(ParentChild.class)
public class ParentChild_ { 

    public static volatile SingularAttribute<ParentChild, Item> child;
    public static volatile SingularAttribute<ParentChild, Long> id;
    public static volatile SingularAttribute<ParentChild, Integer> index;
    public static volatile SingularAttribute<ParentChild, Item> parent;

}
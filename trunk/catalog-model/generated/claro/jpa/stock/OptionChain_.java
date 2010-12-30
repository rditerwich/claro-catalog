package claro.jpa.stock;

import claro.jpa.stock.Exchange;
import claro.jpa.stock.Option;
import claro.jpa.stock.Underlying;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101224-r8754", date="2010-12-30T14:34:55")
@StaticMetamodel(OptionChain.class)
public class OptionChain_ { 

    public static volatile SingularAttribute<OptionChain, Long> id;
    public static volatile SingularAttribute<OptionChain, String> symbol;
    public static volatile SingularAttribute<OptionChain, Underlying> underlying;
    public static volatile SingularAttribute<OptionChain, Exchange> exchange;
    public static volatile CollectionAttribute<OptionChain, Option> options;

}
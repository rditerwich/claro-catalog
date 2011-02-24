package claro.jpa.stock;

import claro.jpa.stock.Exchange;
import claro.jpa.stock.Option;
import claro.jpa.stock.Underlying;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-02-14T12:16:55")
@StaticMetamodel(OptionChain.class)
public class OptionChain_ { 

    public static volatile SingularAttribute<OptionChain, Long> id;
    public static volatile SingularAttribute<OptionChain, String> symbol;
    public static volatile SingularAttribute<OptionChain, Underlying> underlying;
    public static volatile SingularAttribute<OptionChain, Exchange> exchange;
    public static volatile ListAttribute<OptionChain, Option> options;

}
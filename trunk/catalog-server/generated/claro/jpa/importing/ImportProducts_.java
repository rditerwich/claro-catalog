package claro.jpa.importing;

import claro.jpa.catalog.Property;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportProperty;
import claro.jpa.importing.ImportRules;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-03-11T13:19:18")
@StaticMetamodel(ImportProducts.class)
public class ImportProducts_ { 

    public static volatile SingularAttribute<ImportProducts, Long> id;
    public static volatile SingularAttribute<ImportProducts, String> outputChannelExpression;
    public static volatile SingularAttribute<ImportProducts, Property> matchProperty;
    public static volatile ListAttribute<ImportProducts, ImportCategory> categories;
    public static volatile ListAttribute<ImportProducts, ImportProperty> properties;
    public static volatile SingularAttribute<ImportProducts, ImportRules> rules;

}
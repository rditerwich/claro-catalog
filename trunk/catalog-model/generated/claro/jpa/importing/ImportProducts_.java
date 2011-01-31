package claro.jpa.importing;

import claro.jpa.catalog.Property;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportProperty;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110129-r8903", date="2011-01-31T15:11:35")
@StaticMetamodel(ImportProducts.class)
public class ImportProducts_ { 

    public static volatile SingularAttribute<ImportProducts, Long> id;
    public static volatile SingularAttribute<ImportProducts, String> outputChannelExpression;
    public static volatile SingularAttribute<ImportProducts, Property> matchProperty;
    public static volatile ListAttribute<ImportProducts, ImportCategory> categories;
    public static volatile ListAttribute<ImportProducts, ImportProperty> properties;

}
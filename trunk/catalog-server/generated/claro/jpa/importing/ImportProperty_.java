package claro.jpa.importing;

import claro.jpa.catalog.Property;
import claro.jpa.importing.ImportProducts;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-03-15T16:43:49")
@StaticMetamodel(ImportProperty.class)
public class ImportProperty_ { 

    public static volatile SingularAttribute<ImportProperty, Long> id;
    public static volatile SingularAttribute<ImportProperty, String> valueExpression;
    public static volatile SingularAttribute<ImportProperty, Property> property;
    public static volatile SingularAttribute<ImportProperty, ImportProducts> importProducts;

}
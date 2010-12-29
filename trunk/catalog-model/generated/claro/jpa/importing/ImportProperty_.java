package claro.jpa.importing;

import claro.jpa.catalog.Property;
import claro.jpa.importing.ImportDefinition;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated("EclipseLink-2.1.1.v20100817-r8050 @ Tue Dec 28 13:44:08 CET 2010")
@StaticMetamodel(ImportProperty.class)
public class ImportProperty_ { 

    public static volatile SingularAttribute<ImportProperty, Long> id;
    public static volatile SingularAttribute<ImportProperty, ImportDefinition> importDefinition;
    public static volatile SingularAttribute<ImportProperty, String> valueExpression;
    public static volatile SingularAttribute<ImportProperty, Property> property;

}
package claro.jpa.importing;

import claro.jpa.catalog.Property;
import claro.jpa.importing.ImportSource;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101231-r8757", date="2010-12-31T12:33:30")
@StaticMetamodel(ImportProperty.class)
public class ImportProperty_ { 

    public static volatile SingularAttribute<ImportProperty, Long> id;
    public static volatile SingularAttribute<ImportProperty, String> valueExpression;
    public static volatile SingularAttribute<ImportProperty, Property> property;
    public static volatile SingularAttribute<ImportProperty, ImportSource> importSource;

}
package claro.jpa.importing;

import claro.jpa.catalog.Property;
import claro.jpa.importing.ImportSource;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101224-r8754", date="2010-12-29T16:49:28")
@StaticMetamodel(ImportProperty.class)
public class ImportProperty_ { 

    public static volatile SingularAttribute<ImportProperty, Long> id;
    public static volatile SingularAttribute<ImportProperty, String> valueExpression;
    public static volatile SingularAttribute<ImportProperty, Property> property;
    public static volatile SingularAttribute<ImportProperty, ImportSource> importSource;

}
package claro.jpa.importing;

import claro.jpa.importing.ImportSource;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110121-r8858", date="2011-01-21T14:45:02")
@StaticMetamodel(ImportCategory.class)
public class ImportCategory_ { 

    public static volatile SingularAttribute<ImportCategory, Long> id;
    public static volatile SingularAttribute<ImportCategory, String> categoryExpression;
    public static volatile SingularAttribute<ImportCategory, ImportSource> importSource;

}
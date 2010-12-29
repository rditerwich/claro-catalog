package claro.jpa.importing;

import claro.jpa.importing.ImportSource;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101224-r8754", date="2010-12-29T16:49:28")
@StaticMetamodel(ImportCategory.class)
public class ImportCategory_ { 

    public static volatile SingularAttribute<ImportCategory, Long> id;
    public static volatile SingularAttribute<ImportCategory, String> categoryExpression;
    public static volatile SingularAttribute<ImportCategory, ImportSource> importSource;

}
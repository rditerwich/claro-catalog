package claro.jpa.importing;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-02-06T20:51:27")
@StaticMetamodel(ImportCategory.class)
public class ImportCategory_ { 

    public static volatile SingularAttribute<ImportCategory, Long> id;
    public static volatile SingularAttribute<ImportCategory, String> categoryExpression;
    public static volatile SingularAttribute<ImportCategory, ImportProducts> importProducts;

}
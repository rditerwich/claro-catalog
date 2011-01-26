package claro.jpa.importing;

import claro.jpa.importing.ImportFileFormat;
import claro.jpa.importing.ImportProducts;
import claro.jpa.importing.ImportSource;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110121-r8858", date="2011-01-26T12:59:44")
@StaticMetamodel(ImportRules.class)
public class ImportRules_ { 

    public static volatile SingularAttribute<ImportRules, Long> id;
    public static volatile SingularAttribute<ImportRules, String> defaultCurrency;
    public static volatile SingularAttribute<ImportRules, String> languageExpression;
    public static volatile SingularAttribute<ImportRules, String> relativeUrl;
    public static volatile SingularAttribute<ImportRules, ImportSource> importSource;
    public static volatile SingularAttribute<ImportRules, ImportProducts> importProducts;
    public static volatile SingularAttribute<ImportRules, ImportFileFormat> fileFormat;

}
package claro.jpa.importing;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-02-06T20:51:27")
@StaticMetamodel(ImportRules.class)
public class ImportRules_ { 

    public static volatile SingularAttribute<ImportRules, XmlFileFormat> xmlFileFormat;
    public static volatile SingularAttribute<ImportRules, Long> id;
    public static volatile SingularAttribute<ImportRules, TabularFileFormat> tabularFileFormat;
    public static volatile SingularAttribute<ImportRules, String> defaultCurrency;
    public static volatile SingularAttribute<ImportRules, String> languageExpression;
    public static volatile SingularAttribute<ImportRules, String> relativeUrl;
    public static volatile SingularAttribute<ImportRules, ImportSource> importSource;
    public static volatile SingularAttribute<ImportRules, ImportProducts> importProducts;
    public static volatile SingularAttribute<ImportRules, ImportFileFormat> fileFormat;

}
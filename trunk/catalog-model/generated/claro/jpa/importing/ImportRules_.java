package claro.jpa.importing;

import claro.jpa.importing.ImportFileFormat;
import claro.jpa.importing.ImportProducts;
import claro.jpa.importing.ImportSource;
import claro.jpa.importing.TabularFileFormat;
import claro.jpa.importing.XmlFileFormat;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-02-10T12:30:54")
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
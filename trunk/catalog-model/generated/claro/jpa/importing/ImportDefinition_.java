package claro.jpa.importing;

import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportProperty;
import claro.jpa.importing.ImportSource;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated("EclipseLink-2.1.1.v20100817-r8050 @ Sun Dec 19 14:57:38 CET 2010")
@StaticMetamodel(ImportDefinition.class)
public class ImportDefinition_ { 

    public static volatile SingularAttribute<ImportDefinition, String> importFileBaseName;
    public static volatile SingularAttribute<ImportDefinition, Long> id;
    public static volatile SingularAttribute<ImportDefinition, String> importSourceName;
    public static volatile CollectionAttribute<ImportDefinition, ImportSource> importSources;
    public static volatile SingularAttribute<ImportDefinition, Integer> priority;
    public static volatile SingularAttribute<ImportDefinition, String> name;
    public static volatile SingularAttribute<ImportDefinition, String> importFilePattern;
    public static volatile CollectionAttribute<ImportDefinition, ImportCategory> categories;
    public static volatile CollectionAttribute<ImportDefinition, ImportProperty> properties;
    public static volatile SingularAttribute<ImportDefinition, String> importUrl;
    public static volatile SingularAttribute<ImportDefinition, Boolean> importSourceNameAppendFileName;

}
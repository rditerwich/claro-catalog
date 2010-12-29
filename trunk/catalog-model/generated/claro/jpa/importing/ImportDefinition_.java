package claro.jpa.importing;

import claro.jpa.catalog.Property;
import claro.jpa.catalog.Source_;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportProperty;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated("EclipseLink-2.1.1.v20100817-r8050 @ Tue Dec 28 13:44:08 CET 2010")
@StaticMetamodel(ImportDefinition.class)
public class ImportDefinition_ extends Source_ {

    public static volatile SingularAttribute<ImportDefinition, String> outputChannelExpression;
    public static volatile SingularAttribute<ImportDefinition, String> importUrlExpression;
    public static volatile SingularAttribute<ImportDefinition, String> languageExpression;
    public static volatile SingularAttribute<ImportDefinition, String> name;
    public static volatile SingularAttribute<ImportDefinition, Property> matchProperty;
    public static volatile CollectionAttribute<ImportDefinition, ImportCategory> categories;
    public static volatile CollectionAttribute<ImportDefinition, ImportProperty> properties;
    public static volatile SingularAttribute<ImportDefinition, Integer> sequenceNr;

}
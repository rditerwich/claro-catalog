package claro.jpa.importing;

import claro.jpa.catalog.Property;
import claro.jpa.catalog.Source_;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportProperty;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101224-r8754", date="2010-12-29T16:49:28")
@StaticMetamodel(ImportSource.class)
public class ImportSource_ extends Source_ {

    public static volatile SingularAttribute<ImportSource, String> outputChannelExpression;
    public static volatile SingularAttribute<ImportSource, String> importUrlExpression;
    public static volatile SingularAttribute<ImportSource, String> defaultCurrency;
    public static volatile SingularAttribute<ImportSource, String> languageExpression;
    public static volatile SingularAttribute<ImportSource, String> name;
    public static volatile SingularAttribute<ImportSource, Property> matchProperty;
    public static volatile CollectionAttribute<ImportSource, ImportCategory> categories;
    public static volatile CollectionAttribute<ImportSource, ImportProperty> properties;
    public static volatile SingularAttribute<ImportSource, Integer> sequenceNr;

}
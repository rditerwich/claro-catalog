package claro.jpa.importing;

import claro.jpa.catalog.Source_;
import claro.jpa.importing.ImportRules;
import claro.jpa.jobs.Job;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-03-15T16:43:49")
@StaticMetamodel(ImportSource.class)
public class ImportSource_ extends Source_ {

    public static volatile SingularAttribute<ImportSource, Boolean> multiFileImport;
    public static volatile SingularAttribute<ImportSource, Boolean> sequentialUrl;
    public static volatile SingularAttribute<ImportSource, Boolean> orderedUrl;
    public static volatile SingularAttribute<ImportSource, String> name;
    public static volatile SingularAttribute<ImportSource, Job> job;
    public static volatile SingularAttribute<ImportSource, Boolean> incremental;
    public static volatile SingularAttribute<ImportSource, String> lastImportedUrl;
    public static volatile SingularAttribute<ImportSource, String> importUrl;
    public static volatile ListAttribute<ImportSource, ImportRules> rules;

}
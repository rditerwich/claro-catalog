package claro.jpa.importing;

import claro.jpa.importing.ImportSource;
import claro.jpa.jobs.JobResult_;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2013-02-22T11:24:28")
@StaticMetamodel(ImportJobResult.class)
public class ImportJobResult_ extends JobResult_ {

    public static volatile SingularAttribute<ImportJobResult, ImportSource> importSource;
    public static volatile SingularAttribute<ImportJobResult, String> url;

}
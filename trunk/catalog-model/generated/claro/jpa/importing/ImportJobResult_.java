package claro.jpa.importing;

import claro.jpa.importing.ImportSource;
import claro.jpa.jobs.JobResult_;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110121-r8858", date="2011-01-26T15:50:12")
@StaticMetamodel(ImportJobResult.class)
public class ImportJobResult_ extends JobResult_ {

    public static volatile SingularAttribute<ImportJobResult, ImportSource> importSource;
    public static volatile SingularAttribute<ImportJobResult, String> url;

}
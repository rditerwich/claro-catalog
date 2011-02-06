package claro.jpa.importing;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

import claro.jpa.jobs.JobResult_;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-02-06T20:51:27")
@StaticMetamodel(ImportJobResult.class)
public class ImportJobResult_ extends JobResult_ {

    public static volatile SingularAttribute<ImportJobResult, ImportSource> importSource;
    public static volatile SingularAttribute<ImportJobResult, String> url;

}
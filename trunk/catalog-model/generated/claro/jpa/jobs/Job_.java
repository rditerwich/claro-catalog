package claro.jpa.jobs;

import claro.jpa.jobs.Frequency;
import claro.jpa.jobs.JobResult;
import java.sql.Timestamp;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20101231-r8757", date="2010-12-31T10:56:05")
@StaticMetamodel(Job.class)
public class Job_ { 

    public static volatile SingularAttribute<Job, Long> id;
    public static volatile SingularAttribute<Job, Boolean> lastSuccess;
    public static volatile SingularAttribute<Job, Frequency> runFrequency;
    public static volatile CollectionAttribute<Job, JobResult> results;
    public static volatile SingularAttribute<Job, String> name;
    public static volatile SingularAttribute<Job, Timestamp> firstRun;
    public static volatile SingularAttribute<Job, Integer> health;

}
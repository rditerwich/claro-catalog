package claro.jpa.jobs;

import claro.jpa.jobs.Frequency;
import claro.jpa.jobs.JobResult;
import java.sql.Timestamp;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110121-r8858", date="2011-01-21T14:45:02")
@StaticMetamodel(Job.class)
public class Job_ { 

    public static volatile SingularAttribute<Job, Long> id;
    public static volatile SingularAttribute<Job, Boolean> lastSuccess;
    public static volatile SingularAttribute<Job, Frequency> runFrequency;
    public static volatile CollectionAttribute<Job, JobResult> results;
    public static volatile SingularAttribute<Job, String> name;
    public static volatile SingularAttribute<Job, Timestamp> firstRun;
    public static volatile SingularAttribute<Job, Integer> healthPerc;

}
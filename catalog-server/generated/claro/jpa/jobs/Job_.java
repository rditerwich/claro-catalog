package claro.jpa.jobs;

import claro.jpa.jobs.Frequency;
import claro.jpa.jobs.JobResult;
import java.sql.Timestamp;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2013-02-22T11:24:28")
@StaticMetamodel(Job.class)
public class Job_ { 

    public static volatile SingularAttribute<Job, Long> id;
    public static volatile SingularAttribute<Job, Boolean> lastSuccess;
    public static volatile SingularAttribute<Job, Frequency> runFrequency;
    public static volatile ListAttribute<Job, JobResult> results;
    public static volatile SingularAttribute<Job, String> name;
    public static volatile SingularAttribute<Job, Timestamp> lastTime;
    public static volatile SingularAttribute<Job, Timestamp> firstRun;
    public static volatile SingularAttribute<Job, Integer> healthPerc;

}
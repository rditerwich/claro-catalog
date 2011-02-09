package claro.jpa.jobs;

import claro.jpa.jobs.Job;
import java.sql.Timestamp;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110203-r8920", date="2011-02-09T11:20:28")
@StaticMetamodel(JobResult.class)
public class JobResult_ { 

    public static volatile SingularAttribute<JobResult, Timestamp> startTime;
    public static volatile SingularAttribute<JobResult, Long> id;
    public static volatile SingularAttribute<JobResult, Job> job;
    public static volatile SingularAttribute<JobResult, Timestamp> endTime;
    public static volatile SingularAttribute<JobResult, String> log;
    public static volatile SingularAttribute<JobResult, Boolean> success;

}
package claro.jpa.jobs;

import claro.jpa.jobs.Job;
import java.sql.Timestamp;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110129-r8903", date="2011-02-01T08:03:50")
@StaticMetamodel(JobResult.class)
public class JobResult_ { 

    public static volatile SingularAttribute<JobResult, Timestamp> startTime;
    public static volatile SingularAttribute<JobResult, Long> id;
    public static volatile SingularAttribute<JobResult, Job> job;
    public static volatile SingularAttribute<JobResult, Timestamp> endTime;
    public static volatile SingularAttribute<JobResult, String> log;
    public static volatile SingularAttribute<JobResult, Boolean> success;

}
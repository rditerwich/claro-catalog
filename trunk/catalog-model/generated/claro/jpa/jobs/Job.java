package claro.jpa.jobs;

import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Job implements Serializable {
    private Long id = 0l;
    private String name = "";
    private Timestamp firstRun;
    private Frequency runFrequency;
    private Integer healthPerc;
    private Boolean lastSuccess;
    private Timestamp lastTime;
    private List<JobResult> results;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        if (value == null) {
            value = 0l;
        }
        this.id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        if (value == null) {
            value = "";
        }
        this.name = value;
    }

    public Timestamp getFirstRun() {
        return firstRun;
    }

    public void setFirstRun(Timestamp value) {
        this.firstRun = value;
    }

    public Frequency getRunFrequency() {
        return runFrequency;
    }

    public void setRunFrequency(Frequency value) {
        this.runFrequency = value;
    }

    public Integer getHealthPerc() {
        return healthPerc;
    }

    public void setHealthPerc(Integer value) {
        this.healthPerc = value;
    }

    public Boolean getLastSuccess() {
        return lastSuccess;
    }

    public void setLastSuccess(Boolean value) {
        this.lastSuccess = value;
    }

    public Timestamp getLastTime() {
        return lastTime;
    }

    public void setLastTime(Timestamp value) {
        this.lastTime = value;
    }

    public List<JobResult> getResults() {
        if (results == null) {
            results = new ArrayList<JobResult>();
        }
        return results;
    }

    public void setResults(List<JobResult> value) {
        this.results = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Job) {
            Job otherJob = (Job) other;
            if (this.id == null) {
                if (otherJob.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherJob.id)) {
                return false;
            }

            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + (id  == null? 0 : id .hashCode());
        return result;
    }

}
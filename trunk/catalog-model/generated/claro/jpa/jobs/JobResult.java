package claro.jpa.jobs;

import java.io.Serializable;
import java.sql.Timestamp;

@SuppressWarnings("serial")
public class JobResult implements Serializable {
    private Long id;
    private Job job;
    private Boolean success = false;
    private Timestamp startTime;
    private Timestamp endTime;
    private String log = "";

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job value) {
        this.job = value;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean value) {
        if (value == null) {
            value = false;
        }
        this.success = value;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp value) {
        this.startTime = value;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp value) {
        this.endTime = value;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String value) {
        if (value == null) {
            value = "";
        }
        this.log = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof JobResult) {
            JobResult otherJobResult = (JobResult) other;
            if (this.id == null) {
                if (otherJobResult.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherJobResult.id)) {
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
package claro.jpa.jobs;

import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.sql.Timestamp;

@SuppressWarnings("serial")
public class Job implements Serializable {
    private Long id;
    private String name;
    private Timestamp firstRun;
    private Frequency runFrequency;
    private Integer health;
    private Boolean lastSuccess;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
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

    public Integer getHealth() {
        return health;
    }

    public void setHealth(Integer value) {
        this.health = value;
    }

    public Boolean getLastSuccess() {
        return lastSuccess;
    }

    public void setLastSuccess(Boolean value) {
        this.lastSuccess = value;
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
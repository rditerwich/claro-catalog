package claro.jpa.importing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import claro.jpa.catalog.Source;
import claro.jpa.jobs.Job;

@SuppressWarnings("serial")
public class ImportSource extends Source implements Serializable {
    private String name = "";
    private String importUrl;
    private Boolean sequentialUrl = false;
    private Boolean orderedUrl = false;
    private Boolean incremental = false;
    private String lastImportedUrl;
    private Boolean multiFileImport = false;
    private List<ImportRules> rules;
    private Job job;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        if (value == null) {
            value = "";
        }
        this.name = value;
    }

    public String getImportUrl() {
        return importUrl;
    }

    public void setImportUrl(String value) {
        this.importUrl = value;
    }

    public Boolean getSequentialUrl() {
        return sequentialUrl;
    }

    public void setSequentialUrl(Boolean value) {
        if (value == null) {
            value = false;
        }
        this.sequentialUrl = value;
    }

    public Boolean getOrderedUrl() {
        return orderedUrl;
    }

    public void setOrderedUrl(Boolean value) {
        if (value == null) {
            value = false;
        }
        this.orderedUrl = value;
    }

    public Boolean getIncremental() {
        return incremental;
    }

    public void setIncremental(Boolean value) {
        if (value == null) {
            value = false;
        }
        this.incremental = value;
    }

    public String getLastImportedUrl() {
        return lastImportedUrl;
    }

    public void setLastImportedUrl(String value) {
        this.lastImportedUrl = value;
    }

    public Boolean getMultiFileImport() {
        return multiFileImport;
    }

    public void setMultiFileImport(Boolean value) {
        if (value == null) {
            value = false;
        }
        this.multiFileImport = value;
    }

    public List<ImportRules> getRules() {
        if (rules == null) {
            rules = new ArrayList<ImportRules>();
        }
        return rules;
    }

    public void setRules(List<ImportRules> value) {
        this.rules = value;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job value) {
        this.job = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ImportSource) {
            ImportSource otherImportSource = (ImportSource) other;
            return super.equals(otherImportSource);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result;
    }

}
package claro.jpa.importing;

import java.io.Serializable;
import java.lang.Override;
import java.lang.String;
import claro.jpa.jobs.JobResult;

@SuppressWarnings("serial")
public class ImportJobResult extends JobResult implements Serializable {
    private ImportSource importSource;
    private String url = "";

    public ImportSource getImportSource() {
        return importSource;
    }

    public void setImportSource(ImportSource value) {
        this.importSource = value;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String value) {
        if (value == null) {
            value = "";
        }
        this.url = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ImportJobResult) {
            ImportJobResult otherImportJobResult = (ImportJobResult) other;
            return super.equals(otherImportJobResult);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;
        return result;
    }

}
package claro.jpa.query;

import java.lang.Long;
import java.lang.Override;

public class Query {
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Query) {
            Query otherQuery = (Query) other;
            if (this.id == null) {
                if (otherQuery.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherQuery.id)) {
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
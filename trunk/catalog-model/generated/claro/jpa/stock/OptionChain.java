package claro.jpa.stock;

import java.io.Serializable;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class OptionChain implements Serializable {
    private Long id = 0l;
    private Exchange exchange;
    private Underlying underlying;
    private String symbol = "";
    private List<Option> options;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        if (value == null) {
            value = 0l;
        }
        this.id = value;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange value) {
        this.exchange = value;
    }

    public Underlying getUnderlying() {
        return underlying;
    }

    public void setUnderlying(Underlying value) {
        this.underlying = value;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String value) {
        if (value == null) {
            value = "";
        }
        this.symbol = value;
    }

    public List<Option> getOptions() {
        if (options == null) {
            options = new ArrayList<Option>();
        }
        return options;
    }

    public void setOptions(List<Option> value) {
        this.options = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof OptionChain) {
            OptionChain otherOptionChain = (OptionChain) other;
            if (this.id == null) {
                if (otherOptionChain.id != null) {
                    return false;
                }
            } 
            else if (!this.id.equals(otherOptionChain.id)) {
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
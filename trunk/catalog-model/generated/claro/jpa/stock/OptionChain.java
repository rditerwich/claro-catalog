package claro.jpa.stock;

import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;

public class OptionChain {
    private Long id;
    private Exchange exchange;
    private Underlying underlying;
    private String symbol;
    private Collection<Option> options;

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
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
        this.symbol = value;
    }

    public Collection<Option> getOptions() {
        if (options == null) {
            options = new ArrayList<Option>();
        }
        return options;
    }

    public void setOptions(Collection<Option> value) {
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
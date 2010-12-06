package claro.catalog.data;

import java.io.Serializable;

public class MoneyValue implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public final Double value;
	public final String currency;

	public MoneyValue(Double value, String currency) {
		this.value = value;
		this.currency = currency;
	}
}

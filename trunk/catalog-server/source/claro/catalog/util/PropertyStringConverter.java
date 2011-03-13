package claro.catalog.util;

import claro.catalog.data.MediaValue;
import claro.jpa.catalog.PropertyType;
import easyenterprise.lib.util.Money;

public class PropertyStringConverter {

	/**
   * ISO 4217 currency code.
   * See http://en.wikipedia.org/wiki/ISO_4217.
	 */
	private String defaultCurrency;
	
	
	/**
	 * When null, the currency of the locale is used.
	 * @return
	 */
	public String getDefaultCurrency() {
		return defaultCurrency;
	}
	
	public void setDefaultCurrency(String defaultCurrency) {
		this.defaultCurrency = defaultCurrency;
	}
	
	public String toString(PropertyType type, Object value) {
		switch (type) {
		case Media: 
			MediaValue mediaValue = (MediaValue) value;
			return mediaValue.mimeType + ":" + mediaValue.mediaContentId + ":" + mediaValue.name;
		case Money: 
			Money moneyValue = (Money) value;
			return moneyValue.value.toString();
		default: return value.toString();
		}
	}
	
	/**
	 * Converts a string value to a typed property value.
	 * @param type
	 * @param value
	 * @return Typed value, never null.
	 */
	public Object fromString(PropertyType type, String value) throws Exception {
		switch (type) {
		case String:
			return value;
		case Integer:
			return Long.parseLong(value);
		case Real:
			return Double.parseDouble(value);
		case Media: 
			int index = value.indexOf(':');
			if (index < 0) {
				throw new Exception("missing mimetype in mimetype:id[:name]");
			}
			String mimetype = value.substring(0, index);
			int index2 = value.indexOf(':', index + 1);
			if (index2 < 0) {
				throw new Exception("missing id in mimetype:id[:name]");
			}
			long id = Long.parseLong(value.substring(index + 1, index2));
			String fileName = "";
			index = value.indexOf(':', index2 + 1);
			if (index > -1) {
				fileName = value.substring(index + 1);
			}
			return MediaValue.create(id, mimetype, fileName);
		case Money: 
			return Money.parse(value, defaultCurrency);
		default: 
			throw new Exception("invalid string value:" + value);
		}
	}
}
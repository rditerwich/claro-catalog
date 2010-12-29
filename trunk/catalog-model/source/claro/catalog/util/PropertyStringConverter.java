package claro.catalog.util;

import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import claro.catalog.data.MediaValue;
import claro.catalog.data.MoneyValue;
import claro.jpa.catalog.PropertyType;
import easyenterprise.lib.util.ObjectUtil;

public class PropertyStringConverter {

	public static final Map<String, Currency> currencyMap = createCurrencyMap();
	
	/**
   * ISO 4217 currency code.
   * See http://en.wikipedia.org/wiki/ISO_4217.
	 */
	private static String defaultCurrency;
	
	private static Locale locale = Locale.getDefault();
	
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
	
  public Locale getLocale() {
		return locale;
	}
  
  public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	public static String toString(PropertyType type, Object value) {
		switch (type) {
		case Media: 
			MediaValue mediaValue = (MediaValue) value;
			return mediaValue.mimeType + ":" + mediaValue.propertyValueId + ":" + mediaValue.filename;
		case Money: 
			MoneyValue moneyValue = (MoneyValue) value;
			String currencyCode = ObjectUtil.orElse(moneyValue.currency, defaultCurrency);
			Currency currency = Currency.getInstance(currencyCode);
			NumberFormat format = NumberFormat.getInstance(locale); 
			format.setCurrency(currency);
			return format.format(moneyValue.value);
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
			return new MediaValue(id, mimetype, fileName);
		case Money: 
			// parse currency symbol
			String symbol = "";
			for (int i = 0; i < value.length(); i++) {
				if (Character.isDigit(i) || Character.isWhitespace(i)) {
					symbol = value.substring(0, i).toUpperCase();
					value = value.substring(i).trim();
					break;
				}
			}
			Currency currency = currencyMap.get(symbol);
			String currencyCode = currency != null ? currency.getCurrencyCode() : defaultCurrency;
			if (currencyCode == null && locale != null) {
				currency = Currency.getInstance(locale);
				if (currency != null) {
					currencyCode = currency.getCurrencyCode();
				}
			}
			return new MoneyValue(Double.parseDouble(value), currencyCode);
		default: 
			throw new Exception("invalid string value:" + value);
		}
	}
	
	public static String mimetype(String fileName) {
		return URLConnection.getFileNameMap().getContentTypeFor(fileName);
	}
	
	private static Map<String, Currency> createCurrencyMap() {
		Map<String, Currency> result = new HashMap<String, Currency>();
		for (Locale locale : Locale.getAvailableLocales()) {
			try {
				Currency currency = Currency.getInstance(locale);
				if (currency != null) {
					result.put(currency.getCurrencyCode(), currency);
					for (Locale locale2 : Locale.getAvailableLocales()) {
						result.put(currency.getSymbol(locale2), currency);
					}
				}
			} catch (Exception e) {
			}
		}
		return result;
	}
}
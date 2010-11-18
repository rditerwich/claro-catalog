package claro.catalog.model;

import java.io.Serializable;
import java.util.Arrays;

import com.google.common.base.Objects;

public abstract class PropertyValues implements Serializable {

	private static final long serialVersionUID = 1L;

	public boolean hasValue() {
		return getValue() != null;
	}
	
	public Object getValue() {
		return getValue(null);
	}
	
	public Object[] getValues() {
		return getValues(null);
	}
	
	/**
	 * Null-language is denoted by null value, which may or may not 
	 * occur in the array.
	 * @return
	 */
	public abstract String[] getLanguages();
	public abstract Object[] getValues(String language);
	public abstract Object getValue(String language);
	abstract PropertyValues add(String language, Object value);
}

abstract class Single extends PropertyValues {
  private static final long serialVersionUID = 1L;
	public Object[] getValues(String language) {
		return new Object[] { getValue(language) };
	}
}

abstract class Many extends PropertyValues {
  private static final long serialVersionUID = 1L;
  protected static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	public Object getValue(String language) {
		Object[] values = getValues(language);
		if (values.length < 1) {
			return null;
		}
		return values[0];
	}
}

class Empty extends Many {
  private static final long serialVersionUID = 1L;
  private static final String[] EMPTY_STRING_ARRAY = new String[0];
	public Object[] getValues(String language) {
		return EMPTY_OBJECT_ARRAY;
  }
	public String[] getLanguages() {
		return EMPTY_STRING_ARRAY;
	}
  protected PropertyValues add(String language, Object value) {
  	return language == null ? new NoLanguageSingleValue(value) : new SingleLanguageSingleValue(language, value); 
  }
}

class NoLanguageSingleValue extends Single {
  private static final long serialVersionUID = 1L;
  private static final String[] NULL_STRING_ARRAY = new String[] { null };
	private final Object value;
	public NoLanguageSingleValue(Object value) {
		this.value = value;
  }
	public Object getValue(String language) {
		if (language == null) {
			return value;
		} else {
			return null;
		}
	}
	public String[] getLanguages() {
		return NULL_STRING_ARRAY;
	}
	protected PropertyValues add(String language, Object value) {
		return language == null ? new SingleLanguageMultiValue(null, this.value, value) : new MultiLanguageSingleValue(null, this.value, language, value); 
	}
}

class SingleLanguageSingleValue extends Single {
	private static final long serialVersionUID = 1L;
	private final String language;
	private final Object value;
	public SingleLanguageSingleValue(String language, Object value) {
		this.language = language;
		this.value = value;
	}
	public Object getValue(String language) {
		if (Objects.equal(this.language, language)) {
			return value;
		} else {
			return null;
		}
	}
	@Override
	public String[] getLanguages() {
		return new String[] { language };
	}
	protected PropertyValues add(String language, Object value) {
	 return Objects.equal(this.language, language) ? new SingleLanguageMultiValue(language, this.value, value) : new MultiLanguageSingleValue(this.language, this.value, language, value);
	}
}

class MultiLanguageSingleValue extends Single {
	private static final long serialVersionUID = 1L;
	private String[] languages;
	private Object[] values;
	public MultiLanguageSingleValue(String language1, Object value1, String language2, Object value2) {
		this.languages = new String[] { language1, language2 };
		this.values = new Object[] { value1, value2 };
	}
	public Object getValue(String language) {
		for (int i = 0; i < languages.length; i++) {
			if (Objects.equal(languages[i], language)) {
				return values[i];
			}
		}
		return null;
	}
	@Override
	public String[] getLanguages() {
		return languages;
	}
	protected PropertyValues add(String language, Object value) {
		for (int i = 0; i < languages.length; i++) {
			if (Objects.equal(languages[i], language)) {
				return new MultiLanguageMultiValue(languages, values, language, value);
			}
		}
		languages = Arrays.copyOf(languages, languages.length + 1);
		languages[languages.length - 1] = language;
		values = Arrays.copyOf(values, values.length + 1);
		values[values.length - 1] = value;
		return this;
	}
}
class SingleLanguageMultiValue extends Many {
	private static final long serialVersionUID = 1L;
	private String language;
	private Object[] values;
	public SingleLanguageMultiValue(String language, Object... values) {
		this.language = language;
		this.values = values;
	}
	public Object[] getValues(String language) {
		if (Objects.equal(this.language, language)) {
			return values;
		}
		return EMPTY_OBJECT_ARRAY;
	}
	@Override
	public String[] getLanguages() {
		return new String[] { language };
	}
	protected PropertyValues add(String language, Object value) {
		if (Objects.equal(this.language, language)) {
			values = Arrays.copyOf(values, values.length + 1);
			values[values.length - 1] = value;
			return this;
		} else {
			return new MultiLanguageMultiValue(this.language, values, language, value); 
		}
	}
}

class MultiLanguageMultiValue extends Many {
	private static final long serialVersionUID = 1L;
	private String[] languages;
	private Object[][] values;
	public MultiLanguageMultiValue(String language, Object[] values, String language2, Object values2) {
		this.languages = new String[] { language, language2 };
		this.values = new Object[][] { values, new Object[] { values2} };
	}
	public MultiLanguageMultiValue(String[] languages, Object[] values, String language2, Object value2) {
		this.languages = new String[languages.length + 1];
		this.values = new Object[values.length + 1][];
		for (int i = 0; i < languages.length; i++) {
			this.languages[i] = languages[i];
			this.values[i] = new Object[] { values[i] };
		}
		this.languages[languages.length] = language2;
		this.values[values.length] = new Object[] { value2 };
	}
	public Object[] getValues(String language) {
		for (int i = 0; i < languages.length; i++) {
			if (Objects.equal(languages[i], language)) {
				return values[i];
			}
		}
		return EMPTY_OBJECT_ARRAY;
	}
	@Override
	public String[] getLanguages() {
		return languages;
	}
	protected PropertyValues add(String language, Object value) {
		for (int i = 0; i < languages.length; i++) {
			if (Objects.equal(languages[i], language)) {
				values[i] = Arrays.copyOf(values[i], values[i].length + 1);
				values[i][values[i].length - 1] = value;
				return this;
			}
		}
		languages = Arrays.copyOf(languages, languages.length + 1);
		values = Arrays.copyOf(values, values.length + 1);
		languages[languages.length] = language;
		values[values.length] = new Object[] { value };
		return this;
	}
}

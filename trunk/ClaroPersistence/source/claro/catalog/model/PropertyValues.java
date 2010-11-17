package claro.catalog.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyValue;

public abstract class PropertyValues implements Serializable {

	private static final long serialVersionUID = 1L;

	public Object getValue() {
		return getValue(null);
	}
	
	public Object[] getValues() {
		return getValues(null);
	}
	
	public abstract Object[] getValues(String language);
	public abstract Object getValue(String language);
	abstract PropertyValues add(String language, PropertyValue value);
}

abstract class Single extends PropertyValues {
  private static final long serialVersionUID = 1L;
	public Object[] getValues(String language) {
		return new Object[] { getValue(language) };
	}
}

abstract class Many extends PropertyValues {
  private static final long serialVersionUID = 1L;
	public Object getValue(String language) {
		Object[] values = getValues(language);
		if (values.length < 1) {
			return null;
		}
		return values[0];
	}
}

class SingleEmpty extends Single {
  private static final long serialVersionUID = 1L;
	public Object getValue(String language) {
		return null;
  }
  protected PropertyValues add(String language, PropertyValue value) {
  	return language == null ? new SingleNullLanguageSingleValue(value) : new SingleSingleLanguageSingleValue(value); 
  }
}

class SingleNullLanguageSingleValue extends Single {
  private static final long serialVersionUID = 1L;
	private final Object value;
	public SingleNullLanguageSingleValue(Object value) {
		this.value = value;
  }
	public Object getValue(String language) {
		if (language == null) {
			return value;
		} else {
			return null;
		}
	}
}

class SingleLanguageSingleValue extends Single {
	String language;
}

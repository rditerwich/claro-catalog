package claro.catalog.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Objects;

public abstract class StaticMap<K, V> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public boolean hasValue() {
		return getValue() != null;
	}
	
	public V getValue() {
		return getValue(null);
	}
	
	public List<V> getValues() {
		return getValues(null);
	}
	
	/**
	 * Null-key is denoted by null value, which may or may not 
	 * occur in the array.
	 * @return
	 */
	public abstract List<K> getKeys();
	public abstract List<V> getValues(K key);
	public abstract V getValue(K key);
	abstract StaticMap<K, V> add(K key, V value);
	
	<T> Object[] array(Object value1) {
		return new Object[] { value1 };
	}
	
	<T> Object[] array(Object value1, Object value2) {
		return new Object[] { value1, value2 };
	}
	
	<T> Object[] array(Object[] values, Object value) {
		Object[] copy = Arrays.copyOf(values, values.length + 1);
		copy[values.length] = value;
		return copy;
	}
	
	<T> Object[][] arrays(Object[] values, Object... values2) {
		Object[][] result = new Object[values.length + values2.length][];
		for (int i = 0; i < values.length; i++) {
			result[i] = array(values[i]); 
		}
		for (int i = 0; i < values2.length; i++) {
			result[values.length + i] = array(values2[i]); 
		}
		return result;
	}
}

abstract class Single<K, V> extends StaticMap<K, V> {
  private static final long serialVersionUID = 1L;
	public List<V> getValues(K key) {
		return Collections.singletonList(getValue(key));
	}
}

abstract class Many<K, V> extends StaticMap<K, V> {
  private static final long serialVersionUID = 1L;
  protected static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	public V getValue(K key) {
		List<V> values = getValues(key);
		if (values.isEmpty()) {
			return null;
		}
		return values.get(0);
	}
}

class Empty<K, V> extends Many<K, V> {
  private static final long serialVersionUID = 1L;
	public List<V> getValues(K key) {
		return Collections.emptyList();
  }
	public List<K> getKeys() {
		return Collections.emptyList();
	}
  protected StaticMap<K, V> add(K key, V value) {
  	return key == null ? new NoKeySingleValue<K, V>(value) : new SinglekeySingleValue<K, V>(key, value); 
  }
}

class NoKeySingleValue<K, V> extends Single<K, V> {
  private static final long serialVersionUID = 1L;
	private final V value;
	public NoKeySingleValue(V value) {
		this.value = value;
  }
	public V getValue(K key) {
		if (key == null) {
			return value;
		} else {
			return null;
		}
	}
	public List<K> getKeys() {
		return Collections.singletonList(null);
	}
	protected StaticMap<K, V> add(K key, V value) {
		return key == null 
			? new SinglekeyMultiValue<K, V>(null, this.value, value) 
			: new MultikeySingleValue<K, V>(array(null, key), array(this.value, value)); 
	}
}

class SinglekeySingleValue<K, V> extends Single<K, V> {
	private static final long serialVersionUID = 1L;
	private final K key;
	private final V value;
	public SinglekeySingleValue(K key, V value) {
		this.key = key;
		this.value = value;
	}
	public V getValue(K key) {
		if (Objects.equal(this.key, key)) {
			return value;
		} else {
			return null;
		}
	}
	@Override
	public List<K> getKeys() {
		return Collections.singletonList(key);
	}
	protected StaticMap<K, V> add(K key, V value) {
		return Objects.equal(this.key, key) 
	 		? new SinglekeyMultiValue<K, V>(key, this.value, value) 
			: new MultikeySingleValue<K, V>(array(this.key, key), array(this.value, value));
	}
}

@SuppressWarnings("unchecked")
class MultikeySingleValue<K, V> extends Single<K, V> {
	private static final long serialVersionUID = 1L;
	private final Object[] keys;
	private final Object[] values;
	MultikeySingleValue(Object[] keys, Object[] values) {
		this.keys = keys;
		this.values = values;
	}
  public V getValue(K key) {
		for (int i = 0; i < keys.length; i++) {
			if (Objects.equal(keys[i], key)) {
				return (V) values[i];
			}
		}
		return null;
	}
	@Override
	public List<K> getKeys() {
		return (List<K>) Arrays.asList(keys);
	}
	protected StaticMap<K, V> add(K key, V value) {
		for (int i = 0; i < keys.length; i++) {
			if (Objects.equal(keys[i], key)) {
				return new MultikeyMultiValue<K, V>(array(keys, key), arrays(values, value) );
			}
		}
		return new MultikeySingleValue<K, V>(array(keys, key), array(values, value));
	}
}
class SinglekeyMultiValue<K, V> extends Many<K, V> {
	private static final long serialVersionUID = 1L;
	private K key;
	private Object[] values;
	public SinglekeyMultiValue(K key, Object... values) {
		this.key = key;
		this.values = values;
	}
	@SuppressWarnings("unchecked")
  public List<V> getValues(K key) {
		if (Objects.equal(this.key, key)) {
			return (List<V>) Arrays.asList(values);
		}
		return Collections.emptyList();
	}
	@Override
	public List<K> getKeys() {
		return Collections.singletonList(key);
	}
	protected StaticMap<K, V> add(K key, V value) {
		if (Objects.equal(this.key, key)) {
			return new SinglekeyMultiValue<K, V>(key, array(values, value));
		} else {
			return new MultikeyMultiValue<K, V>(array(this.key, key), arrays(values, value); 
		}
	}
}

class MultikeyMultiValue<K, V> extends Many<K, V> {
	private static final long serialVersionUID = 1L;
	private Object[] keys;
	private Object[][] values;
	public MultikeyMultiValue(Object[] keys, Object[][] values) {
		
	}
	public MultikeyMultiValue(K key, Object[] values, K key2, V values2) {
		this.keys = new String[] { key, key2 };
		this.values = new Object[][] { values, new Object[] { values2} };
	}
	public MultikeyMultiValue(String[] keys, Object[] values, K key2, V value2) {
		this.keys = new String[keys.length + 1];
		this.values = new Object[values.length + 1][];
		for (int i = 0; i < keys.length; i++) {
			this.keys[i] = keys[i];
			this.values[i] = new Object[] { values[i] };
		}
		this.keys[keys.length] = key2;
		this.values[values.length] = new Object[] { value2 };
	}
	public List<V> getValues(K key) {
		for (int i = 0; i < keys.length; i++) {
			if (Objects.equal(keys[i], key)) {
				return values[i];
			}
		}
		return EMPTY_OBJECT_ARRAY;
	}
	@Override
	public List<K> getKeys() {
		return keys;
	}
	protected StaticMap<K, V> add(K key, V value) {
		for (int i = 0; i < keys.length; i++) {
			if (Objects.equal(keys[i], key)) {
				values[i] = Arrays.copyOf(values[i], values[i].length + 1);
				values[i][values[i].length - 1] = value;
				return this;
			}
		}
		keys = Arrays.copyOf(keys, keys.length + 1);
		values = Arrays.copyOf(values, values.length + 1);
		keys[keys.length] = key;
		values[values.length] = new Object[] { value };
		return this;
	}
}

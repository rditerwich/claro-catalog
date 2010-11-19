package claro.catalog.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Objects;

/**
 * Simple, Small, Static key-value map-like class. It's optimized for 
 * memory usage (and transport size), and is tailored for the 'simple'
 * cases: when there is only one item, all items have null keys, all 
 * items have the same key, or when there is only one value per key.
 * Large content is inefficient, specially when building them. Class
 * is particularly useful for caching and transport purposes.   
 */
public abstract class SMap<K, V> implements Serializable {

	private static final long serialVersionUID = 1L;
	private static SMap<?, ?> emptyMap = new Empty<Object, Object>(); 
	
	@SuppressWarnings("unchecked")
	public static <K, V> SMap<K, V> empty() {
		return (SMap<K, V>) emptyMap;
	}
	
	public static <K, V> SMap<K, V> fromMap(Map<K, V> map) {
		if (map.isEmpty()) return empty();
		if (map.size() == 1) {
			K key = map.keySet().iterator().next();
			if (key == null) return new NoKeySingleValue<K, V>(map.get(key));
			else return new SingleKeySingleValue<K, V>(key, map.get(key));
		}
		int index = 0;
		Object[] keys = new Object[map.size()];
		Object[] values = new Object[map.size()];
		for (Entry<K, V> entry : map.entrySet()) {
			keys[index] = entry.getKey();
			values[index] = entry.getValue();
		}
		return new MultiKeySingleValue<K, V>(keys, values);
	}
	
	public static <K, V> SMap<K, V> fromMultiMap(Map<K, ? extends Collection<V>> map) {
		if (map.isEmpty()) return empty();
		if (map.size() == 1) {
			K key = map.keySet().iterator().next();
			Collection<V> value = map.get(key);
			if (value.isEmpty()) return empty();
			if (key == null && value.size() == 1) return new NoKeySingleValue<K, V>(value.iterator().next());
			if (key != null && value.size() == 1) return new SingleKeySingleValue<K, V>(key, value.iterator().next());
			if (value.size() > 1) return new SingleKeyMultiValue<K, V>(key, value.toArray());
		}
		int index = 0;
		Object[] keys = new Object[map.size()];
		Object[][] values = new Object[map.size()][];
		for (Entry<K, ? extends Collection<V>> entry : map.entrySet()) {
			keys[index] = entry.getKey();
			values[index] = entry.getValue().toArray();
		}
		return new MultiKeyMultiValue<K, V>(keys, values);
	}
	
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
	abstract SMap<K, V> add(K key, V value);
	
	<T> T[] concat(T[] values, T value) {
		T[] copy = Arrays.copyOf(values, values.length + 1);
		copy[values.length] = value;
		return copy;
	}
}

abstract class Single<K, V> extends SMap<K, V> {
	private static final long serialVersionUID = 1L;
	public List<V> getValues(K key) {
		return Collections.singletonList(getValue(key));
	}
}

abstract class Many<K, V> extends SMap<K, V> {
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
	protected SMap<K, V> add(K key, V value) {
		return key == null ? new NoKeySingleValue<K, V>(value) : new SingleKeySingleValue<K, V>(key, value); 
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
	protected SMap<K, V> add(K key, V value) {
		return key == null 
		? new SingleKeyMultiValue<K, V>(null, this.value, value) 
				: new MultiKeySingleValue<K, V>(concat(null, key), new Object[] { this.value, value }); 
	}
}

class SingleKeySingleValue<K, V> extends Single<K, V> {
	private static final long serialVersionUID = 1L;
	private final K key;
	private final V value;
	public SingleKeySingleValue(K key, V value) {
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
	protected SMap<K, V> add(K key, V value) {
		return Objects.equal(this.key, key) 
		? new SingleKeyMultiValue<K, V>(key, this.value, value) 
				: new MultiKeySingleValue<K, V>(new Object[] { this.key, key }, new Object[] { this.value, value });
	}
}

@SuppressWarnings("unchecked")
class MultiKeySingleValue<K, V> extends Single<K, V> {
	private static final long serialVersionUID = 1L;
	private final Object[] keys;
	private final Object[] values;
	MultiKeySingleValue(Object[] keys, Object[] values) {
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
	protected SMap<K, V> add(K key, V value) {
		for (int i = 0; i < keys.length; i++) {
			if (Objects.equal(keys[i], key)) {
				Object[][] valueArrays = new Object[keys.length][];
				for (int j = 0; j < keys.length; j++) {
					valueArrays[j] = i == j 
					? new Object[] { values[j], value } 
					: new Object[] { values[j] };
				}
				return new MultiKeyMultiValue<K, V>(keys, valueArrays);
			}
		}
		return new MultiKeySingleValue<K, V>(concat(keys, key), concat(values, value));
	}
}

class SingleKeyMultiValue<K, V> extends Many<K, V> {
	private static final long serialVersionUID = 1L;
	private final K key;
	private final Object[] values;
	public SingleKeyMultiValue(K key, Object... values) {
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
	protected SMap<K, V> add(K key, V value) {
		if (Objects.equal(this.key, key)) {
			return new SingleKeyMultiValue<K, V>(key, concat(values, value));
		} else {
			return new MultiKeyMultiValue<K, V>(new Object[] { this.key, key }, new Object[][] { values, new Object[] { value } }); 
		}
	}
}

class MultiKeyMultiValue<K, V> extends Many<K, V> {
	private static final long serialVersionUID = 1L;
	private final Object[] keys;
	private final Object[][] values;
	public MultiKeyMultiValue(Object[] keys, Object[][] values) {
		this.keys = keys;
		this.values = values;
	}
	@SuppressWarnings("unchecked")
	public List<V> getValues(K key) {
		for (int i = 0; i < keys.length; i++) {
			if (Objects.equal(keys[i], key)) {
				return (List<V>) Arrays.asList(values[i]);
			}
		}
		return Collections.emptyList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<K> getKeys() {
		return (List<K>) Arrays.asList(keys);
	}
	
	protected SMap<K, V> add(K key, V value) {
		for (int i = 0; i < keys.length; i++) {
			if (Objects.equal(keys[i], key)) {
				Object[][] newValues = Arrays.copyOf(values, values.length);
				newValues[i] = concat(values[i], value); 
				return new MultiKeyMultiValue<K, V>(keys, newValues);
			}
		}
		return new MultiKeyMultiValue<K, V>(concat(keys, key), concat(values, new Object[] { value }));
	}
}
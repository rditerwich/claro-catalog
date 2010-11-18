package claro.catalog.util;

import java.util.AbstractList;

import scala.actors.threadpool.Arrays;

public class StaticList<T> extends AbstractList<T> {

	private Object[] values;

	public StaticList(T... values) {
		this.values = values;
  }
	
	private StaticList(Object[] values, T value) {
		this.values = Arrays.copyOf(values, values.length + 1);
		this.values[values.length] = value;
	}
	
	@Override
	@SuppressWarnings("unchecked")
  public T get(int index) {
	  return (T) values[index];
  }

	@Override
  public int size() {
	  return values.length;
  }

	public StaticList<T> append(T value) {
		return new StaticList<T>(values, value);
	}
}

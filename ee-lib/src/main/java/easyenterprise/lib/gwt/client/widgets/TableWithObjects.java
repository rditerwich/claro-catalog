package easyenterprise.lib.gwt.client.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;

import easyenterprise.lib.util.CollectionUtil;

public class TableWithObjects<T> extends Table {

	public TableWithObjects() {
		super();
	}

	public TableWithObjects(int rows, int columns) {
		super(rows, columns);
	}

	private List<T> objects = Collections.emptyList();
	
	@Override
	public int insertRow(int beforeRow) {
		if (beforeRow < objects.size()) {
			objects.add(beforeRow, null);
		}
		return super.insertRow(beforeRow);
	}

	@Override
	public void removeRow(int row) {
		if (row < objects.size()) {
			objects.remove(row);
		}
		super.removeRow(row);
	}

	@Override
	public void setHeaderHTML(int row, int column, String text) {
	  super.setHeaderHTML(row, column, text);
	  setHeaderClass(row, column);
	}
	
	@Override
	public void setHeaderText(int row, int column, String text) {
	  super.setHeaderText(row, column, text);
    setHeaderClass(row, column);
	}

  private void setHeaderClass(int row, int col) {
    Element thead = getTHeadElement();
    if (row < thead.getChildCount()) {
      Node tr = thead.getChild(row);
      if (col < tr.getChildCount()) {
        Element th = (Element) tr.getChild(col);
        th.setClassName("col" + col);
      }
    }
  }
	
	@Override
	public void resizeRows(int rows) {
	  int oldRows = numRows;
	  super.resizeRows(rows);
	  for (int row = oldRows; row < rows; row++) {
	    for (int col = 0; col < getColumnCount(); col++) {
	      getCellFormatter().setStylePrimaryName(row, col, "col" + col);
	    }
	  }
	  while (objects.size() > rows) objects.remove(objects.size() - 1);
	}
	
	public T getObject(int row) {
		if (row < objects.size()) {
			return objects.get(row);
		}
		return null;
	}
	
	public void setObject(int row, T object) {
		if (objects.isEmpty()) {
			objects = new ArrayList<T>();
		}
		while (objects.size() <= row) {
			objects.add(null);
		}
		objects.set(row, object);
	}
	
	public int findObject(T object) {
		int index = CollectionUtil.indexOfRef(objects, object);
		if (index != -1) return index;
		return objects.indexOf(object);
	}
}

package claro.catalog.model;

import java.util.Comparator;

import claro.jpa.catalog.ImportSource;

public class ImportSourceComparator implements Comparator<ImportSource> {

	public static ImportSourceComparator instance = new ImportSourceComparator();
	
	private ImportSourceComparator() {
  }

	public int compare(ImportSource o1, ImportSource o2) {
		if (o1 == o2) return 0;
		if (o1 == null) return -1;
		if (o2 == null) return 1;
		int i1 = o1.getPriority() != null ? o1.getPriority() : 0; 
		int i2 = o2.getPriority() != null ? o2.getPriority() : 0;
		if (i1 < i2) return 1;
		if (i1 > i2) return -1;
	  return 0;
  }
}

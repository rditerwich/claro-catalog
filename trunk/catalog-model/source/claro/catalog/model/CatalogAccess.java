package claro.catalog.model;

import java.util.ArrayDeque;
import java.util.Queue;

public class CatalogAccess {
	
	private static final ThreadLocal<CatalogAccess> threadInstance = new ThreadLocal<CatalogAccess>();

	final CatalogDao dao;
	final boolean updating;
	final Queue<ItemModel> invalidItems = new ArrayDeque<ItemModel>();
	
	private CatalogAccess(CatalogDao dao, boolean updating) {
		this.dao = dao;
		this.updating = updating;
	}

	static CatalogDao getDao() {
		return getInstance(false).dao;
	}
	
	static CatalogDao getDaoForUpdating() {
		return getInstance(true).dao;
	}

	static Queue<ItemModel> getInvalidItems() {
		return getInstance(true).invalidItems;
	}
	
	static CatalogAccess getInstance(boolean updating) {
		CatalogAccess updateStatus = threadInstance.get();
		if (updating && (updateStatus == null || !updateStatus.updating)) {
			throw new RuntimeException("No catalog update allowed. Please call " + CatalogModel.class.getName() + ".update");
		}
		if (updateStatus == null) {
			throw new RuntimeException("No catalog access allowed. Please call " + CatalogModel.class.getName() + ".access or " + CatalogModel.class.getName() + ".update");
		}
		return updateStatus;
	}
	
	static void access(CatalogDao dao, Runnable runnable, boolean writable) {
		CatalogAccess accessInfo = threadInstance.get();
		if (accessInfo != null) {
			throw new RuntimeException("No nested catalog access allowed");
		}
		accessInfo = new CatalogAccess(dao, writable);
		threadInstance.set(accessInfo);
		try {
			runnable.run();
		} finally {
			threadInstance.set(null);
			for (ItemModel item : accessInfo.invalidItems) {
				item.invalidate();
			}
		}
	}
}

package claro.catalog.model;

import java.util.ArrayDeque;
import java.util.Queue;

import claro.catalog.CatalogDao;

public class CatalogAccess {
	
	private static final ThreadLocal<CatalogAccess> threadInstance = new ThreadLocal<CatalogAccess>();

	final CatalogDao dao;
	final Queue<ItemModel> invalidItems = new ArrayDeque<ItemModel>();
	
	private CatalogAccess(CatalogDao dao) {
		this.dao = dao;
	}

	static CatalogDao getDao() {
		return getInstance().dao;
	}
	
	static Queue<ItemModel> getInvalidItems() {
		return getInstance().invalidItems;
	}
	
	static CatalogAccess getInstance() {
		CatalogAccess updateStatus = threadInstance.get();
		if (updateStatus == null) {
			throw new RuntimeException("No catalog access allowed. Please call " + CatalogModel.class.getName() + ".startOperation(...)");
		}
		return updateStatus;
	}
	
	static void startOperation(CatalogDao dao) {
		CatalogAccess accessInfo = threadInstance.get();
		if (accessInfo != null) {
			throw new RuntimeException("No nested catalog access allowed");
		}
		accessInfo = new CatalogAccess(dao);
		threadInstance.set(accessInfo);
	}
	
	static void endOperation() {
		CatalogAccess accessInfo = threadInstance.get();
		threadInstance.set(null);
		for (ItemModel item : accessInfo.invalidItems) {
			item.invalidate();
		}
	}
}

package claro.catalog;

import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandWrapper;

public class CatalogDaoService extends CommandWrapper {

	private static final ThreadLocal<CatalogDao> daoPerThread = new ThreadLocal<CatalogDao>();
	private final CatalogDao dao;
	
	public CatalogDaoService(CatalogDao dao, CommandWrapper delegate) {
		super(delegate);
		this.dao = dao;
	}
	
	public static CatalogDao getCatalogDao() {
		CatalogDao catalogDao = daoPerThread.get();
		if (catalogDao == null) {
			throw new RuntimeException("No catalog server running");
		}
		return catalogDao;
	}
	
	public <T extends CommandResult, I extends CommandImpl<T>> T executeImpl(I command) throws CommandException {
		CatalogDao originalDao = daoPerThread.get();
		daoPerThread.set(dao);
		try {
			return super.executeImpl(command);
		}
		finally {
			daoPerThread.set(originalDao);
			dao.commitTransaction();
		}
	}
}

package claro.catalog;

import java.util.Map;

import claro.catalog.model.CatalogModel;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandWrapper;

public class CatalogModelService extends CommandWrapper {

	private static final ThreadLocal<State> stateLocal = new ThreadLocal<State>();

	private Map<Long, CatalogModel> catalogModels = new MapMaker().makeComputingMap(new Function<Long, CatalogModel>() {
		public CatalogModel apply(Long catalogId) {
				return new CatalogModel(catalogId, server.getCatalogDao());
		}
	});

	private final CatalogServer server;
	
	public CatalogModelService(CatalogServer server, CommandWrapper delegate) {
		super(delegate);
		this.server = server;
	}
	
	/**
	 * Get the catalog model for <code>catalogId</code>.  Must only be called from commands that are executed by this service.
	 * @param catalogId
	 * @return
	 */
	public static CatalogModel getCatalogModel(Long catalogId) {
		if (catalogId == null) {
			throw new RuntimeException("No catalog id specified");
		}
		State state = stateLocal.get();
		if (state == null) {
			throw new RuntimeException("Catalog server not running.");
		}
		CatalogModel catalogModel = state.service.catalogModels.get(catalogId);
		
		if (!state.operationStarted && !state.parentHasStartedOperation) {
			// TODO startOpertion not static
			CatalogModel.startOperation(catalogModel.dao);
			state.operationStarted = true;
		}
		return catalogModel;
	}
	
	// TODO dirty, do differently
	public static void clearCatalogModel(Long catalogId) {
			State state = stateLocal.get();
			if (state != null) {
				state.service.catalogModels.remove(catalogId);
			}
	}

	public <T extends CommandResult, I extends CommandImpl<T>> T executeImpl(I command) throws CommandException {
		State oldState = stateLocal.get();
		State state = new State();
		state.service = this;
		state.parentHasStartedOperation = oldState != null && oldState.operationStarted;
		stateLocal.set(state);
		try {
			return super.executeImpl(command);
		}
		finally {
			if (state.operationStarted) {
				stateLocal.set(oldState);
				CatalogModel.endOperation();
			}
		}
	}
	
	public static class State {
		CatalogModelService service;
		boolean parentHasStartedOperation;
		boolean operationStarted;
		
	}
}
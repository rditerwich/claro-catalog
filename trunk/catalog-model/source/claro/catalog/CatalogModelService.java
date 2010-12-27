package claro.catalog;

import java.util.Map;

import claro.catalog.model.CatalogModel;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandWrapper;
import easyenterprise.lib.command.jpa.JpaService;

public class CatalogModelService extends CommandWrapper {

	private static final ThreadLocal<State> stateLocal = new ThreadLocal<State>() {
		protected State initialValue() {
			return new State();
		};
	};

	private static Map<Long, CatalogModel> catalogModels = new MapMaker().makeComputingMap(new Function<Long, CatalogModel>() {
		public CatalogModel apply(Long id) {
				return new CatalogModel(id);
		}
	});
	
	public CatalogModelService(CommandWrapper delegate) {
		super(delegate);
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
		if (!state.operationStarted && !state.parentHasStartedOperation) {
			CatalogModel.startOperation(createDao());
			state.operationStarted = true;
		}
		return catalogModels.get(catalogId);
	}

	private static CatalogDao createDao() {
		return new CatalogDao(JpaService.getEntityManager());
	}

	public <T extends CommandResult, I extends CommandImpl<T>> T executeImpl(I command) throws CommandException {
		State oldState = stateLocal.get();
		State state = new State();
		state.parentHasStartedOperation = oldState.operationStarted;
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
		boolean parentHasStartedOperation;
		boolean operationStarted;
	}
}
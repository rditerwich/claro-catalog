package claro.catalog;

import java.util.Map;

import claro.catalog.command.CatalogCommand;
import claro.catalog.model.CatalogDao;
import claro.catalog.model.CatalogModel;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandWrapper;
import easyenterprise.lib.command.jpa.JpaService;

public class CatalogModelService extends CommandWrapper {

	private static Map<Long, CatalogModel> catalogModels = new MapMaker().makeComputingMap(new Function<Long, CatalogModel>() {
		public CatalogModel apply(Long id) {
//			CatalogModel.startOperation(createDao());
//			try {
				return new CatalogModel(id);
//			}
//			finally {
//				CatalogModel.endOperation();
//			}
		}
	});
	
	public CatalogModelService(CommandWrapper delegate) {
		super(delegate);
	}
	
	public static CatalogModel getCatalogModel(Long catalogId) {
		return catalogModels.get(catalogId);
	}

	private static CatalogDao createDao() {
		return new CatalogDao(JpaService.getEntityManager());
	}

	public <T extends CommandResult, I extends CommandImpl<T>> T executeImpl(I command) throws CommandException {
		if (command instanceof CatalogCommand) {
			CatalogModel.startOperation(createDao());
			try {
				return super.executeImpl(command);
			}
			finally {
				CatalogModel.endOperation();
			}
		} else {
			return super.execute(command);
		}
	}
}
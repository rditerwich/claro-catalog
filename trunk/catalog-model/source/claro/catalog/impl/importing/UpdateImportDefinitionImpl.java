package claro.catalog.impl.importing;

import claro.catalog.command.importing.UpdateImportDefinition;
import claro.catalog.model.CatalogDao;
import claro.jpa.importing.ImportDefinition;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.jpa.JpaService;

public class UpdateImportDefinitionImpl extends UpdateImportDefinition implements CommandImpl<UpdateImportDefinition.Result> {

	@Override
	public Result execute() throws CommandException {
		CatalogDao dao = new CatalogDao(JpaService.getEntityManager());
		if (importDefinition != null) {
			ImportDefinition definition = dao.findOrCreate(ImportDefinition.class, importDefinition.getId());
			
		}
		return new Result();
	}

}

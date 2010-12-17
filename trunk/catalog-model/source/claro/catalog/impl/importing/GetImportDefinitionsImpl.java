package claro.catalog.impl.importing;

import claro.catalog.command.importing.GetImportDefinitionsCommand;
import claro.catalog.command.importing.GetImportDefinitionsResult;
import claro.catalog.model.CatalogDao;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.jpa.JpaService;

public class GetImportDefinitionsImpl extends GetImportDefinitionsCommand implements CommandImpl<GetImportDefinitionsResult>{

	@Override
	public GetImportDefinitionsResult execute() throws CommandException {
		
		CatalogDao dao = new CatalogDao(JpaService.getEntityManager());
		
		dao.getImportDefinitions(paging);
		
		return new GetImportDefinitionsResult();
	}

}

package claro.catalog.impl.importing;

import java.util.Collections;

import claro.catalog.CatalogDao;
import claro.catalog.command.importing.GetImportSources;
import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.Cloner;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.jpa.JpaService;

public class GetImportSourcesImpl extends GetImportSources implements CommandImpl<GetImportSources.Result>{

	private static final long serialVersionUID = 1L;
	static View view = new BasicView("job", "rules/fileFormat", "rules/tabularFileFormat", "rules/xmlFileFormat", "rules/importProducts/matchProperty", "rules/importProducts/categories/categoryExpression", "rules/importProducts/properties/property", "rules/importProducts/properties/importProducts");

	@Override
	public Result execute() throws CommandException {
		checkValid();
		
		Result result = new Result();
		CatalogDao dao = new CatalogDao(JpaService.getEntityManager());
		if (importSourceId != null) {
			result.importSources = Collections.singletonList(dao.getImportSourceById(importSourceId));
		} else if (ImportSourceName != null) {
			result.importSources = dao.getImportSourcesByName(ImportSourceName, paging);
		} else {
			result.importSources = dao.getImportSources(paging);
		}
		
		result.importSources = Cloner.clone(result.importSources, view);
		return result;
	}

}

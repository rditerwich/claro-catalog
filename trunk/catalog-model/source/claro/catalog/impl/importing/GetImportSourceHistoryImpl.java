package claro.catalog.impl.importing;

import java.util.Collections;

import claro.catalog.CatalogDao;
import claro.catalog.command.importing.GetImportSourceHistory;
import claro.jpa.importing.ImportJobResult;
import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.Cloner;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.jpa.JpaService;

public class GetImportSourceHistoryImpl extends GetImportSourceHistory implements CommandImpl<GetImportSourceHistory.Result> {

	private static final long serialVersionUID = 1L;
	private static View view = new BasicView();

	@Override
	public Result execute() throws CommandException {
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Result result = new Result();
		
		if (jobResultId != null) {
			result.jobResults = Collections.singletonList(JpaService.getEntityManager().find(ImportJobResult.class, jobResultId));
		} else {
			result.jobResults = CatalogDao.get().getImportSourceHistory(importSourceId, paging);
		}
		
		result.jobResults = Cloner.clone(result.jobResults, view);
		return result;
	}

}

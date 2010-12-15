package claro.catalog.command.importing;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.util.Paging;

public class GetImportDefinitionsCommand implements Command<GetImportDefinitionsResult> {

	private static final long serialVersionUID = 1L;
	
	public Paging paging;
}

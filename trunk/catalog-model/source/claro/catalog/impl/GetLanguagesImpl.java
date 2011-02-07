package claro.catalog.impl;

import claro.catalog.CatalogModelService;
import claro.catalog.command.GetLanguages;
import claro.catalog.model.CatalogModel;
import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.Cloner;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;

public class GetLanguagesImpl extends GetLanguages implements CommandImpl<GetLanguages.Result>{

	private static final long serialVersionUID = 1L;
	static View view = new BasicView("languages");

	@Override
	public Result execute() throws CommandException {
		checkValid();
		
		Result result = new Result();
		CatalogModel catalogModel = CatalogModelService.getCatalogModel(catalogId);

		result.languages = Cloner.clone(catalogModel.getCatalog(), view).getLanguages();
		return result;
	}

}

package claro.catalog.impl;

import claro.catalog.CatalogModelService;
import claro.catalog.command.FlushCatalogModel;
import claro.catalog.model.CatalogModel;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;

public class FlushCatalogModelImpl extends FlushCatalogModel implements CommandImpl<FlushCatalogModel.Result>{

	private static final long serialVersionUID = 1L;

	@Override
	public Result execute() throws CommandException {
		checkValid();
		
		CatalogModel catalogModel = CatalogModelService.getCatalogModel(catalogId);
		catalogModel.invalidateAllItems();
		catalogModel.flush();
		
		Result result = new Result();
		return result;
	}
}

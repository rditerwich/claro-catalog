package claro.catalog.impl;

import claro.catalog.CatalogModelService;
import claro.catalog.command.RootPropertiesCommand;
import claro.catalog.command.RootPropertiesCommandResult;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.catalog.util.CatalogCommand;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.util.SMap;

public class RootPropertiesCommandImpl extends RootPropertiesCommand implements CommandImpl<RootPropertiesCommandResult>, CatalogCommand {

	private static final long serialVersionUID = 1L;

	@Override
	public RootPropertiesCommandResult execute() throws CommandException {
		RootPropertiesCommandResult result = new RootPropertiesCommandResult();

		// Get root item from catalog:
		CatalogModel catalogModel = CatalogModelService.getCatalogModel(getCatalogId());
		ItemModel rootItem = catalogModel.getRootItem();

		// Fill result
		result.rootProperties = SMap.empty();
		for (PropertyModel property : rootItem.getProperties()) {
			result.rootProperties.add(property.getPropertyInfo().labels.get(), property.getPropertyInfo());
		}
		
		return result;
	}
}

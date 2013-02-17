package claro.catalog.impl;

import java.util.Map.Entry;

import claro.catalog.CatalogModelService;
import claro.catalog.command.RootDataCommand;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.impl.items.ItemUtil;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.catalog.util.CatalogCommand;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.util.SMap;

public class RootDataCommandImpl extends RootDataCommand implements CommandImpl<RootDataCommand.Result>, CatalogCommand {

	private static final long serialVersionUID = 1L;

	@Override
	public Result execute() throws CommandException {
		Result result = new Result();

		// Get root item from catalog:
		CatalogModel catalogModel = CatalogModelService.getCatalogModel(getCatalogId());
		ItemModel rootItem = catalogModel.getRootItem();

		// Fill result
		result.generalGroup = catalogModel.findOrCreatePropertyGroupInfo(catalogModel.generalPropertyGroup);
		result.rootCategory = catalogModel.getRootItem().getItemId();
		result.rootCategoryLabels = ItemUtil.getNameLabels(catalogModel.getRootItem(), catalogModel, null, null);
		result.rootProperties = SMap.empty();
		for (Entry<PropertyGroupInfo, PropertyModel> property : rootItem.getProperties()) {
			result.rootProperties = result.rootProperties.add(property.getValue().getPropertyInfo().labels.get(), property.getValue().getPropertyInfo());
		}
		
		return result;
	}
}

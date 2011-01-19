package claro.catalog.impl.items;

import java.util.LinkedHashMap;
import java.util.Map;

import claro.catalog.CatalogModelService;
import claro.catalog.command.items.ItemDetailsCommand;
import claro.catalog.command.items.ItemDetailsCommandResult;
import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.catalog.util.CatalogCommand;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.PropertyGroup;
import claro.jpa.catalog.StagingArea;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.jpa.JpaService;
import easyenterprise.lib.util.SMap;

public class ItemDetailsCommandImpl extends ItemDetailsCommand implements CommandImpl<ItemDetailsCommandResult>, CatalogCommand {

	private static final long serialVersionUID = 1L;

	public ItemDetailsCommandResult execute() throws CommandException {
		
		// Obtain entities for input:
		
		StagingArea area = null;
		if (getStagingArea() != null) {
			area = JpaService.getEntityManager().find(StagingArea.class, getStagingArea());
			if (area == null) {
				throw new CommandException("Area not found");
			}
		}

		OutputChannel channel = null;
		if (getOutputChannel() != null) {
			channel = JpaService.getEntityManager().find(OutputChannel.class, getOutputChannel());
			if (channel == null) {
				throw new CommandException("Channel not found");
			}
		}

		// Obtain model for item:
		CatalogModel catalogModel = CatalogModelService.getCatalogModel(getCatalogId());
		ItemModel itemModel = catalogModel.getItem(getItem());
		
		// Convert item model to result.
		ItemDetailsCommandResult result = new ItemDetailsCommandResult();
		
		result.categories = ItemUtil.parents(itemModel, catalogModel, area, channel);
		
		// Properties.
		result.propertyData = ItemUtil.propertyData(itemModel, area, channel);
		
		return result;
	}
}

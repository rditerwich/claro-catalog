package claro.catalog.impl;

import claro.catalog.CatalogModelService;
import claro.catalog.command.CatalogCommand;
import claro.catalog.command.ItemDetailsCommand;
import claro.catalog.command.ItemDetailsCommandResult;
import claro.catalog.data.PropertyData;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.StagingArea;
import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandService;
import easyenterprise.lib.command.jpa.JpaService;
import easyenterprise.lib.util.SMap;

public class ItemDetailsCommandImpl extends ItemDetailsCommand implements CommandImpl<ItemDetailsCommandResult>, CatalogCommand {

	private static final long serialVersionUID = 1L;
	private static final View propertyInfoView = new BasicView();
	private static final View valuesView = new BasicView();
	private static final View effectiveValuesView = new BasicView();
	private static final View importValuesView = new BasicView();

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
		
		result.propertyData = SMap.empty();
		for (PropertyModel property : itemModel.getPropertyExtent()) {
			PropertyData propertyData = new PropertyData();
			
			// fill propertyData
			// TODO Ruud: is the clone necessary?
			propertyData.values = SMap.create(channel, CommandService.clone(property.getValues(area, channel), valuesView));
			propertyData.effectiveValues = SMap.create(area, SMap.create(channel, CommandService.clone(property.getEffectiveValues(area, channel), effectiveValuesView)));
			
			// TODO How to do this???
//			propertyData.importSourceValues = SMap.create(channel, CommandService.clone(property.getImportSourceValues(null), importValuesView));

			// Add propertyinfo to result
			result.propertyData.add(CommandService.clone(property.getPropertyInfo(), propertyInfoView), propertyData);
		}
		
		return result;
	}

}

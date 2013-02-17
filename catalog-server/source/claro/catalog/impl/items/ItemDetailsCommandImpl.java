package claro.catalog.impl.items;

import javax.persistence.EntityManager;

import claro.catalog.CatalogDaoService;
import claro.catalog.CatalogModelService;
import claro.catalog.command.items.ItemDetailsCommand;
import claro.catalog.data.ItemType;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.util.CatalogCommand;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.StagingArea;
import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.Printer;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;

public class ItemDetailsCommandImpl extends ItemDetailsCommand implements CommandImpl<ItemDetailsCommand.Result>, CatalogCommand {

	private static final long serialVersionUID = 1L;
	static View shopView = new BasicView();

	public ItemDetailsCommand.Result execute() throws CommandException {
		
		// Obtain entities for input:
		EntityManager entityManager = CatalogDaoService.getCatalogDao().getEntityManager();
		
		StagingArea area = null;
		if (stagingAreaId != null) {
			area = entityManager.find(StagingArea.class, stagingAreaId);
			if (area == null) {
				throw new CommandException("Area not found");
			}
		}

		OutputChannel channel = null;
		if (outputChannelId != null) {
			channel = entityManager.find(OutputChannel.class, outputChannelId);
			if (channel == null) {
				throw new CommandException("Channel not found");
			}
		}

		// Obtain model for item:
		CatalogModel catalogModel = CatalogModelService.getCatalogModel(getCatalogId());
		ItemModel itemModel = catalogModel.getItem(itemId);
		
		// Convert item model to result.
		ItemDetailsCommand.Result result = new ItemDetailsCommand.Result();
		
		result.parents = ItemUtil.parents(itemModel, catalogModel, area, channel, itemModel.isCategory());
		result.parentExtentWithSelf = ItemUtil.parentExtent(itemModel, catalogModel, area, channel, true);
		result.groups = ItemUtil.groups(itemModel, catalogModel, area, channel);
		result.propertyData = ItemUtil.propertyData(catalogModel, itemModel, area, channel);
		if (!itemModel.isCategory()) {
			result.promotions = ItemUtil.promotions(itemModel, catalogModel, area, channel);
		}
		if (itemModel.isProduct()) {
			result.masterValues = ItemUtil.effectivePropertyValues(itemModel, area, channel);
		}

		System.out.println("ItemDetailsCommand: " + Printer.print(result, new BasicView("parents", "parentExtentWithSelf", "groups", "propertyData", "promotions")));
		
		return result;
	}
	@Override
	public Long getCatalogId() {
		return catalogId;
	}
}

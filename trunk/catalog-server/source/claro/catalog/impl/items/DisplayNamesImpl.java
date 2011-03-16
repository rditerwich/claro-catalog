package claro.catalog.impl.items;

import javax.persistence.EntityManager;

import claro.catalog.CatalogModelService;
import claro.catalog.command.items.DisplayNames;
import claro.catalog.model.CatalogModel;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.StagingArea;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.util.SMap;

public class DisplayNamesImpl extends DisplayNames implements CommandImpl<DisplayNames.Result>{

	private static final long serialVersionUID = 1L;

	@Override
	public Result execute() throws CommandException {
		
		CatalogModel catalogModel = CatalogModelService.getCatalogModel(catalogId);
		EntityManager entityManager = catalogModel.dao.getEntityManager();
		
		// Initialize parameter entities:
		StagingArea stagingArea = null;
		if (stagingAreaId != null) {  
			stagingArea = entityManager.find(StagingArea.class, stagingAreaId);
			if (stagingArea == null) {
				throw new CommandException("Area not found");
			}
		}

		OutputChannel outputChannel = null;
		if (outputChannelId != null) {
			outputChannel = entityManager.find(OutputChannel.class, outputChannelId);
			if (outputChannel == null) {
				throw new CommandException("Channel not found");
			}
		}

		
		Result result = new Result();
		result.items = SMap.empty();
		for (Long itemId : itemIds) {
			result.items = result.items.add(itemId, ItemUtil.getNameLabels(catalogModel.getItem(itemId), catalogModel, outputChannel, stagingArea));
		}
		return result;
	}

}

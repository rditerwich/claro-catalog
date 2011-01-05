package claro.catalog.impl.items;

import claro.catalog.CatalogModelService;
import claro.catalog.command.items.GetCategoryTree;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.StagingArea;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.jpa.JpaService;
import easyenterprise.lib.util.SMap;

public class GetCategoryTreeImpl extends GetCategoryTree implements CommandImpl<GetCategoryTree.Result> {

	private static final long serialVersionUID = 1L;

	private CatalogModel catalogModel;
	private StagingArea stagingArea;
	private OutputChannel outputChannel;
	private Result result;

	@Override
	public Result execute() throws CommandException {
		
		catalogModel = CatalogModelService.getCatalogModel(catalogId);

		// Initialize parameter entities:
		stagingArea = null;
		if (stagingAreaId != null) {  
			stagingArea = JpaService.getEntityManager().find(StagingArea.class, stagingAreaId);
			if (stagingArea == null) {
				throw new CommandException("Area not found");
			}
		}

		outputChannel = null;
		if (outputChannelId != null) {
			outputChannel = JpaService.getEntityManager().find(OutputChannel.class, outputChannelId);
			if (outputChannel == null) {
				throw new CommandException("Channel not found");
			}
		}

		
		result = new Result();
		result.categories = SMap.empty();
		result.children = SMap.empty();
		
		collectTree();
		
		return result;
	}

	private void collectTree() {
		addCategory(catalogModel.getRootItem());
	}

	private void addCategory(ItemModel item) {
		// Is it the right type?
		if (!item.getItemClass().equals(Category.class)) {
			return;
		}
		
		// Do we already have it?
		SMap<String, String> itemLabels = result.categories.get(item.getItemId());
		if (itemLabels != null) {
			return;
		}
		
		// Add item
		result.categories = result.categories.add(item.getItemId(), ItemUtil.getNameLabels(item, catalogModel, outputChannel, stagingArea));
		
		// set and traverse children
		for (ItemModel child : item.getChildren()) {
			result.children = result.children.add(item.getItemId(), child.getItemId());
			
			addCategory(child);
		}
	}

}

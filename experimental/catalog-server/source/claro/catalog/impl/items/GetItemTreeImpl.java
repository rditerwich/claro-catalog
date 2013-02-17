package claro.catalog.impl.items;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.EntityManager;

import claro.catalog.CatalogModelService;
import claro.catalog.command.items.GetItemTree;
import claro.catalog.data.ItemData;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.StagingArea;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.util.SMap;

public class GetItemTreeImpl extends GetItemTree implements CommandImpl<GetItemTree.Result> {

	private static final long serialVersionUID = 1L;

	private CatalogModel catalogModel;
	private StagingArea stagingArea;
	private OutputChannel outputChannel;
	private Result result;

	@Override
	public Result execute() throws CommandException {
		
		catalogModel = CatalogModelService.getCatalogModel(catalogId);
		EntityManager entityManager = catalogModel.dao.getEntityManager();
		
		// Initialize parameter entities:
		stagingArea = null;
		if (stagingAreaId != null) {  
			stagingArea = entityManager.find(StagingArea.class, stagingAreaId);
			if (stagingArea == null) {
				throw new CommandException("Area not found");
			}
		}

		outputChannel = null;
		if (outputChannelId != null) {
			outputChannel = entityManager.find(OutputChannel.class, outputChannelId);
			if (outputChannel == null) {
				throw new CommandException("Channel not found");
			}
		}

		
		result = new Result();
		result.root = rootId == null? catalogModel.getRootItem().getItemId() : rootId;
		result.items = SMap.empty();
		result.children = new LinkedHashMap<Long, List<Long>>();
		
		collectTree();
		
		return result;
	}

	private void collectTree() {
		addItem(catalogModel.getItem(result.root), 0);
	}

	private void addItem(ItemModel item, int currentDepth) {
		// Excluded?  TODO This excludes entire subtree, should we still traverse children?
		// isEmpty check to prevent needlessly getting entity.
		if (outputChannel != null && !outputChannel.getExcludedItems().isEmpty() && outputChannel.getExcludedItems().contains(item.getEntity())) {
			return;
		}
		
		// Do we already have it?
		ItemData itemSeen = result.items.get(item.getItemId());
		if (itemSeen != null) {
			return;
		}
		
		// Add item
		result.items = result.items.add(item.getItemId(), new ItemData(item.getItemId(), item.getItemType(), ItemUtil.getNameLabels(item, catalogModel, outputChannel, stagingArea)));
		
		// set and traverse children
		if (depth == -1 || currentDepth < depth) {
			ArrayList<Long> children = new ArrayList<Long>();
			result.children.put(item.getItemId(), children);
			
			for (ItemModel child : item.getChildren()) {
				if (!categoriesOnly || child.getItemClass().equals(Category.class)) {
	
					children.add(child.getItemId());
					
					addItem(child, currentDepth + 1);
				}
			}
		}
	}

}

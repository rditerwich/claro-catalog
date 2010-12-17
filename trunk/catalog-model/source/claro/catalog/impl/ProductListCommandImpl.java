package claro.catalog.impl;

import java.util.List;
import java.util.Set;

import claro.catalog.CatalogModelService;
import claro.catalog.command.ProductListCommand;
import claro.catalog.command.ProductListCommandResult;
import claro.catalog.data.PropertyInfo;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.StagingArea;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.jpa.JpaService;
import easyenterprise.lib.util.SMap;

public class ProductListCommandImpl extends ProductListCommand implements CommandImpl<ProductListCommandResult> {

	private static final long serialVersionUID = 1L;

	@Override
	public ProductListCommandResult execute() throws CommandException {
		ProductListCommandResult result = new ProductListCommandResult();

		// Get entities:
		StagingArea stagingArea = null;
		if (stagingAreaId != null) {  
			stagingArea = JpaService.getEntityManager().find(StagingArea.class, stagingAreaId);
			if (stagingArea == null) {
				throw new CommandException("Area not found");
			}
		}

		OutputChannel outputChannel = null;
		if (outputChannelId != null) {
			outputChannel = JpaService.getEntityManager().find(OutputChannel.class, outputChannelId);
			if (outputChannel == null) {
				throw new CommandException("Channel not found");
			}
		}

		CatalogModel catalogModel = CatalogModelService.getCatalogModel(catalogId);

		// Compile product list:
		// TODO Should we find categories as well, and add all products in the children extent?
		List<ItemModel> products = catalogModel.findItems(stagingArea, outputChannel, uiLanguage, language, filterString, paging);
		
		// Collect effective values:
		result.products = SMap.empty();
		for (ItemModel product : products) {
			SMap<PropertyInfo, SMap<String, Object>> propertyValues = SMap.empty();
			result.products.add(product.getItemId(), propertyValues);
			Set<PropertyModel> properties = product.getPropertyExtent();
			for (PropertyModel property : properties) {
				propertyValues.add(property.getPropertyInfo(), property.getEffectiveValues(stagingArea, outputChannel));
			}
		}
		
		return result;
	}
}

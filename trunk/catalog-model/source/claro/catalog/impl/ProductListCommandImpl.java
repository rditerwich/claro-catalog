package claro.catalog.impl;

import java.util.List;

import claro.catalog.CatalogModelService;
import claro.catalog.command.ProductListCommand;
import claro.catalog.command.ProductListCommandResult;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.util.SMap;

public class ProductListCommandImpl extends ProductListCommand implements CommandImpl<ProductListCommandResult> {

	private static final long serialVersionUID = 1L;

	@Override
	public ProductListCommandResult execute() throws CommandException {
		ProductListCommandResult result = new ProductListCommandResult();

		CatalogModel catalogModel = CatalogModelService.getCatalogModel(getCatalogId());
		
		

		// Compile product list:
		// TODO
		List<ItemModel> products = null;
		
		// Collect effective values:
		result.products = SMap.empty();
		for (ItemModel product : products) {
			// TODO.
//			result.products.add(product.getItemId(), SMap.empty());
//			product.getProperties()
		}
		
		return result;
	}
}

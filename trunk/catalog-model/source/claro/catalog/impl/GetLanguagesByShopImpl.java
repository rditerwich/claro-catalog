package claro.catalog.impl;

import claro.catalog.CatalogModelService;
import claro.catalog.command.GetLanguagesByShop;
import claro.catalog.model.CatalogModel;
import claro.jpa.catalog.Language;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.shop.Shop;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.util.SMap;

public class GetLanguagesByShopImpl extends GetLanguagesByShop implements CommandImpl<GetLanguagesByShop.Result>{

	private static final long serialVersionUID = 1L;

	@Override
	public Result execute() throws CommandException {
		checkValid();
		
		Result result = new Result();
		CatalogModel catalogModel = CatalogModelService.getCatalogModel(catalogId);

		result.languages = SMap.empty();
		for (Language language : catalogModel.getCatalog().getLanguages()) {
			result.languages = result.languages.add(null, language.getName());
		}
		
		for (OutputChannel channel : catalogModel.getCatalog().getOutputChannels()) {
			if (channel instanceof Shop) {
				Shop shop = (Shop) channel;
				
				for (Language language : shop.getLanguages()) {
					result.languages = result.languages.add(shop, language.getName());
				}
			}
		}
		return result;
	}
}

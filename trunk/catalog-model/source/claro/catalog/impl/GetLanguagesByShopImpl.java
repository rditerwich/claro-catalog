package claro.catalog.impl;

import claro.catalog.CatalogModelService;
import claro.catalog.command.GetLanguagesByShop;
import claro.catalog.model.CatalogModel;
import claro.jpa.catalog.Language;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.shop.Shop;
import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.Cloner;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.util.SMap;

public class GetLanguagesByShopImpl extends GetLanguagesByShop implements CommandImpl<GetLanguagesByShop.Result>{

	private static final long serialVersionUID = 1L;
	static View view = new BasicView();

	@Override
	public Result execute() throws CommandException {
		checkValid();
		
		Result result = new Result();
		CatalogModel catalogModel = CatalogModelService.getCatalogModel(catalogId);

		result.languages = SMap.empty();
		result.languages = result.languages.set(null, null); // Add default language
		for (Language language : catalogModel.getCatalog().getLanguages()) {
			result.languages = result.languages.add(null, language.getName());
		}
		
		for (OutputChannel channel : catalogModel.getCatalog().getOutputChannels()) {
			if (channel instanceof Shop) {
				Shop shop = Cloner.clone((Shop) channel, view);
				
				result.languages = result.languages.set(shop, null); // Add default language
				for (Language language : shop.getLanguages()) {
					result.languages = result.languages.add(shop, language.getName());
				}
			}
		}
		return result;
	}
}

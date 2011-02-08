package claro.catalog.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.base.Objects;

import claro.catalog.CatalogModelService;
import claro.catalog.command.GetLanguagesByShop;
import claro.catalog.model.CatalogModel;
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
		
		// TODO We should iterate over catalog languages, but use union of shop languages instead.
//		for (String language : catalogModel.getCatalog().getLanguages().split(",") {
//			result.languages = result.languages.add(null, language);
//		}
		
		Set<String> languages = new LinkedHashSet<String>();
		
		for (OutputChannel channel : catalogModel.getCatalog().getOutputChannels()) {
			if (channel instanceof Shop) {
				Shop shop = Cloner.clone((Shop) channel, view);
				
				result.languages = result.languages.set(shop, null); // Add default language
				for (String language : Objects.firstNonNull(shop.getLanguages(), "").split(",")) {
					result.languages = result.languages.add(shop, language);
					languages.add(language);
				}
			}
		}
		
		// TODO See TODO above
		for (String language : languages) {
			result.languages = result.languages.add(null, language);
		}
		
		return result;
	}
}

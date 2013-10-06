package claro.catalog.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import claro.catalog.CatalogDao;
import claro.catalog.CatalogDaoService;
import claro.catalog.command.GetLanguagesByOutputChannel;
import claro.catalog.util.CatalogModelUtil;
import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.shop.Shop;
import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.Cloner;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.SMap;

public class GetLanguagesByShopImpl extends GetLanguagesByOutputChannel implements CommandImpl<GetLanguagesByOutputChannel.Result>{

	private static final long serialVersionUID = 1L;
	static View view = new BasicView();

	@Override
	public Result execute() throws CommandException {
		checkValid();
		
		Result result = new Result();
		CatalogDao dao = CatalogDaoService.getCatalogDao();
		Catalog catalog = dao.getEntityManager().find(Catalog.class, catalogId);

		result.languages = SMap.empty();
		result.languages = result.languages.set(null, null); // Add default language
		
		// TODO We should iterate over catalog languages, but use union of shop languages instead.
//		for (String language : catalogModel.getCatalog().getLanguages().split(",") {
//			result.languages = result.languages.add(null, language);
//		}
		
		Set<String> languages = new LinkedHashSet<String>();
		
		for (OutputChannel channel : CollectionUtil.notNull(catalog.getOutputChannels())) {
			if (channel instanceof Shop) {
				Shop shop = Cloner.clone((Shop) channel, view);
				
				result.languages = result.languages.set(shop, null); // Add default language
				for (String language : CatalogModelUtil.splitLanguages(shop.getLanguages())) {
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

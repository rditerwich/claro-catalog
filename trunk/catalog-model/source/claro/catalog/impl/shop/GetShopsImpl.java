package claro.catalog.impl.shop;

import claro.catalog.CatalogDao;
import claro.catalog.command.shop.GetShops;
import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.Cloner;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.jpa.JpaService;

public class GetShopsImpl extends GetShops implements CommandImpl<GetShops.Result>{

	private static final long serialVersionUID = 1L;
	static View view = new BasicView("navigation", "navigation/subNavigation", "promotions", "promotions/templates", "templates", "excludedItems", "excludedProperties");

	@Override
	public Result execute() throws CommandException {
		checkValid();
		
		Result result = new Result();
		CatalogDao dao = new CatalogDao(JpaService.getEntityManager());
		result.shops = dao.getShops(catalogId, paging);

		result.shops = Cloner.clone(result.shops, view);
		return result;
	}

}

package claro.catalog.impl.order;

import java.util.Date;
import java.util.List;

import claro.catalog.CatalogDao;
import claro.catalog.CatalogDaoService;
import claro.catalog.command.order.GetOrders;
import claro.catalog.command.shop.GetShops;
import claro.jpa.order.OrderHistory;
import claro.jpa.order.OrderStatus;
import claro.jpa.order.ProductOrder;
import claro.jpa.order.Transport;
import claro.jpa.party.Address;
import claro.jpa.party.User;
import claro.jpa.shop.Shop;
import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.Cloner;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;

public class GetOrdersImpl extends GetOrders implements CommandImpl<GetOrders.Result>{

	private static final long serialVersionUID = 1L;
	static View view = new BasicView("shop", "productOrders", "deliveryAddress", "transport", "user", "history");

	@Override
	public Result execute() throws CommandException {
		checkValid();
		
		Result result = new Result();
		CatalogDao dao = CatalogDaoService.getCatalogDao();
		result.orders = dao.getOrders(statusFilter, paging);

		result.orders = Cloner.clone(result.orders, view);
		return result;
	}

}

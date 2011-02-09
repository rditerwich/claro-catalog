package claro.catalog.manager.client.orders;

import java.util.ArrayList;
import java.util.List;

import claro.catalog.command.order.GetOrders;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.command.StatusCallback;
import claro.jpa.order.Order;
import easyenterprise.lib.command.gwt.GwtCommandFacade;

public abstract class OrderModel implements Globals {
	public static int REQUESTED_PAGESIZE;

	private int startIndex;
	private boolean lastPage;
	
	private List<Order> orders = new ArrayList<Order>();
	private Order order;
	
	public List<Order> getOrders() {
		return orders;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
		renderAll();
	}
	
	public void setOrder(Order order) {
		this.order = order;
		renderAll();
	}

	public void createOrder() {
//		Order order = new Order();
//		Catalog catalog = new Catalog();
//		catalog.setId(CatalogManager.getCurrentCatalogId());
//		order.setCatalog(catalog);
//		order.setName("new order");
//		getOrders().add(0, order);
//		setOrder(order);
//		openDetail();
	}


	/**
	 * Fetches the current page
	 */
	public void fetchOrders() {
		fetchOrders(false);
	}
	
	/**
	 * Paging not supported yet.
	 */
	@Deprecated
	public void fetchPreviousPage() {
		
	}
	
	/**
	 * Paging not supported yet.
	 */
	@Deprecated
	public void fetchNextPage() {
		
	}

	public void fetchOrders(boolean wait) {
		// TODO For now, ignore wait.  Use PagedView in future.
		GetOrders command = new GetOrders();
		GwtCommandFacade.execute(command, new StatusCallback<GetOrders.Result>(messages.loadingOrdersMessage()) {
			public void onSuccess(GetOrders.Result result) {
				super.onSuccess(result);
				setOrders(result.orders);
			}
		});
	}
	
	
	/**
	 * Paging not supported yet.
	 */
	@Deprecated
	public int getStartIndex() {
		return startIndex;
	}
	
	/**
	 * Paging not supported yet.
	 */
	@Deprecated
	public boolean isLastPage() {
		return lastPage;
	}
	
//	public void store(final StoreOrder command) {
//		command.catalogId = CatalogManager.getCurrentCatalogId();
//		GwtCommandFacade.execute(command, new StatusCallback<StoreOrder.Result>(messages.savingOrdersAction(), false) {
//			public void onSuccess(StoreOrder.Result result) {
//				super.onSuccess(result);
//				if (result.order != null) {
//					int index = CollectionUtil.indexOfRef(orders, command.order);
//					if (index != -1) {
//						orders.set(index, result.order);
//					}
//				} else {
//					orders.remove(command.order);
//				}
//				setOrder(result.order);
//			}
//		});
//	}

	protected abstract void openDetail();
	
	protected abstract void renderAll();

}

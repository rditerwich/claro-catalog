package claro.catalog.command.order;

import java.util.List;

import claro.jpa.order.Order;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.Paging;

public class GetOrders implements Command<GetOrders.Result> {

	private static final long serialVersionUID = 1L;

	public Paging paging = Paging.NO_PAGING;
	// TODO Probably need to filter... maybe limit by date?
	
	public void checkValid() throws CommandValidationException {
	}
	
	public static class Result implements CommandResult {
		private static final long serialVersionUID = 1L;
		
		public List<Order> orders;
	}
}

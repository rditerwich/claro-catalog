package claro.catalog.manager.client.orders;

import java.util.Date;

import claro.catalog.manager.client.Globals;
import claro.jpa.order.OrderStatus;

import com.google.gwt.i18n.client.DateTimeFormat;

public class OrderUtil implements Globals {
	public static DateTimeFormat.PredefinedFormat ORDER_FORMAT_SHORT = DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT;
	public static DateTimeFormat.PredefinedFormat ORDER_FORMAT_LONG = DateTimeFormat.PredefinedFormat.DATE_TIME_LONG;

	public static String formatOrderDate(Date date) {
		return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(date);
	}

	public static String formatOrderDateLong(Date date) {
		return DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_LONG).format(date);
	}
	
	public static String formatStatus(OrderStatus status) {
		switch (status) {
		case Canceled: return messages.orderCanceledStatus();
		case Closed:
		case Complete:
		case InShoppingCart:
		case OnHold:
		case PendingPayment:
		case Processing:
		case ReceivedPayment:
		case Shipped:
		default:
			return status.toString();	
		}
	}

}

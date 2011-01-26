package claro.catalog.manager.client.widgets;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;

public class ActionLabel extends Label {

	public ActionLabel(String label, ClickHandler handler) {
		super(label);
		addClickHandler(handler);
		setStyleName("ee-ActionLabel");
	}
}

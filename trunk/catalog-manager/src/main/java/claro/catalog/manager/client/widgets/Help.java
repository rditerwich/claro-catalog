package claro.catalog.manager.client.widgets;

import claro.catalog.manager.client.Globals;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class Help extends Image implements Globals {
	private PopupPanel popup;
	public Help(final String message) {
		super(images.help());
		setTitle(message);
		addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (popup == null) {
					popup = new PopupPanel();
					popup.add(new Label(message));
					popup.setAutoHideEnabled(true);
					popup.setWidth("20em");
			}
				if (popup.isShowing()) popup.hide();
				else popup.show();
				popup.setPopupPosition(getAbsoluteLeft() + getOffsetWidth(), getAbsoluteTop());
			}
		});
	}
}

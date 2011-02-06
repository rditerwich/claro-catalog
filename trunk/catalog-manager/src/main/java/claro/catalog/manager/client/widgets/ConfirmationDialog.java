package claro.catalog.manager.client.widgets;

import claro.catalog.manager.client.Globals;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.gwt.client.widgets.EEButton;
import easyenterprise.lib.gwt.client.widgets.EEButtonBar;

public abstract class ConfirmationDialog extends DialogBox implements Globals {

	public ConfirmationDialog(final String message, final ImageResource icon) {
		setModal(true);
		setWidget(new VerticalPanel() {{
			add(new HorizontalPanel() {{
				add(new Image(icon));
				add(new Label(message));
			}});
			add(new EEButtonBar() {{
				add(new EEButton(messages.yesButton()) {{
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							yesPressed();
							hide();
						}
					});
				}});
				add(new EEButton(messages.noButton()) {{
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							hide();
						}
					});
				}});
			}});
		}});
		center();
		hide();
	}
	
	protected abstract void yesPressed();
}

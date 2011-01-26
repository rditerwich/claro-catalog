package claro.catalog.manager.client.widgets;

import claro.catalog.manager.client.Globals;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.gwt.GwtCommandFacade;

public abstract class RefreshPanel<T extends CommandResult, W extends Widget> extends FlowPanel implements Globals {

	private final W widget;
	private Label pleaseWaitLabel;
	private Label errorLabel;
	private FlowPanel panel;
	private boolean isWaiting;
	private boolean refreshPending;
	
	public RefreshPanel(final boolean refreshButton, final W widget) {
		this.widget = widget;
		add(pleaseWaitLabel = new Label(messages.pleaseWait()));
		add(errorLabel = new Label(""));
		add(panel = new FlowPanel() {{
			setVisible(false);
			if (refreshButton) {
				add(new Label(messages.refresh()) {{
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							refresh();
						}
					});
				}});
			}
			add(widget);
		}});
		pleaseWaitLabel.setVisible(false);
		errorLabel.setVisible(false);
		panel.setVisible(false);
		refreshPending = true;
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		refresh();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (refreshPending) {
			refresh();
		}
	}
	
	public void refresh() {
		if (!isVisible()) {
			refreshPending = true;
			return;
		}
		refreshPending = false;
		panel.setVisible(false);
		errorLabel.setVisible(false);
		isWaiting = true;
		timer.schedule(100);
		Command<T> cmd = getRefreshCommand();
		if (cmd != null) {
			GwtCommandFacade.execute(cmd, new AsyncCallback<T>() {
				public void onFailure(Throwable caught) {
					isWaiting = false;
					errorLabel.setText(caught.getMessage());
					pleaseWaitLabel.setVisible(false);
					errorLabel.setVisible(true);
				}
				public void onSuccess(T result) {
					isWaiting = false;
					pleaseWaitLabel.setVisible(false);
					errorLabel.setVisible(false);
					render(widget, result);
					panel.setVisible(true);
				}
			});
		} else {
			isWaiting = false;
			pleaseWaitLabel.setVisible(false);
			errorLabel.setVisible(false);
		}
	}
	
	private final Timer timer = new Timer() {
		public void run() {
			if (isWaiting) {
				pleaseWaitLabel.setVisible(true);
			}
		}
	};
	
	protected abstract Command<T> getRefreshCommand();
	protected abstract void render(W widget, T result);
}

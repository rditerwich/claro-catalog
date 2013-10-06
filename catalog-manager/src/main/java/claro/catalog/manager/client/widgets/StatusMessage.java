package claro.catalog.manager.client.widgets;

import claro.catalog.manager.client.Globals;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;

/**
 * Displays a status message at the top of the page.
 * 
 * Use with default timeout of 15 seconds: StatusMessage.get().show("message");
 * Or with custom timeout (in seconds): StatusMessage.get().show("message", 20);
 */
public class StatusMessage implements Globals {

	private static final int DEFAULT_TIMEOUT = 15;

	private static FlowPanel statusPanel = new FlowPanel();
	private static DecoratedPopupPanel status = new DecoratedPopupPanel(false, false) {{
		// set animation after initial show+hide
		setAnimationEnabled(true);
		setWidget(new HorizontalPanel() {{
			setVerticalAlignment(ALIGN_TOP);
			setSpacing(3);
			add(statusPanel);
			add(new ActionImage(images.closeBlue(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					status.hide();
				}
			}));
		}});
	}};

	private boolean canceled;
	private HTML message = new HTML();
	private static final PositionCallback pc = new PositionCallback() {
		@Override
		public void setPosition(int offsetWidth, int offsetHeight) {
			status.setPopupPosition((Window.getClientWidth() - offsetWidth) >> 1, 0);
		}
	};

	/**
	 * Displays message for 15 seconds.
	 * 
	 * @param message
	 */
	public static StatusMessage show(String message) {
		return show(message, DEFAULT_TIMEOUT);
	}
	
	public void cancel() {
		canceled = true;
		statusPanel.remove(message);
		if (status.isShowing() && statusPanel.getWidgetCount() == 0) {
			status.hide(); 
		}
	}

	/**
	 * Displays message for the duration of given seconds.
	 * 
	 * @param message
	 * @param durationSeconds
	 */
	public static StatusMessage show(String message, int durationSeconds) {
		final StatusMessage result = new StatusMessage();
		show(result, message, durationSeconds);
		return result;
	}
	private static StatusMessage show(final StatusMessage result, String message, int durationSeconds) {
		new Timer() {
			public void run() {
				result.cancel();
			}
		}.schedule(durationSeconds * 1000);
		result.message.setHTML(message);
		statusPanel.add(result.message);
		if (!status.isShowing()) {
			status.setPopupPositionAndShow(pc);
		}
		
		return result;
	}
	
	public static StatusMessage show(final String message, int startDelay, final int durationSeconds) {
		if (startDelay > 0) {
			final StatusMessage result = new StatusMessage();
			new Timer() {
				public void run() {
					if (!result.canceled) {
						show(result, message, durationSeconds);
					}
				}
			}.schedule(startDelay * 1000);
			return result;
		} else {
			return show(message, durationSeconds);
		}
	}
	
	public static StatusMessage showError(String message, Throwable cause) {
		return show(message); // TODO show link with panel for details.
	}
}

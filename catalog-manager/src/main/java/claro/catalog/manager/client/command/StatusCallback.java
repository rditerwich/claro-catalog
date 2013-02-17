package claro.catalog.manager.client.command;

import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.StatusMessage;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.gwt.RetryingCallback;

public abstract class StatusCallback<T extends CommandResult> implements RetryingCallback<T>, Globals {
	private static final int SUCCESS_MESSAGE_TIMEOUT = 3;
	private final String action;
	private StatusMessage actionMessage;
	private final boolean showErrorsOnly;
	public StatusCallback() {
		this(null, true);
	}
	public StatusCallback(String action) {
		this(action, true);
	}
	public StatusCallback(String action, boolean showErrorsOnly) {
		this.action = action;
		this.showErrorsOnly = showErrorsOnly;
		if (action != null && !showErrorsOnly) {
			actionMessage = StatusMessage.show(action, 2, 1000);
		}
		
	}
	public void failedAttempt(int attemptNr, Throwable caught) {
		if (actionMessage != null) {
			actionMessage.cancel();
		}
		String message = action != null? messages.failureRetryingMessage(action, attemptNr) : messages.internalFailureRetryingMessage(attemptNr);
		StatusMessage.showError(message, caught);
	}
	public void onFailure(Throwable caught) {
		if (actionMessage != null) {
			actionMessage.cancel();
		}
		String message = action != null? messages.failureMessage(action) : messages.internalFailureMessage();
		StatusMessage.showError(message, caught);
		caught.printStackTrace();
	}
	public void onSuccess(T result) {
		if (actionMessage != null) {
			actionMessage.cancel();
		}
		String message = action != null? messages.successMessage(action) : null;
		if (message != null && !showErrorsOnly) {
			StatusMessage.show(message, SUCCESS_MESSAGE_TIMEOUT);
		}
	};
}
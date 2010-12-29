package claro.catalog.manager.client.command;

import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.StatusMessage;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.gwt.RetryingCallback;

public abstract class StatusCallback<T extends CommandResult> implements RetryingCallback<T>, Globals {
	private final String action;
	public StatusCallback() {
		this(null);
	}
	public StatusCallback(String action) {
		this.action = action;
		
	}
	public void failedAttempt(int attemptNr, Throwable caught) {
		String message = action != null? messages.failureRetryingMessage(action, attemptNr) : messages.internalFailureRetryingMessage(attemptNr);
		StatusMessage.get().showError(message, caught);
	}
	public void onFailure(Throwable caught) {
		String message = action != null? messages.failureMessage(action) : messages.internalFailureMessage();
		StatusMessage.get().showError(message, caught);
	}
}
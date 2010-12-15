package claro.catalog.manager.client.command;

import claro.catalog.manager.client.Util;
import claro.catalog.manager.client.widgets.StatusMessage;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.gwt.RetryingCallback;

public abstract class StatusCallback<T extends CommandResult> implements RetryingCallback<T> {
	private final String action;
	public StatusCallback() {
		this(null);
	}
	public StatusCallback(String action) {
		this.action = action;
		
	}
	public void failedAttempt(int attemptNr, Throwable caught) {
		String message = action != null? Util.i18n.failureRetryingMessage(action, attemptNr) : Util.i18n.internalFailureRetryingMessage(attemptNr);
		StatusMessage.get().showError(message, caught);
	}
	public void onFailure(Throwable caught) {
		String message = action != null? Util.i18n.failureMessage(action) : Util.i18n.internalFailureMessage();
		StatusMessage.get().showError(message, caught);
	}
}
package claro.catalog.manager.client.importing;

import java.util.List;

import claro.catalog.command.importing.GetImportSourceHistory;
import claro.catalog.command.importing.GetImportSourceHistory.Result;
import claro.catalog.manager.client.Globals;
import claro.jpa.importing.ImportJobResult;
import claro.jpa.importing.ImportSource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.gwt.client.widgets.Table;

public abstract class ImportHistoryPanel extends VerticalPanel implements Globals {

	private ImportSource importSource;
	private Label pleaseWaitLabel;
	private Table table;

	public ImportHistoryPanel() {
		add(pleaseWaitLabel = new Label());
		add(table = new Table());
	}
	
	public void setImportSource(ImportSource importSource) {
		if (this.importSource != importSource) {
			this.importSource = importSource;
			refresh();
			fetch();
		}
	}
	
	public void refresh() {
		pleaseWaitLabel.setText(messages.pleaseWait());
		table.clear();
	}

	private void fetch() {
		if (importSource == null || importSource.getId() == null) return;
		GetImportSourceHistory command = new GetImportSourceHistory();
		command.importSourceId = importSource.getId();
		GwtCommandFacade.execute(command, new AsyncCallback<Result> () {
			public void onFailure(Throwable caught) {
			}
			public void onSuccess(Result result) {
				render(result.jobResults);
			}
		});
	}
	
	private void render(List<ImportJobResult> jobResults) {
		pleaseWaitLabel.setText("");
		table.resize(1 + jobResults.size(), 4);
		table.setHeaderText(0, 0, "time");
		table.setHeaderText(0, 1, messages.jobStatus());
		table.setHeaderText(0, 2, messages.importUrlLabel());
		int row = 0;
		for (final ImportJobResult jobResult : jobResults) {
			table.setWidget(row, 0, new Label(jobResult.getEndTime().toString()));
			table.setWidget(row, 1, new Label(jobResult.getSuccess() ? "success" : "failed"));
			table.setWidget(row, 2, new Label(jobResult.getUrl()));
			table.setWidget(row, 3, new Anchor(messages.showLogLink()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						showLog(jobResult);
					}
				});
			}});
			
			row++;
		}
	}
	
	protected abstract void showLog(ImportJobResult jobResult);
}

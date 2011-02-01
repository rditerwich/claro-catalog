package claro.catalog.manager.client.importing;

import claro.catalog.command.importing.GetImportSourceHistory;
import claro.catalog.command.importing.GetImportSourceHistory.Result;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.RefreshPanel;
import claro.jpa.importing.ImportJobResult;

import com.google.common.base.Objects;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.gwt.client.widgets.Table;

public class ImportHistoryPanel2 extends RefreshPanel<Result, Table> implements Globals {

	private final ImportSoureModel state;
	private Long lastImportSourceId = null;

	public ImportHistoryPanel2(ImportSoureModel state) {
		super(true, new Table());
		this.state = state;
	}
	
	protected Command<Result> getRefreshCommand() {
		if (state.getImportSource() != null && state.getImportSource().getId() != null) {
			GetImportSourceHistory command = new GetImportSourceHistory();
			lastImportSourceId = state.getImportSource().getId();
			command.importSourceId = lastImportSourceId;
			return command;
		}
		return null;
	}
	
	protected void render(Table table, Result result) {
		table.resize(1 + result.jobResults.size(), 4);
		table.setHeaderText(0, 0, "time");
		table.setHeaderText(0, 1, messages.jobStatus());
		table.setHeaderText(0, 2, messages.importUrlLabel());
		int row = 0;
		for (final ImportJobResult jobResult : result.jobResults) {
			table.setWidget(row, 0, new Label(jobResult.getEndTime().toString()));
			table.setWidget(row, 1, new Label(jobResult.getSuccess() ? "success" : "failed"));
			table.setWidget(row, 2, new Label(jobResult.getUrl()));
			table.setWidget(row, 3, new Anchor(messages.showLogLink()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						state.setJobResult(jobResult);
						state.showLog();
					}
				});
			}});
			
			row++;
		}
	}
	
	public void render() {
		if (!Objects.equal(lastImportSourceId, state.getImportSource().getId())) {
			refresh();
		}
	}
}

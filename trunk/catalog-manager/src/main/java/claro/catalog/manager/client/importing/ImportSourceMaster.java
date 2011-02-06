package claro.catalog.manager.client.importing;

import claro.catalog.manager.client.Globals;
import claro.jpa.importing.ImportSource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import easyenterprise.lib.gwt.client.widgets.TableWithObjects;

public class ImportSourceMaster extends TableWithObjects<ImportSource> implements Globals {

	private static final int NAME_COL = 0;
	private static final int LASTRUN_COL = 1;
	private static final int STATUS_COL = 2;
	private static final int WEATHER_COL = 3;
	private static final int NR_COLS = 4;

	private final ImportSoureModel model;

	public ImportSourceMaster(ImportSoureModel model) {
		this.model = model;
		resizeColumns(NR_COLS);
		setHeaderText(0, NAME_COL, messages.importSourceLabel());
		setHeaderText(0, LASTRUN_COL, messages.lastRunLabel());
		setHeaderText(0, STATUS_COL, messages.statusHeader());
		setHeaderText(0, WEATHER_COL, messages.healthHeader());	}
	
	public void render() {
		int fromRows = getRowCount();
		resizeRows(model.getImportSources().size());
		
		// Create new Rows
		for (int row = fromRows; row < model.getImportSources().size(); row++) {
			
			final ClickHandler selectRowClickHandler = new ClickHandler() {
				public void onClick(ClickEvent event) {
				}
			};
			
			// Image
			setWidget(row, NAME_COL, new Anchor() {{
				addClickHandler(selectRowClickHandler);
			}});
			setWidget(row, LASTRUN_COL, new Label() {{
				addDomHandler(selectRowClickHandler, ClickEvent.getType());
			}});
			setWidget(row, STATUS_COL, new Image() {{
				addDomHandler(selectRowClickHandler, ClickEvent.getType());
			}});
			setWidget(row, WEATHER_COL, new Image() {{
				addClickHandler(selectRowClickHandler);
				setTitle("Shows how many imports were succesful in recent history.");
			}});
		}
		
		// render all rows
		for (int row = 0; row < model.getImportSources().size(); row++) {
			renderRow(row);
		}
	}
	
	public void renderRow(int row) {
		ImportSource importSource = model.getImportSources().get(row);
		setObject(row, importSource);
		
		// name
		((HasText) getWidget(row, NAME_COL)).setText(importSource.getName());
		
		// status
		if (importSource.getJob() != null && importSource.getJob().getLastSuccess() != null) {
			((Image) getWidget(row, STATUS_COL)).setResource(importSource.getJob().getLastSuccess() ? images.ok() : images.error());
		} else {
			((Image) getWidget(row, STATUS_COL)).setResource(images.warning());
		}
		
		// lastrun
		if (importSource.getJob() != null && importSource.getJob().getLastTime() != null) {
			((HasText) getWidget(row, LASTRUN_COL)).setText(importSource.getJob().getLastTime().toString());
		}
		
		// health
		Image healthWidget = (Image) getWidget(row, WEATHER_COL);
		int health = 0;
		if (importSource.getJob() != null && importSource.getJob().getHealthPerc() != null) {
			health = importSource.getJob().getHealthPerc() / 20;
		}
		switch (health) {
		case 0: healthWidget.setResource(images.health0()); break;
		case 1: healthWidget.setResource(images.health1()); break;
		case 2: healthWidget.setResource(images.health2()); break;
		case 3: healthWidget.setResource(images.health3()); break;
		case 4: healthWidget.setResource(images.health4()); break;
		default: healthWidget.setResource(images.health0()); break;
		}
	}
}


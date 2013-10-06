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
		addStyleName("ImportSourceMaster");
		resizeColumns(NR_COLS);
		setHeaderText(0, NAME_COL, messages.importSourceLabel());
		setHeaderText(0, LASTRUN_COL, messages.lastRunLabel());
		setHeaderText(0, STATUS_COL, messages.statusHeader());
		setHeaderText(0, WEATHER_COL, messages.healthHeader());	
	}
	
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
			setWidget(row, STATUS_COL, new Image(importSource.getJob().getLastSuccess() ? images.ok() : images.error()));
		} else {
			setWidget(row, STATUS_COL, new Image(images.warning()));
		}
		
		// lastrun
		if (importSource.getJob() != null && importSource.getJob().getLastTime() != null) {
			((HasText) getWidget(row, LASTRUN_COL)).setText(importSource.getJob().getLastTime().toString());
		}
		
		// health
		int health = 0;
		if (importSource.getJob() != null && importSource.getJob().getHealthPerc() != null) {
			health = importSource.getJob().getHealthPerc() / 20;
		}
		switch (health) {
		case 0: setWidget(row, WEATHER_COL, new Image(images.health0())); break;
		case 1: setWidget(row, WEATHER_COL, new Image(images.health1())); break;
		case 2: setWidget(row, WEATHER_COL, new Image(images.health2())); break;
		case 3: setWidget(row, WEATHER_COL, new Image(images.health3())); break;
		case 4: setWidget(row, WEATHER_COL, new Image(images.health4())); break;
		default: setWidget(row, WEATHER_COL, new Image(images.health0())); break;
		}
	}
}


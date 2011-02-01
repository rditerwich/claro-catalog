package claro.catalog.manager.client.importing;

import java.util.ArrayList;
import java.util.List;

import org.cobogw.gwt.user.client.ui.RoundedPanel;

import claro.catalog.manager.client.GlobalStylesEnum;
import claro.catalog.manager.client.Globals;
import claro.jpa.importing.ImportSource;
import claro.jpa.jobs.Job;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.widgets.EEButton;
import easyenterprise.lib.gwt.client.widgets.MasterDetail;
import easyenterprise.lib.gwt.client.widgets.PullUpTabs;
import easyenterprise.lib.gwt.client.widgets.Table;

public class ImportMasterDetail extends MasterDetail implements Globals {

	private static final int NAME_COL = 0;
	private static final int LASTRUN_COL = 1;
	private static final int STATUS_COL = 2;
	private static final int WEATHER_COL = 3;
	private static final int NR_COLS = 4;
	
	private class RowWidgets {
		public Anchor name;
		public Label lastRun;
		public Image status;
		public Image health;
	}

	private List<RowWidgets> tableWidgets = new ArrayList<RowWidgets>();
	private ImportMainPanel importSourceMainPanel;
	private MultiFileImportPanel multiFilePanel;
	private ImportFileFormatPanel fileFormatPanel;
	private ImportDataMappingPanel propertyMappingPanel;
	private ImportHistoryPanel2 historyPanel;
	private ImportLogPanel logPanel;

	private PullUpTabs tabs;
	private ImportSoureModel model;
	
	public ImportMasterDetail(int headerSize, int footerSize) {
		super(headerSize, footerSize);
		this.model = new ImportSoureModel() {
			
			@Override
			protected void renderAll() {
				if (model.getImportSource() == null) {
					closeDetail(false);
				} else {
					importSourceMainPanel.render();
					multiFilePanel.render();
					fileFormatPanel.render();
					propertyMappingPanel.render();
					historyPanel.render();
					logPanel.render();
				}
				renderTable();
			}

			@Override
			protected void showFileFormat() {
				tabs.showTab(fileFormatPanel);
			}

			@Override
			protected void showDataMapping() {
				tabs.showTab(propertyMappingPanel);
			}

			@Override
			protected void showLog() {
				tabs.showTab(logPanel);
			}
		};
	}

	public void setImportSources(List<ImportSource> importSources) {
		model.setImportSources(importSources);
		if (getDetailOpen()) {
			int index = importSources.indexOf(model.getImportSource());
			if (index < 0) {
				model.setImportSource(null);
				closeDetail(false);
			} else {
				openDetail(index);
			}
		}
		renderTable();
	}
	
	@Override
	final protected void masterPanelCreated(DockLayoutPanel masterPanel) {
		Table table = getMasterTable();
		table.resizeColumns(NR_COLS);
		table.setHeaderText(0, NAME_COL, messages.importSourceLabel());
		table.setHeaderText(0, LASTRUN_COL, messages.lastRunLabel());
		table.setHeaderText(0, STATUS_COL, messages.statusHeader());
		table.setHeaderText(0, WEATHER_COL, messages.healthHeader());
		
		LayoutPanel header = getMasterHeader();
		header.add(new RoundedPanel( RoundedPanel.ALL, 4) {{
			setBorderColor("white");
			add(new Anchor(messages.newImportSourceLink()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						createImportSource();
						event.stopPropagation();
					}
				});
			}});
		}});
	}
	
	@Override
	protected final Widget tableCreated(Table table) {
		table.setStylePrimaryName(GlobalStylesEnum.mainTable.toString());
		return new RoundedPanel(table, RoundedPanel.ALL, 4) {{
			setBorderColor("white");
		}};
	}

	@Override
	final protected void detailPanelCreated(LayoutPanel detailPanel) {
		detailPanel.add(new LayoutPanel() {{
//			add(new ActionImage(images.closeBlue(), new ClickHandler() {
//				public void onClick(ClickEvent event) {
//					tabs.hideTab();
//					closeDetail(true);
//				}
//			}) {{
//				setStylePrimaryName(GlobalStylesEnum.detailPanelCloseButton.toString());
//			}});
			add(tabs = new PullUpTabs(26, 5) {{
				setMainWidget(importSourceMainPanel = new ImportMainPanel(model));
				addTab(new EEButton(messages.multiFileTab()), 100, multiFilePanel = new MultiFileImportPanel(model));
				addTab(new EEButton(messages.fileFormatTab()), 110, fileFormatPanel = new ImportFileFormatPanel(model));
				addTab(new EEButton(messages.dataMappingTab()), 140, propertyMappingPanel = new ImportDataMappingPanel(model));
				addTab(new EEButton(messages.history()), 100, historyPanel = new ImportHistoryPanel2(model));
				addTab(new EEButton(messages.log()), 80, logPanel = new ImportLogPanel(model));
			}});
		}});
		//ruud
//tabs.showTab(0);
	}


	private void renderTable() {
		Table masterTable = getMasterTable();
		masterTable.resizeRows(model.getImportSources().size());
		
		// Delete old rows:
		while (tableWidgets.size() > model.getImportSources().size()) {
			tableWidgets.remove(tableWidgets.size() - 1);
		}
		
		// Create new Rows
		while (tableWidgets.size() < model.getImportSources().size()) {
			final int row = tableWidgets.size();
			final RowWidgets rowWidgets = new RowWidgets();
			tableWidgets.add(rowWidgets);
			
			final ClickHandler selectRowClickHandler = new ClickHandler() {
				public void onClick(ClickEvent event) {
					selectRow(row);
					// prevent detail to close immediately
					event.stopPropagation();
				}
			};
			
			// Image
			masterTable.setWidget(row, NAME_COL, rowWidgets.name = new Anchor() {{
				addClickHandler(selectRowClickHandler);
			}});
			masterTable.setWidget(row, LASTRUN_COL, rowWidgets.lastRun = new Label() {{
				addDomHandler(selectRowClickHandler, ClickEvent.getType());
			}});
			masterTable.setWidget(row, STATUS_COL, rowWidgets.status = new Image() {{
				addDomHandler(selectRowClickHandler, ClickEvent.getType());
			}});
			masterTable.setWidget(row, WEATHER_COL, rowWidgets.health = new Image() {{
				addClickHandler(selectRowClickHandler);
				setTitle("Shows how many imports were succesful in recent history.");
			}});
			
		}
		
		// render all rows
		for (int row = 0; row < model.getImportSources().size(); row++) {
			renderRow(row);
		}
	}
	
	private void renderRow(int row) {
		ImportSource importSource = model.getImportSources().get(row);
		RowWidgets rowWidgets = tableWidgets.get(row);
		rowWidgets.name.setText(importSource.getName());
		
		// lastrun
		if (importSource.getJob() != null && importSource.getJob().getLastSuccess() != null) {
			rowWidgets.status.setResource(importSource.getJob().getLastSuccess() ? images.ok() : images.error());
		} else {
			rowWidgets.status.setResource(images.warning());
		}
		if (importSource.getJob() != null && importSource.getJob().getLastTime() != null) {
			rowWidgets.lastRun.setText(importSource.getJob().getLastTime().toString());
		}
		
		// health
		int health = 0;
		if (importSource.getJob() != null && importSource.getJob().getHealthPerc() != null) {
			health = importSource.getJob().getHealthPerc() / 20;
		}
		switch (health) {
		case 0: rowWidgets.health.setResource(images.health0()); break;
		case 1: rowWidgets.health.setResource(images.health1()); break;
		case 2: rowWidgets.health.setResource(images.health2()); break;
		case 3: rowWidgets.health.setResource(images.health3()); break;
		case 4: rowWidgets.health.setResource(images.health4()); break;
		default: rowWidgets.health.setResource(images.health0()); break;
		}
		
	}

	private void selectRow(int row) {
		openDetail(row);
		model.setImportSource(model.getImportSources().get(row));
	}
	
	final protected void createImportSource() {
		ImportSource importSource = new ImportSource();
		importSource.setName("new import source");
		importSource.setJob(new Job());
		model.getImportSources().add(0, importSource);
		renderTable();
		selectRow(0);
	}
}

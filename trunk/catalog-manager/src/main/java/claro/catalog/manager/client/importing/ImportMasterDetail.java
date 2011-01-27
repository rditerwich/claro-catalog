package claro.catalog.manager.client.importing;

import static com.google.common.base.Objects.equal;

import java.util.ArrayList;
import java.util.List;

import org.cobogw.gwt.user.client.ui.RoundedPanel;

import claro.catalog.command.importing.StoreImportSource;
import claro.catalog.manager.client.GlobalStyles;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.ActionImage;
import claro.catalog.manager.client.widgets.MediaWidget;
import claro.jpa.importing.ImportJobResult;
import claro.jpa.importing.ImportRules;
import claro.jpa.importing.ImportSource;
import claro.jpa.jobs.Job;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.widgets.MasterDetail;
import easyenterprise.lib.gwt.client.widgets.PullUpTabs;
import easyenterprise.lib.gwt.client.widgets.Table;

public abstract class ImportMasterDetail extends MasterDetail implements Globals {

	private static final int NAME_COL = 0;
	private static final int STATUS_COL = 1;
	private static final int WEATHER_COL = 2;
	private static final int LASTRUN_COL = 3;
	private static final int NR_COLS = 3;
	
	private class RowWidgets {
		public Anchor name;
		public MediaWidget status;
		public Image health;
		public Label lastRun;
	}

	private List<ImportSource> importSources = new ArrayList<ImportSource>();
	private ImportSource currentImportSource = null;
	
	private List<RowWidgets> tableWidgets = new ArrayList<RowWidgets>();
	private ImportMainPanel importSourceMainPanel;
	private MultiFileImportPanel multiFilePanel;
	private ImportDefinitionPanel propertyMappingsPanel;
	private ImportHistoryPanel2 historyPanel;
	private ImportLogPanel logPanel;

	private PullUpTabs tabs;
	
	public ImportMasterDetail(int headerSize, int footerSize) {
		super(headerSize, footerSize);
	}

	public void setImportSources(List<ImportSource> importSources) {
		this.importSources = importSources;
		if (getDetailOpen()) {
			int index = importSources.indexOf(currentImportSource);
			if (index < 0) {
				currentImportSource = null;
				closeDetail(false);
			} else {
				openDetail(index);
			}
		}
		renderTable();
	}
	
	public void importSourceChanged(ImportSource original, ImportSource importSource) {
		int row = importSources.indexOf(original);
		if (row >= 0) {
			importSources.set(row, importSource);
			renderTable();
		}
		if (currentImportSource != null && equal(currentImportSource.getId(), importSource.getId())) {
			currentImportSource = importSource;
			multiFilePanel.setImportSource(currentImportSource);
			importSourceMainPanel.setImportSource(currentImportSource);
			propertyMappingsPanel.setImportSource(currentImportSource);
			historyPanel.setImportSource(currentImportSource);
			logPanel.setImportSource(currentImportSource);
		}
	}
	
	protected abstract void updateImportSource(ImportSource importSource);
	protected abstract void storeImportSource(StoreImportSource command);

	@Override
	final protected void masterPanelCreated(DockLayoutPanel masterPanel) {
		Table table = getMasterTable();
		table.resizeColumns(NR_COLS);
		table.setHeaderText(0, 0, messages.importSourceLabel());
		
		LayoutPanel header = getMasterHeader();
		header.add(new RoundedPanel( RoundedPanel.ALL, 4) {{
			setBorderColor("white");
			add(new Anchor(messages.newImportSourceLink()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						createImportSource();
					}
				});
			}});
		}});
	}
	
	@Override
	protected final Widget tableCreated(Table table) {
		table.setStylePrimaryName(GlobalStyles.mainTable.toString());
		return new RoundedPanel(table, RoundedPanel.ALL, 4) {{
			setBorderColor("white");
		}};
	}

	@Override
	final protected void detailPanelCreated(LayoutPanel detailPanel) {
		detailPanel.add(new LayoutPanel() {{
			add(new ActionImage(images.closeBlue(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					tabs.hideTab();
					closeDetail(true);
				}
			}) {{
				setStylePrimaryName(GlobalStyles.detailPanelCloseButton.toString());
			}});
			add(tabs = new PullUpTabs(30, 5) {{
				setMainWidget(importSourceMainPanel = new ImportMainPanel() {
					protected void storeImportSource(StoreImportSource command) {
						ImportMasterDetail.this.storeImportSource(command);
					}
					@Override
					protected void showFileFormat(ImportRules rules) {
					}
					@Override
					protected void showImportRules(ImportRules rules) {
						tabs.showTab(propertyMappingsPanel);
					}
					protected void showLastRunLog() {
						new Timer() {
							public void run() {
								tabs.showTab(logPanel);
							}
						}.schedule(1);
					};
				});
				addTab(new Label(messages.multiFileTab()), 100, multiFilePanel = new MultiFileImportPanel() {
					
					@Override
					protected void storeImportSource(StoreImportSource command) {
						ImportMasterDetail.this.storeImportSource(command);
					}
					
					@Override
					protected void showImportRules(ImportRules rules) {
						tabs.showTab(propertyMappingsPanel);
					}
					
					@Override
					protected void showFileFormat(ImportRules rules) {
					}
				});
				addTab(new Label(messages.definition()), 80, propertyMappingsPanel = new ImportDefinitionPanel() {
					protected void storeImportSource(StoreImportSource command) {
						ImportMasterDetail.this.storeImportSource(command);
					}
				});
				addTab(new Label(messages.history()), 50, historyPanel = new ImportHistoryPanel2() {
					protected void showLog(ImportJobResult jobResult) {
						logPanel.setJobResult(jobResult);
						tabs.showTab(logPanel);
					}
				});
				addTab(new Label(messages.log()), 50, logPanel = new ImportLogPanel());
			}});
		}});
		//ruud
//tabs.showTab(0);
	}


	private void renderTable() {
		Table masterTable = getMasterTable();
		masterTable.resizeRows(importSources.size());
		
		// Delete old rows:
		while (tableWidgets.size() > importSources.size()) {
			tableWidgets.remove(tableWidgets.size() - 1);
		}
		
		// Create new Rows
		while (tableWidgets.size() < importSources.size()) {
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
			masterTable.setWidget(row, STATUS_COL, rowWidgets.status = new MediaWidget() {{
				addClickHandler(selectRowClickHandler);
			}});
			masterTable.setWidget(row, WEATHER_COL, rowWidgets.health = new Image() {{
				addClickHandler(selectRowClickHandler);
				setTitle("Shows how many imports were succesful in recent history.");
			}});
			
		}
		
		// render all rows
		for (int row = 0; row < importSources.size(); row++) {
			renderRow(row);
		}
	}
	
	private void renderRow(int row) {
		ImportSource importSource = importSources.get(row);
		RowWidgets rowWidgets = tableWidgets.get(row);
		rowWidgets.name.setText(importSource.getName());
		
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
		currentImportSource = importSources.get(row);
		importSourceChanged(currentImportSource, currentImportSource);
	}
	
	final protected void createImportSource() {
			ImportSource importSource = new ImportSource();
			importSource.setName("new import source");
			importSource.setJob(new Job());
			importSources.add(0, importSource);
			renderTable();
			selectRow(0);
	}
}

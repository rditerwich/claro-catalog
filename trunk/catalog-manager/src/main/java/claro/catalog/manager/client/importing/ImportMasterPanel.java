package claro.catalog.manager.client.importing;

import static easyenterprise.lib.util.ObjectUtil.orElse;

import java.util.ArrayList;
import java.util.List;

import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.MediaWidget;
import claro.jpa.importing.ImportSource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;

import easyenterprise.lib.gwt.client.widgets.MasterDetail;
import easyenterprise.lib.gwt.client.widgets.Table;
import easyenterprise.lib.util.ObjectUtil;

public abstract class ImportMasterPanel extends MasterDetail implements Globals {

	private static final int NAME_COL = 0;
	private static final int STATUS_COL = 1;
	private static final int WEATHER_COL = 2;
	private static final int LASTRUN_COL = 3;
	private static final int NR_COLS = 3;
	
	private class RowWidgets {
		public Label name;
		public MediaWidget status;
		public Image health;
		public Label lastRun;
	}

	private List<ImportSource> importSources = new ArrayList<ImportSource>();
	private ImportSource currentImportSource = null;
	
	private List<RowWidgets> tableWidgets = new ArrayList<RowWidgets>();

	public ImportMasterPanel(int headerSize, int footerSize) {
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

	private void renderTable() {
		Table masterTable = getMasterTable();
		masterTable.resizeRows(importSources.size());
		
		// Delete old rows:
		// TODO What if selected row is deleted?  maybe close detail?
		while (tableWidgets.size() > importSources.size()) {
			tableWidgets.remove(tableWidgets.size() - 1);
		}
		
		// Create new Rows
		while (tableWidgets.size() < importSources.size()) {
			final int row = tableWidgets.size();
			final RowWidgets rowWidgets = new RowWidgets();
			tableWidgets.add(rowWidgets);
			
			// Image
			masterTable.setWidget(row, NAME_COL, rowWidgets.name = new Label());
			masterTable.setWidget(row, STATUS_COL, rowWidgets.status = new MediaWidget(false) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						rowSelected(row);
					}
				});
			}});
			masterTable.setWidget(row, WEATHER_COL, rowWidgets.health = new Image());
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
		String healthString = orElse(importSource.getHealth(), "");
		int healthCount = Math.min(5, healthString.length()); 
		int health = 0;
		if (healthCount > 0) {
			for (int i = 0; i < healthCount; i++) {
				if (healthString.charAt(i) != '0') health += 1;
			}
			health = health * 5 / healthCount;
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
	
	private void rowSelected(int row) {
		ImportSource importSource = importSources.get(row);
		if (importSource.getId() != null) {
			updateImportSource(importSource);
		}
	}
	
	protected abstract ImportSource updateImportSource(ImportSource importSource);

	@Override
	final protected void masterPanelCreated(DockLayoutPanel masterPanel) {
		Table table = getMasterTable();
		table.resizeColumns(NR_COLS);
		table.setHeaderText(0, 0, messages.importSource());
		System.out.println("Table created");
		
		LayoutPanel header = getMasterHeader();
		header.add(new FlowPanel() {{
			add(new Anchor(messages.newImportSource()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						createImportSource();
					}
				});
			}});
		}});
	}

	@Override
	final protected void detailPanelCreated(LayoutPanel detailPanel) {
		detailPanel.add(new Anchor("Close") {{
			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					closeDetail(true);
				}
			});
		}});
	}

	final protected void createImportSource() {
			ImportSource importSource = new ImportSource();
			importSources.add(0, importSource);
			renderTable();
			openDetail(0);
	}
}

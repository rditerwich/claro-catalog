package claro.catalog.manager.client.importing;

import java.util.ArrayList;
import java.util.List;

import claro.catalog.data.PropertyInfo;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.ProductList;
import claro.catalog.manager.client.widgets.MediaWidget;
import claro.jpa.importing.ImportSource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;

import easyenterprise.lib.gwt.client.widgets.MasterDetail;
import easyenterprise.lib.gwt.client.widgets.Table;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.SMap;

public abstract class ImportMasterPanel extends MasterDetail implements Globals {

	private static final int NAME_COL = 1;
	private static final int STATUS_COL = 2;
	private static final int WEATHER_COL = 3;
	private static final int LASTRUN_COL = 3;
	
	private class RowWidgets {
		public Label name;
		public MediaWidget status;
		public MediaWidget weather;
		public Label lastRun;
	}

	private List<ImportSource> importSources = new ArrayList<ImportSource>();
	
	private List<RowWidgets> tableWidgets = new ArrayList<RowWidgets>();

	public ImportMasterPanel(int headerSize, int footerSize) {
		super(headerSize, footerSize);
	}

	public void setImportSources(List<ImportSource> importSources) {
		this.importSources = importSources;
		render();
	}

	private void render() {
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
		}
	}
	
	private void rowSelected(int row) {
		importSourceSelected(importSources.get(row));
	}
	
	protected void importSourceSelected(ImportSource importSource) {
		if (importSource.getId() != null) {
			updateImportSource(importSource);
		}
	}
	
	protected abstract ImportSource updateImportSource(ImportSource importSource);

	@Override
	protected void masterPanelCreated(DockLayoutPanel masterPanel) {
		Table table = getMasterTable();
		table.resizeColumns(2);
		table.setHeaderText(0, 0, messages.importSource());
		System.out.println("Table created");
		
		LayoutPanel header = getMasterHeader();
		header.add(new FlowPanel() {{
			add(new Button(messages.newImportSource()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						createImportSource();
					}
				});
			}});
		}});
	}

	@Override
	protected void detailPanelCreated(LayoutPanel detailPanel) {
		detailPanel.add(new Anchor("Close") {{
			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					closeDetail();
				}
			});
		}});
	}

	protected void createImportSource() {
			ImportSource importSource = new ImportSource();
			importSources.add(0, importSource);
			render();
			openDetail(0);
	}
}

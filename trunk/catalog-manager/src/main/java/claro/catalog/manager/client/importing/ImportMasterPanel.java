package claro.catalog.manager.client.importing;

import java.util.List;

import claro.catalog.manager.client.Globals;
import claro.jpa.importing.ImportSource;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;

import easyenterprise.lib.gwt.client.widgets.MasterDetail;
import easyenterprise.lib.gwt.client.widgets.Table;

public class ImportMasterPanel extends MasterDetail implements Globals {

	private List<ImportSource> importSources;

	public ImportMasterPanel(int headerSize, int footerSize) {
		super(headerSize, footerSize);
	}

	public void setImportSources(List<ImportSource> importSources) {
		this.importSources = importSources;
		render();
	}

	private void render() {
		getMasterTable();
	}
	
	@Override
	protected void masterPanelCreated(DockLayoutPanel masterPanel) {
		Table table = getMasterTable();
		table.resizeColumns(2);
		table.setHeaderText(0, 0, messages.importSource());
		System.out.println("Table created");
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
}

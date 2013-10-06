package claro.catalog.manager.client.importing;

import static easyenterprise.lib.gwt.client.StyleUtil.createStyle;
import static easyenterprise.lib.gwt.client.StyleUtil.setStyle;
import claro.catalog.manager.client.Page;
import claro.catalog.manager.client.widgets.CatalogManagerMasterDetail;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

import easyenterprise.lib.sexpr.BuiltinFunctions;
import easyenterprise.lib.sexpr.DefaultContext;

public class ImportPage extends Page {

	private final LayoutPanel mainPanel;
	private DefaultContext context = new DefaultContext();
	private CatalogManagerMasterDetail masterDetail;
	private ImportSoureModel model;
	private ImportExportRibbon header;
	private ImportSourceMaster master;
	private TabLayoutPanel tabs;
	private ImportMainPanel importSourceMainPanel;
	private ImportPropertiesPanel propertiesPanel;
	private MultiFileImportPanel multiFilePanel;
	private ImportFileFormatPanel fileFormatPanel;
	private ImportDataMappingPanel propertyMappingPanel;
	private ImportHistoryPanel2 historyPanel;
	private ImportLogPanel logPanel;

	public ImportPage(PlaceController placeController) {
		super(placeController);
		context.addFunctions(BuiltinFunctions.functions);
		mainPanel = new LayoutPanel();
		setStyle(mainPanel, createStyle("ImportPage"));
		initWidget(mainPanel);
		this.model = new ImportSoureModel() {
			
			@Override
			protected void renderAll() {
				if (getImportSource() == null) {
					masterDetail.closeDetail(false);
					master.render();
				} else {
					importSourceMainPanel.render();
					propertiesPanel.render();
					multiFilePanel.render();
					fileFormatPanel.render();
					propertyMappingPanel.render();
					historyPanel.render();
					logPanel.render();
					master.render();
					// must render master first!
					masterDetail.setCurrentRow(master.findObject(getImportSource()));
				}
			}

			@Override
			protected void showFileFormat() {
				tabs.selectTab(fileFormatPanel);
			}

			@Override
			protected void showDataMapping() {
				tabs.selectTab(propertyMappingPanel);
			}

			@Override
			protected void openDetail() {
				masterDetail.openDetail();
			}
			
			@Override
			protected void showLog() {
				tabs.selectTab(logPanel);
			}
		};
	}

	@Override
	public void show() {
		model.fetchImportSources();
	}

	@Override
	protected void initialize() {
		
		mainPanel.add(masterDetail = new CatalogManagerMasterDetail() {{
			setHeader(header = new ImportExportRibbon(model));
			setMaster(master = new ImportSourceMaster(model));
			setDetail(new DockLayoutPanel(Unit.PX) {{
			  addNorth(importSourceMainPanel = new ImportMainPanel(model), 190);
			  add(tabs = new TabLayoutPanel(40, Unit.PX) {{
  				add(propertiesPanel = new ImportPropertiesPanel(model), "Properties");
  				add(multiFilePanel = new MultiFileImportPanel(model), messages.multiFileTab());
  				add(fileFormatPanel = new ImportFileFormatPanel(model), messages.fileFormatTab());
  				add(propertyMappingPanel = new ImportDataMappingPanel(model), messages.dataMappingTab());
  				add(historyPanel = new ImportHistoryPanel2(model), messages.history());
  				add(logPanel = new ImportLogPanel(model), messages.log());
  			}});
			}});
			setRowChangedHandler(new ValueChangeHandler<Integer>() {
				public void onValueChange(ValueChangeEvent<Integer> event) {
					model.setImportSource(model.getImportSources().get(masterDetail.getCurrentRow()));
				}
			});
		}});
	}
}

//
//public void setImportSources(List<ImportSource> importSources) {
//	model.setImportSources(importSources);
//	if (isDetailOpen()) {
//		int index = importSources.indexOf(model.getImportSource());
//		if (index < 0) {
//			model.setImportSource(null);
//			closeDetail(false);
//		} else {
//			openDetail(index);
//		}
//	}
//	master.render();
//}
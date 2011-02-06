package claro.catalog.manager.client.importing;

import static easyenterprise.lib.gwt.client.StyleUtil.createStyle;
import static easyenterprise.lib.gwt.client.StyleUtil.setStyle;
import claro.catalog.manager.client.Page;
import claro.catalog.manager.client.widgets.CatalogManagerMasterDetail;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.LayoutPanel;

import easyenterprise.lib.gwt.client.widgets.EEButton;
import easyenterprise.lib.gwt.client.widgets.PullUpTabs;
import easyenterprise.lib.sexpr.BuiltinFunctions;
import easyenterprise.lib.sexpr.DefaultContext;

public class ImportPage extends Page {

	private final LayoutPanel mainPanel;
	private DefaultContext context = new DefaultContext();
	private CatalogManagerMasterDetail masterDetail;
	private ImportSoureModel model;
	private ImportExportRibbon header;
	private ImportSourceMaster master;
	private PullUpTabs tabs;
	private ImportMainPanel importSourceMainPanel;
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
				} else {
					importSourceMainPanel.render();
					multiFilePanel.render();
					fileFormatPanel.render();
					propertyMappingPanel.render();
					historyPanel.render();
					logPanel.render();
					masterDetail.setCurrentRow(master.findObject(getImportSource()));
				}
				master.render();
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

	@Override
	public void show() {
		model.fetchImportSources();
	}

	@Override
	protected void initialize() {
		
		mainPanel.add(masterDetail = new CatalogManagerMasterDetail(100) {{
			setHeader(header = new ImportExportRibbon(model));
			setMaster(master = new ImportSourceMaster(model));
			setDetail(tabs = new PullUpTabs(26, 5) {{
				setMainWidget(importSourceMainPanel = new ImportMainPanel(model));
				addTab(new EEButton(messages.multiFileTab()), 100, multiFilePanel = new MultiFileImportPanel(model));
				addTab(new EEButton(messages.fileFormatTab()), 110, fileFormatPanel = new ImportFileFormatPanel(model));
				addTab(new EEButton(messages.dataMappingTab()), 140, propertyMappingPanel = new ImportDataMappingPanel(model));
				addTab(new EEButton(messages.history()), 100, historyPanel = new ImportHistoryPanel2(model));
				addTab(new EEButton(messages.log()), 80, logPanel = new ImportLogPanel(model));
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
package claro.catalog.manager.client.importing;

import claro.catalog.command.importing.GetImportSources;
import claro.catalog.command.importing.UpdateImportSource;
import claro.catalog.manager.client.Page;
import claro.catalog.manager.client.command.StatusCallback;
import claro.jpa.importing.ImportSource;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.LayoutPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.command.gwt.RetryingCallback;
import easyenterprise.lib.sexpr.BuiltinFunctions;
import easyenterprise.lib.sexpr.DefaultContext;

public class ImportPage extends Page {

	private final LayoutPanel mainPanel;
	private boolean initialized;
	private DefaultContext context = new DefaultContext();
	private ImportMasterPanel masterPanel;

	public ImportPage(PlaceController placeController) {
		super(placeController);
		context.addFunctions(BuiltinFunctions.functions);
		mainPanel = new LayoutPanel();
		initWidget(mainPanel);
	}

	@Override
	public Place getPlace() {
		return null;
	}

	@Override
	public void show() {
		initializeMainPanel();
		updateImportSources();
	}

	private void initializeMainPanel() {
		if (initialized) return;
		initialized = true;
		
		mainPanel.add(masterPanel = new ImportMasterPanel(100, 100) {
			protected ImportSource updateImportSource(ImportSource importSource) {
				return ImportPage.this.updateImportSource(importSource);
			}
		});
	}
	
	protected void createImportSource() {
		UpdateImportSource command = new UpdateImportSource();
		command.importSource = new ImportSource();
		GwtCommandFacade.executeWithRetry(command, 3, new StatusCallback<UpdateImportSource.Result>(messages.creatingImportSource()) {
			public void onSuccess(UpdateImportSource.Result result) {
			}
		});
	}

	private void updateImportSources() {
		GetImportSources command = new GetImportSources();
		GwtCommandFacade.executeWithRetry(command, 3, new StatusCallback<GetImportSources.Result>(messages.loadingImportSources()) {
			public void onSuccess(GetImportSources.Result result) {
				masterPanel.setImportSources(result.ImportSources);
				System.out.println("yes");
			}
		});
	}
		
	protected ImportSource updateImportSource(ImportSource importSource) {
		return importSource;
	}
}


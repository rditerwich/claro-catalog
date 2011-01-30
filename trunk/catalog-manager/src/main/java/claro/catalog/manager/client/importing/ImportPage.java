package claro.catalog.manager.client.importing;

import static easyenterprise.lib.util.CollectionUtil.firstOrNull;
import claro.catalog.command.importing.GetImportSources;
import claro.catalog.command.importing.StoreImportSource;
import claro.catalog.manager.client.Page;
import claro.catalog.manager.client.command.StatusCallback;
import claro.jpa.importing.ImportSource;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.LayoutPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.sexpr.BuiltinFunctions;
import easyenterprise.lib.sexpr.DefaultContext;

public class ImportPage extends Page {

	private final LayoutPanel mainPanel;
	private DefaultContext context = new DefaultContext();
	private ImportMasterDetail masterDetail;

	public ImportPage(PlaceController placeController) {
		super(placeController);
		context.addFunctions(BuiltinFunctions.functions);
		mainPanel = new LayoutPanel();
		mainPanel.setStylePrimaryName("ImportPage");
		initWidget(mainPanel);

		ImportingStyles.instance.ensureInjected();
	}

	@Override
	public Place getPlace() {
		return null;
	}

	@Override
	public void show() {
		getImportSources();
	}

	@Override
	protected void initialize() {
		
		mainPanel.add(masterDetail = new ImportMasterDetail(100, 0) {
			protected void updateImportSource(ImportSource importSource) {
				ImportPage.this.getImportSource(importSource);
			}
			protected void storeImportSource(StoreImportSource command) {
				ImportPage.this.storeImportSource(command);
			}
		});
	}
	
	protected void createImportSource() {
		StoreImportSource command = new StoreImportSource(new ImportSource());
		GwtCommandFacade.executeWithRetry(command, 3, new StatusCallback<StoreImportSource.Result>(messages.creatingImportSourceMessage()) {
			public void onSuccess(StoreImportSource.Result result) {
			}
		});
	}

	private void getImportSources() {
		GetImportSources command = new GetImportSources();
		GwtCommandFacade.executeWithRetry(command, 3, new StatusCallback<GetImportSources.Result>(messages.loadingImportSourcesMessage()) {
			public void onSuccess(GetImportSources.Result result) {
				masterDetail.setImportSources(result.importSources);
			}
		});
	}
		
	protected void getImportSource(final ImportSource importSource) {
		GetImportSources command = new GetImportSources();
		command.importSourceId = importSource.getId();
		command.includeDefinitionDetails = true;
		GwtCommandFacade.executeWithRetry(command, 3, new StatusCallback<GetImportSources.Result>() {
			public void onSuccess(GetImportSources.Result result) {
				masterDetail.importSourceChanged(importSource, firstOrNull(result.importSources));
			}
		});
	}
	
	protected void storeImportSource(final StoreImportSource command) {
		final ImportSource importSource = command.importSource;
		GwtCommandFacade.executeWithRetry(command, 3, new StatusCallback<StoreImportSource.Result>() {
			public void onSuccess(StoreImportSource.Result result) {
				masterDetail.importSourceChanged(importSource, result.importSource);
			}
		});
	}
}


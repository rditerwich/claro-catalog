package claro.catalog.manager.client.importing;

import java.sql.Timestamp;

import claro.catalog.command.importing.PerformImport;
import claro.catalog.command.importing.PerformImport.Result;
import claro.catalog.command.importing.StoreImportSource;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.ConfirmationDialog;
import claro.catalog.manager.client.widgets.FormTable;
import claro.jpa.jobs.JobResult;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.gwt.client.widgets.EEButton;
import easyenterprise.lib.gwt.client.widgets.EEButtonBar;
import easyenterprise.lib.gwt.client.widgets.Header;
import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.ModalUploadStatus;
import gwtupload.client.SingleUploader;

public class ImportMainPanel extends Composite implements Globals {

	private final ImportSoureModel model;
	private HTML lastRunTimeHTML;
	private HTML lastRunUrlHTML;
	private Anchor lastRunLog;
	private Header header;
	private Image lastRunStatusImage;
	
	private ConfirmationDialog removeWithConfirmation = new ConfirmationDialog(messages.removeImportSourceConfirmationMessage(), images.removeIcon()) {
		protected void yesPressed() {
			StoreImportSource command = new StoreImportSource(model.getImportSource());
			command.removeImportSource = true;
			model.store(command);
			model.renderAll();
		}
	};
	
	@SuppressWarnings("unchecked")
	public ImportMainPanel(ImportSoureModel model_) {
		this.model = model_;
		initWidget(new VerticalPanel() {{
			setStylePrimaryName("ImportMainPanel");
			add(new Grid(1, 2) {{
				setWidget(0, 0, header = new Header(1, "") {{
				}});
				setWidget(0, 1, new Button(messages.removeImportSourceLink()) {{
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							removeWithConfirmation.show();
						}
					});
				}});
			}});
			add(new Grid(1, 2) {{
				setStylePrimaryName("statusTable");
				setWidget(0, 0, lastRunStatusImage = new Image(""));
				setWidget(0, 1, new VerticalPanel() {{
					add(lastRunTimeHTML = new HTML());
					add(lastRunUrlHTML = new HTML());
					add(lastRunLog = new Anchor(messages.showLogLink()));
				}});
			}});
			add(new EEButtonBar() {{
				add(new Button(messages.importNowButton()));
				add(new SingleUploader(FileInputType.CUSTOM.with(new Button(messages.importFileButton()), false), new ModalUploadStatus()) {{
					setAutoSubmit(true);
					addOnFinishUploadHandler(new OnFinishUploaderHandler() {
						public void onFinish(IUploader uploader) {
							importNow(uploader);
						}
					});
				}});
				add(new Button(messages.removeImportedDataButton()) {{
					getElement().getStyle().setMarginLeft(-28, Unit.PX); // correct for whitespace right of SingleUpLoader
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							new ConfirmationDialog(messages.removeImportedDataMessage(), images.removeIcon()) {
								protected void yesPressed() {
									// TODO Auto-generated method stub
									model.deleteImportedData();
								}
							}.show();
						}
					});
				}});
			}}); 
		}});
	}
	
	public void render() {
		removeWithConfirmation.hide();
		header.setText(model.getImportSource().getName());
		
		Boolean lastSuccess = model.getImportSource().getJob().getLastSuccess();
		Timestamp lastTime = model.getImportSource().getJob().getLastTime();
		String lastUrl = model.getImportSource().getLastImportedUrl();
		lastRunStatusImage.setResource(lastSuccess == null || lastTime == null ? images.warning() : lastSuccess ? images.ok() : images.error());
		lastRunTimeHTML.setHTML(lastSuccess == null || lastTime == null ? messages.notRunMessage() : messages.lastRunLabel() + ": <i>" + lastTime.toString() + "</i>");
		lastRunUrlHTML.setHTML(lastSuccess == null || lastUrl == null ? "" : messages.lastRunUrlLabel() + ": <i>" + lastUrl.toString() + "</i>");
		lastRunLog.setVisible(lastSuccess != null);
	}
	
	private void importNow(IUploader uploader) {
		if (uploader == null || uploader.getStatus() == Status.SUCCESS) {
			PerformImport performImport = new PerformImport();
			performImport.catalogId = CatalogManager.getCurrentCatalogId();
			performImport.importSourceId = model.getImportSource().getId();
			if (uploader != null) {
				performImport.uploadFieldName = uploader.getServerInfo().field;
			}
			GwtCommandFacade.execute(performImport, new AsyncCallback<PerformImport.Result>() {
				public void onFailure(Throwable caught) {
					System.out.println("FAILED");
				}

				@Override
				public void onSuccess(Result result) {
					for (JobResult jobResult : result.jobResults) 
					  System.out.println("SUCCES:" + jobResult.getLog());
				}
			}); 
		}
	}
}

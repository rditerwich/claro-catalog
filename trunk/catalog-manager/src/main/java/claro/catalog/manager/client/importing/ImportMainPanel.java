package claro.catalog.manager.client.importing;

import static easyenterprise.lib.util.ObjectUtil.orFalse;

import java.sql.Timestamp;

import claro.catalog.command.importing.PerformImport;
import claro.catalog.command.importing.PerformImport.Result;
import claro.catalog.command.importing.StoreImportSource;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.FormTable;
import claro.jpa.importing.ImportRules;
import claro.jpa.importing.ImportSource;
import claro.jpa.jobs.JobResult;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.widgets.Header;
import easyenterprise.lib.gwt.client.widgets.InlinePanel;
import gwtupload.client.IFileInput.FileInputType;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.ModalUploadStatus;
import gwtupload.client.SingleUploader;

public abstract class ImportMainPanel extends Composite implements Globals {

	enum Styles implements Style { ImportMainPanel }
	
	private TextBox nameTextBox;
	private TextBox importUrlTextBox;
	private CheckBox incrementalCheckBox;
	private CheckBox sequentialCheckBox;
	private CheckBox orderedCheckBox;
	private CheckBox multiFileCheckBox;
	private ImportSource importSource;
	private HTML lastRunTimeHTML;
	private HTML lastRunUrlHTML;
	private Label lastStatusLabel;
	private Anchor lastRunLog;
	private Header header;
	private Image lastRunStatusImage;
	
	@SuppressWarnings("unchecked")
	public ImportMainPanel() {
		initWidget(new VerticalPanel() {{
			setStylePrimaryName(Styles.ImportMainPanel.toString());
			add(header = new Header(1, ""));
			add(new Grid(1, 3) {{
				setStylePrimaryName("statusTable");
				setWidget(0, 0, lastRunStatusImage = new Image(""));
				setWidget(0, 1, new VerticalPanel() {{
					add(lastRunTimeHTML = new HTML());
					add(lastRunUrlHTML = new HTML());
				}});
				getColumnFormatter().setStylePrimaryName(1, "col2");
				setWidget(0, 2, lastRunLog = new Anchor(messages.showLogLink()));
			}});
			add(new FormTable() {{
				add(messages.nameLabel(), nameTextBox = new TextBox(), messages.importSourceNameHelp());
				add(messages.importUrlLabel(), importUrlTextBox = new TextBox(), messages.importUrlHelp());
				add(messages.incrementalImportLabel(), incrementalCheckBox = new CheckBox(), messages.incrementalImportHelp());
				add(messages.sequentialImportNamesLabel(), sequentialCheckBox = new CheckBox(), messages.sequentialImportNamesHelp());
				add(messages.orderedImportNamesLabel(), orderedCheckBox = new CheckBox(), messages.orderedImportNamesHelp());
				add(messages.incrementalImportLabel(), incrementalCheckBox = new CheckBox(), messages.incrementalImportHelp());
				add(messages.multiFileImportLabel(), multiFileCheckBox = new CheckBox(), messages.multiFileImportHelp());
			}});
			add(new Header(2, messages.actionsHeader()));
			add(new HorizontalPanel() {{
				add(new Anchor(messages.importNowLink()) {{
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							importNow(null);
						}
					});
				}});
				final Button button = new Button();
				add(new SingleUploader(FileInputType.ANCHOR, new ModalUploadStatus(), button) {{
					setAutoSubmit(true);
					button.setText(messages.importNowButton());
					addOnFinishUploadHandler(new OnFinishUploaderHandler() {
						public void onFinish(IUploader uploader) {
							importNow(uploader);
						}
					});
				}});
				
			}});
			add(new FormTable() {{
				add(messages.lastStatusLabel(), new InlinePanel("&#160;", lastStatusLabel = new Label(), new Anchor(messages.showLogLink())), messages.lastStatusHelp());
				lastRunLog.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						showLastRunLog();
					}
				});
			}});
		}});
		
		ValueChangeHandler<?> changeHandler = new ValueChangeHandler<Object>() {
			public void onValueChange(ValueChangeEvent<Object> event) {
				importSource.setName(nameTextBox.getText());
				importSource.setImportUrl(importUrlTextBox.getText());
				importSource.setIncremental(incrementalCheckBox.getValue());
				importSource.setOrderedUrl(orderedCheckBox.getValue());
				importSource.setMultiFileImport(multiFileCheckBox.getValue());
				storeImportSource(new StoreImportSource(importSource));
			}
		};
		nameTextBox.addValueChangeHandler((ValueChangeHandler<String>) changeHandler);
		importUrlTextBox.addValueChangeHandler((ValueChangeHandler<String>) changeHandler);
		incrementalCheckBox.addValueChangeHandler((ValueChangeHandler<Boolean>) changeHandler);
		sequentialCheckBox.addValueChangeHandler((ValueChangeHandler<Boolean>) changeHandler);
		orderedCheckBox.addValueChangeHandler((ValueChangeHandler<Boolean>) changeHandler);
		multiFileCheckBox.addValueChangeHandler((ValueChangeHandler<Boolean>) changeHandler);
	}
	
	public void setImportSource(ImportSource importSource) {
		this.importSource = importSource;
		header.setText(importSource.getName());
		nameTextBox.setText(importSource.getName());
		importUrlTextBox.setText(importSource.getImportUrl());
		incrementalCheckBox.setValue(importSource.getIncremental());
		sequentialCheckBox.setValue(importSource.getSequentialUrl());
		orderedCheckBox.setValue(importSource.getOrderedUrl());
		multiFileCheckBox.setValue(orFalse(importSource.getMultiFileImport()));
		
		Boolean lastSuccess = importSource.getJob().getLastSuccess();
		Timestamp lastTime = importSource.getJob().getLastTime();
		String lastUrl = importSource.getLastImportedUrl();
		lastRunStatusImage.setResource(lastSuccess == null || lastTime == null ? images.warning() : lastSuccess ? images.ok() : images.error());
		lastRunTimeHTML.setHTML(lastSuccess == null || lastTime == null ? messages.notRunMessage() : messages.lastRunLabel() + ": <i>" + lastTime.toString() + "</i>");
		lastRunUrlHTML.setHTML(lastSuccess == null || lastUrl == null ? "" : messages.lastRunUrlLabel() + ": <i>" + lastUrl.toString() + "</i>");
		lastStatusLabel.setText(lastSuccess == null ? messages.notRunMessage() : (lastSuccess ? messages.success() : messages.failed()));
		lastRunLog.setVisible(lastSuccess != null);
	}
	
	private void importNow(IUploader uploader) {
		if (uploader == null || uploader.getStatus() == Status.SUCCESS) {
			PerformImport performImport = new PerformImport();
			performImport.catalogId = CatalogManager.getCurrentCatalogId();
			performImport.importSourceId = importSource.getId();
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
	
	protected abstract void storeImportSource(StoreImportSource command);
	protected abstract void showFileFormat(ImportRules rules);
	protected abstract void showImportRules(ImportRules rules);
	protected abstract void showLastRunLog();
}

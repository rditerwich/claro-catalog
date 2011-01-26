package claro.catalog.manager.client.importing;

import static easyenterprise.lib.util.ObjectUtil.orFalse;
import claro.catalog.command.importing.PerformImport;
import claro.catalog.command.importing.PerformImport.Result;
import claro.catalog.command.importing.StoreImportSource;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.ActionImage;
import claro.catalog.manager.client.widgets.ActionLabel;
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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.widgets.Header;
import easyenterprise.lib.gwt.client.widgets.InlinePanel;
import easyenterprise.lib.gwt.client.widgets.Table;
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
	private Label lastStatusLabel;
	private Anchor lastRunLog;
	private InlinePanel singleFileLinks;
	private Table multiFileLinks;
	private Header header;
	private Image statusImage;
	
	@SuppressWarnings("unchecked")
	public ImportMainPanel() {
		initWidget(new VerticalPanel() {{
			setStylePrimaryName(Styles.ImportMainPanel.toString());
			add(header = new Header(1, ""));
			add(new HorizontalPanel() {{
				add(statusImage = new Image(""));
				add(new VerticalPanel() {{
					add(lastRunLog = new Anchor(messages.showLogLink()));
				}});
			}});
			add(new FormTable() {{
				add(messages.importSourceLabel(), nameTextBox = new TextBox(), messages.importSourceNameHelp());
				add(messages.importUrlLabel(), importUrlTextBox = new TextBox(), messages.importUrlHelp());
				add(messages.incrementalImportLabel(), incrementalCheckBox = new CheckBox(), messages.incrementalImportHelp());
				add(messages.sequentialImportNamesLabel(), sequentialCheckBox = new CheckBox(), messages.sequentialImportNamesHelp());
				add(messages.orderedImportNamesLabel(), orderedCheckBox = new CheckBox(), messages.orderedImportNamesHelp());
				add(messages.incrementalImportLabel(), incrementalCheckBox = new CheckBox(), messages.incrementalImportHelp());
				add(messages.multiFileImportLabel(), multiFileCheckBox = new CheckBox(), messages.multiFileImportHelp());
				add("", singleFileLinks = createRulesLinks(null), "");
				add(multiFileLinks = new Table() {{
					setVisible(false);
				}}, "");
			}});
			add(new FormTable() {{
				add(messages.lastStatusLabel(), new InlinePanel("&#160;", lastStatusLabel = new Label(), new Anchor(messages.showLogLink())), messages.lastStatusHelp());
				lastRunLog.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						showLastRunLog();
					}
				});
				final Button button = new Button();
				add(new SingleUploader(FileInputType.BROWSER_INPUT, new ModalUploadStatus(), button) {{
					button.setText(messages.importNowButton());
					addOnFinishUploadHandler(new OnFinishUploaderHandler() {
						public void onFinish(IUploader uploader) {
							importNow(uploader);
						}
					});
				}}, messages.importNowHelp());
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
		
		singleFileLinks.setVisible(!orFalse(importSource.getMultiFileImport()));
		multiFileLinks.setVisible(orFalse(importSource.getMultiFileImport()));
		if (orFalse(importSource.getMultiFileImport())) {
			multiFileLinks.resize(importSource.getRules().size() + 2, 3);
			multiFileLinks.resizeHeaderRows(1);
			multiFileLinks.setHeaderText(0, 0, messages.relativeImportUrlLabel());
			multiFileLinks.setHeaderText(0, 1, messages.detailsLabel());
			int row = 0;
			for (final ImportRules rules : importSource.getRules()) {
				multiFileLinks.setWidget(row, 0, new Label(rules.getRelativeUrl()));
				multiFileLinks.setWidget(row, 1, createRulesLinks(rules));
				multiFileLinks.setWidget(row, 2, new ActionImage(images.removeImmediately(), new ClickHandler() {
					public void onClick(ClickEvent event) {
						removeNestedFile(rules);
					}
				}));
				row++;
			}
			multiFileLinks.setWidget(row, 0, new ActionLabel(messages.addNestedFileLink(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					addNestedFile();
				}					
			}));
		}
		Boolean lastSuccess = orFalse(importSource.getJob().getLastSuccess());
		statusImage.setResource(lastSuccess ? images.ok() : images.error());
		lastStatusLabel.setText(lastSuccess == null ? messages.notRun() : (lastSuccess ? messages.success() : messages.failed()));
		lastRunLog.setVisible(lastSuccess != null);
	}

	private InlinePanel createRulesLinks(final ImportRules rules) {
		return new InlinePanel("&#160;", 
			new Anchor(messages.fileFormatLink()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						showFileFormat(rules);
					}
				});
			}}, 
			new Anchor(messages.importRulesLink()) {{
			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					showImportRules(rules);
				}
			});
		}});
	}

	private void importNow(IUploader uploader) {
		if (uploader.getStatus() == Status.SUCCESS) {
			PerformImport performImport = new PerformImport();
			performImport.catalogId = CatalogManager.getCurrentCatalogId();
			performImport.importSourceId = importSource.getId();
			performImport.uploadFieldName = uploader.getServerInfo().field;
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
	
	private void addNestedFile() {
		ImportRules rules = new ImportRules();
		importSource.getRules().add(rules);
		storeImportSource(new StoreImportSource(importSource));
	}

	private void removeNestedFile(ImportRules rules) {
		importSource.getRules().remove(rules);
		storeImportSource(new StoreImportSource(importSource));
	}
	
	protected abstract void storeImportSource(StoreImportSource command);
	protected abstract void showFileFormat(ImportRules rules);
	protected abstract void showImportRules(ImportRules rules);
	protected abstract void showLastRunLog();
}

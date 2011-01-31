package claro.catalog.manager.client.importing;

import java.util.Collections;

import claro.catalog.command.importing.StoreImportSource;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.FormTable;
import claro.jpa.importing.ImportRules;
import claro.jpa.importing.ImportSource;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.gwt.client.widgets.InlinePanel;
import easyenterprise.lib.gwt.client.widgets.Table;

public abstract class MultiFileImportPanel extends Composite implements Globals {

	private ImportSource importSource;
	private Table multiFileLinks;
	protected CheckBox isMultiFile;
	protected FlowPanel tablePanel;
	
	public MultiFileImportPanel() {
		initWidget(new VerticalPanel() {{
			setStylePrimaryName("MultiFileImportPanel");
			add(new FormTable() {{
				add(messages.multiFileImportLabel(), isMultiFile = new CheckBox(), messages.multiFileImportHelp());
			}});
			isMultiFile.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						importSource.setMultiFileImport(isMultiFile.getValue());
						tablePanel.setVisible(isMultiFile.getValue());
						storeImportSource(new StoreImportSource(importSource));
					}
				});
				add(tablePanel = new FlowPanel() {{
					add(multiFileLinks = new Table(0, 3) {{
						setHeaderText(0, 0, messages.relativeImportUrlLabel());
					}});
					add(new Anchor(messages.addNestedFileLink()) {{
						addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								ImportRules rules = new ImportRules();
								importSource.getRules().add(rules);
								render();
								storeImportSource(new StoreImportSource(importSource));
							}					
						});
					}});
				}});
		}});
	}
	
	public void setImportSource(ImportSource importSource) {
		this.importSource = importSource;
		render();
	}
	
	private void render() {
		isMultiFile.setValue(importSource.getMultiFileImport());
		tablePanel.setVisible(isMultiFile.getValue());
		if (importSource.getMultiFileImport() && importSource.getRules().isEmpty()) {
			importSource.getRules().add(new ImportRules());
		}
		multiFileLinks.resize(importSource.getRules().size(), 3);
		int row = 0;
		for (ImportRules rules : importSource.getRules()) {
			multiFileLinks.setWidget(row, 0, createTextBox(rules));
			multiFileLinks.setWidget(row, 1, createRemoveImage(rules));
			multiFileLinks.setWidget(row, 2, createRulesLinks(rules));
			row++;
		}
	}

	private TextBox createTextBox(final ImportRules rules) {
		TextBox textbox;
		textbox = new TextBox() {{
			setText(rules.getRelativeUrl());
			addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					rules.setRelativeUrl(getValue());
					storeImportSource(new StoreImportSource(importSource));
				}
			});
		}};
		return textbox;
	}
	
	private InlinePanel createRulesLinks(final ImportRules rules) {
		return new InlinePanel(" ", 
			new Anchor(messages.fileFormatLink()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						showFileFormat(rules);
					}
				});
			}}, 
			new Anchor(messages.dataMappingsLink()) {{
			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					showDataMapping(rules);
				}
			});
		}});
	}

	private Image createRemoveImage(final ImportRules rules) {
		return new Image(images.removeImmediately()) {{
			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					importSource.getRules().remove(rules);
					render();
					StoreImportSource command = new StoreImportSource(importSource);
					command.importRulesToBeRemoved = Collections.singletonList(rules);
					storeImportSource(command);
				}
			});
		}};
	}

	protected abstract void storeImportSource(StoreImportSource command);
	protected abstract void showFileFormat(ImportRules rules);
	protected abstract void showDataMapping(ImportRules rules);
}

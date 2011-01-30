package claro.catalog.manager.client.importing;

import static com.google.common.base.Objects.equal;

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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.gwt.client.widgets.InlinePanel;

public abstract class ImportFileFormatPanel extends Composite implements Globals {

	private ImportSource importSource;
	protected CheckBox isMultiFile;
	protected FlowPanel tablePanel;
	private ImportRules currentRules;
	private HorizontalPanel nestedFilePanel;
	private ListBox nestedFileListBox;

	
	public ImportFileFormatPanel() {
		initWidget(new VerticalPanel() {{
			add(nestedFilePanel = new HorizontalPanel() {{
				add(new Label(messages.selectNestedFileLabel()));
				add(nestedFileListBox = new ListBox() {{
					addChangeHandler(new ChangeHandler() {
						public void onChange(ChangeEvent event) {
							currentRules = importSource.getRules().get(getSelectedIndex());
							render();
						}
					});
				}});
			}});
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
	
	public void setImportRules(ImportRules rules) {
		this.currentRules = rules;
		render();
	}

	private void render() {
		nestedFilePanel.setVisible(isMultiFile.getValue());
		int row = 0;
		nestedFileListBox.clear();
		for (ImportRules rules : importSource.getRules()) {
			nestedFileListBox.addItem(rules.getRelativeUrl());
			if (equal(currentRules, rules)) {
				nestedFileListBox.setSelectedIndex(row);
			}
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
			new Anchor(messages.importRulesLink()) {{
			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					showImportRules(rules);
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
	protected abstract void showImportRules(ImportRules rules);
}

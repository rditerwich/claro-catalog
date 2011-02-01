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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.gwt.client.widgets.InlinePanel;

public abstract class ImportFileFormatPanel extends Composite implements Globals {

	private ImportSource importSource;
	protected FlowPanel tablePanel;
	private ImportRules currentRules;
	private ListBox nestedFileListBox;
	private ListBox fileFormatListBox;
	protected FormTable formTable;
	
	public ImportFileFormatPanel() {
		initWidget(new VerticalPanel() {{
			add(formTable = new FormTable() {{
				add(messages.selectNestedFileLabel(), nestedFileListBox = new ListBox() {{
					addChangeHandler(new ChangeHandler() {
						public void onChange(ChangeEvent event) {
							currentRules = importSource.getRules().get(getSelectedIndex());
							render();
						}
					});
				}}, messages.selectNestedFileHelp());
				add(messages.fileFormatLabel(), fileFormatListBox = new ListBox() {{
					addItem("CSV");
					addItem("Excel");
					addItem("XML");
				}}, messages.fileFormatHelp());
				add(messages.headerLineLabel(), new CheckBox(), messages.headerLineHelp());
				add(messages.fieldSeparatorLabel(), new TextBox(){{setText(";,");}}, messages.fieldSeparatorHelp());
				add(messages.charsetLabel(), new ListBox() {{
					addItem("UTF-8");
					addItem("ASCII");
					addItem("ISO_LATIN_1");
					addItem("ISO_LATIN_1");
				}}, messages.charsetHelp());
			}});
				add(tablePanel = new FlowPanel() {{
					add(new Anchor(messages.dataMappingsLink()) {{
						addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								showDataMapping(currentRules);
							}					
						});
					}});
				}});
		}});
	}
	
	public void setImportSourceAndRules(ImportSource importSource, ImportRules rules) {
		this.importSource = importSource;
		this.currentRules = rules;
		render();
	}

	private void render() {
		formTable.setRowVisible(nestedFileListBox, importSource.getMultiFileImport());
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

package claro.catalog.manager.client.importing;

import java.util.Collections;

import claro.catalog.command.importing.StoreImportSource;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.FormTable;
import claro.jpa.importing.ImportRules;

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

public class MultiFileImportPanel extends Composite implements Globals {

	private Table multiFileLinks;
	protected CheckBox isMultiFile;
	protected FlowPanel tablePanel;
	private final ImportSoureModel model;
	
	public MultiFileImportPanel(ImportSoureModel model_) {
		this.model = model_;
		initWidget(new VerticalPanel() {{
			setStylePrimaryName("MultiFileImportPanel");
			add(new FormTable() {{
				add(messages.multiFileImportLabel(), isMultiFile = new CheckBox(), messages.multiFileImportHelp());
			}});
			isMultiFile.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						model.getImportSource().setMultiFileImport(isMultiFile.getValue());
						tablePanel.setVisible(isMultiFile.getValue());
						model.store(new StoreImportSource(model.getImportSource()));
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
								model.getImportSource().getRules().add(rules);
								render();
								model.store(new StoreImportSource(model.getImportSource()));
							}					
						});
					}});
				}});
		}});
	}
	
	public void render() {
		isMultiFile.setValue(model.getImportSource().getMultiFileImport());
		tablePanel.setVisible(isMultiFile.getValue());
		if (model.getImportSource().getMultiFileImport() && model.getImportSource().getRules().isEmpty()) {
			model.getImportSource().getRules().add(new ImportRules());
		}
		multiFileLinks.resize(model.getImportSource().getRules().size(), 3);
		int row = 0;
		for (ImportRules rules : model.getImportSource().getRules()) {
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
					model.store(new StoreImportSource(model.getImportSource()));
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
						model.setRules(rules);
						model.showFileFormat();
					}
				});
			}}, 
			new Anchor(messages.dataMappingsLink()) {{
			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					model.setRules(rules);
					model.showDataMapping();
				}
			});
		}});
	}

	private Image createRemoveImage(final ImportRules rules) {
		return new Image(images.removeImmediately()) {{
			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					model.getImportSource().getRules().remove(rules);
					render();
					StoreImportSource command = new StoreImportSource(model.getImportSource());
					command.importRulesToBeRemoved = Collections.singletonList(rules);
					model.store(command);
				}
			});
		}};
	}
}

package claro.catalog.manager.client.importing;

import claro.catalog.command.importing.StoreImportSource;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.ConfirmationDialog;
import claro.catalog.manager.client.widgets.FormTable;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.gwt.client.widgets.Header;

public class ImportPropertiesPanel extends Composite implements Globals {

	private final ImportSoureModel model;
	private TextBox nameTextBox;
	private TextBox importUrlTextBox;
	private CheckBox incrementalCheckBox;
	private CheckBox sequentialCheckBox;
	private CheckBox orderedCheckBox;
	
	private ConfirmationDialog removeWithConfirmation = new ConfirmationDialog(messages.removeImportSourceConfirmationMessage(), images.removeIcon()) {
		protected void yesPressed() {
			StoreImportSource command = new StoreImportSource(model.getImportSource());
			command.removeImportSource = true;
			model.store(command);
			model.renderAll();
		}
	};
	
	@SuppressWarnings("unchecked")
	public ImportPropertiesPanel(ImportSoureModel model_) {
		this.model = model_;
		initWidget(new VerticalPanel() {{
			setStylePrimaryName("ImportMainPanel");

			add(new FormTable() {{
				add(messages.nameLabel(), nameTextBox = new TextBox(), messages.importSourceNameHelp());
				add(messages.importUrlLabel(), importUrlTextBox = new TextBox(), messages.importUrlHelp());
				add(messages.incrementalImportLabel(), incrementalCheckBox = new CheckBox(), messages.incrementalImportHelp());
				add(messages.sequentialImportNamesLabel(), sequentialCheckBox = new CheckBox(), messages.sequentialImportNamesHelp());
				add(messages.orderedImportNamesLabel(), orderedCheckBox = new CheckBox(), messages.orderedImportNamesHelp());
			}});		
		}});
		
		ValueChangeHandler<?> changeHandler = new ValueChangeHandler<Object>() {
			public void onValueChange(ValueChangeEvent<Object> event) {
				model.getImportSource().setName(nameTextBox.getText());
				model.getImportSource().setImportUrl(importUrlTextBox.getText());
				model.getImportSource().setIncremental(incrementalCheckBox.getValue());
				model.getImportSource().setSequentialUrl(sequentialCheckBox.getValue());
				model.getImportSource().setOrderedUrl(orderedCheckBox.getValue());
				model.store(new StoreImportSource(model.getImportSource()));
			}
		};
		nameTextBox.addValueChangeHandler((ValueChangeHandler<String>) changeHandler);
		importUrlTextBox.addValueChangeHandler((ValueChangeHandler<String>) changeHandler);
		incrementalCheckBox.addValueChangeHandler((ValueChangeHandler<Boolean>) changeHandler);
		sequentialCheckBox.addValueChangeHandler((ValueChangeHandler<Boolean>) changeHandler);
		orderedCheckBox.addValueChangeHandler((ValueChangeHandler<Boolean>) changeHandler);
	}
	
	public void render() {
		removeWithConfirmation.hide();
		nameTextBox.setText(model.getImportSource().getName());
		importUrlTextBox.setText(model.getImportSource().getImportUrl());
		incrementalCheckBox.setValue(model.getImportSource().getIncremental());
		sequentialCheckBox.setValue(model.getImportSource().getSequentialUrl());
		orderedCheckBox.setValue(model.getImportSource().getOrderedUrl());
	}
	
}

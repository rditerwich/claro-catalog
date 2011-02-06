package claro.catalog.manager.client.importing;

import static com.google.common.base.Objects.equal;
import claro.catalog.command.importing.StoreImportSource;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.FormTable;
import claro.jpa.importing.ImportFileFormat;
import claro.jpa.importing.ImportRules;

import com.google.common.base.Objects;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ImportFileFormatPanel extends Composite implements Globals {

	private static final String CSV = "CSV";
	private static final String EXCEL = "Excel";
	private static final String XML = "XML";
	
	private FlowPanel tablePanel;
	private ListBox nestedFileListBox;
	private ListBox fileFormatListBox;
	private FormTable formTable;
	private CheckBox headerLineCheckbox;
	private TextBox separatorsTextbox;
	
	private final ImportSoureModel model;
	
	public ImportFileFormatPanel(ImportSoureModel model_) {
		this.model = model_;
		initWidget(new VerticalPanel() {{
			add(formTable = new FormTable() {{
				add(messages.selectNestedFileLabel(), nestedFileListBox = new ListBox() {{
					addChangeHandler(new ChangeHandler() {
						public void onChange(ChangeEvent event) {
							model.setRules(getSelectedIndex());
						}
					});
				}}, messages.selectNestedFileHelp());
				add(messages.fileFormatLabel(), fileFormatListBox = new ListBox() {{
					addItem(CSV);
					addItem(XML);
					addChangeHandler(new ChangeHandler() {
						public void onChange(ChangeEvent event) {
							String format = getItemText(getSelectedIndex());
							if (CSV.equals(format)) model.getRules().setFileFormat(model.getCSVFileFormat());
							if (XML.equals(format)) model.getRules().setFileFormat(model.getXMLFileFormat());
							model.store(new StoreImportSource(model.getImportSource()));
						}
					});
				}}, messages.fileFormatHelp());
				add(messages.headerLineLabel(), headerLineCheckbox = new CheckBox() {{
					addValueChangeHandler(new ValueChangeHandler<Boolean>() {
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							model.getCSVFileFormat().setHeaderLine(event.getValue());
							model.store(new StoreImportSource(model.getImportSource()));
						}
					});
				}}, messages.headerLineHelp());
				add(messages.fieldSeparatorLabel(), separatorsTextbox = new TextBox() {{
					setText(";,");
					addValueChangeHandler(new ValueChangeHandler<String>() {
						public void onValueChange(ValueChangeEvent<String> event) {
							model.getCSVFileFormat().setSeparatorChars(event.getValue());
							model.store(new StoreImportSource(model.getImportSource()));
						}
					});
				}}, messages.fieldSeparatorHelp());
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
								model.showDataMapping();
							}					
						});
					}});
				}});
		}});
	}
	
	void render() {
		
		// nested files
		formTable.setRowVisible(nestedFileListBox, model.getImportSource().getMultiFileImport());
		int row = 0;
		nestedFileListBox.clear();
		for (ImportRules rules : model.getImportSource().getRules()) {
			nestedFileListBox.addItem(rules.getRelativeUrl());
			if (equal(model.getImportSource(), rules)) {
				nestedFileListBox.setSelectedIndex(row);
			}
			row++;
		}
		
		headerLineCheckbox.setValue(model.getCSVFileFormat().getHeaderLine());
		separatorsTextbox.setValue(model.getCSVFileFormat().getSeparatorChars());
	}
}

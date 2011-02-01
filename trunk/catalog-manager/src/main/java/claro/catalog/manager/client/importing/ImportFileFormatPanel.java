package claro.catalog.manager.client.importing;

import static com.google.common.base.Objects.equal;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.FormTable;
import claro.jpa.importing.ImportRules;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ImportFileFormatPanel extends Composite implements Globals {

	protected FlowPanel tablePanel;
	private ListBox nestedFileListBox;
	private ListBox fileFormatListBox;
	protected FormTable formTable;
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
								model.showDataMapping();
							}					
						});
					}});
				}});
		}});
	}

	void render() {
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
	}
}

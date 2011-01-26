package claro.catalog.manager.client.importing;

import java.util.Collections;

import claro.catalog.command.importing.StoreImportSource;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.ActionImage;
import claro.catalog.manager.client.widgets.ActionLabel;
import claro.catalog.manager.client.widgets.AlternatePanel;
import claro.jpa.importing.ImportRules;
import claro.jpa.importing.ImportSource;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.gwt.client.widgets.InlinePanel;
import easyenterprise.lib.gwt.client.widgets.Table;

public abstract class MultiFileImportPanel extends Composite implements Globals {

	private ImportSource importSource;
	private AlternatePanel alternate;
	private Table multiFileLinks;
	
	public MultiFileImportPanel() {
		initWidget(alternate = new AlternatePanel(new Label(messages.notAMultiFileImportSourceMessage()),
				new VerticalPanel() {{
					add(multiFileLinks = new Table(2, 3) {{
						setHeaderText(0, 0, messages.relativeImportUrlLabel());
						setHeaderText(0, 1, messages.detailsLabel());
					}});
					add(new ActionLabel(messages.addNestedFileLink(), new ClickHandler() {
						public void onClick(ClickEvent event) {
							ImportRules rules = new ImportRules();
							importSource.getRules().add(rules);
							render();
							storeImportSource(new StoreImportSource(importSource));
						}					
					}));
				}}));
		alternate.setStylePrimaryName("MultiFileImportPanel");
	}
	
	public void setImportSource(ImportSource importSource) {
		this.importSource = importSource;
		render();
	}
	
	private void render() {
		multiFileLinks.resize(importSource.getRules().size() + 1, 3);
		for (int row = 0; row < importSource.getRules().size(); row++) {
			TextBox textbox = (TextBox) multiFileLinks.getWidget(row, 0);
			if (textbox == null) {
				textbox = createTextBox(row);
				multiFileLinks.setWidget(row, 0, textbox);
				multiFileLinks.setWidget(row, 1, createRulesLinks(row));
				multiFileLinks.setWidget(row, 2, createRemoveImage(row));
			}
			row++;
		}
		alternate.showSecond(importSource.getMultiFileImport());
	}

	private TextBox createTextBox(final int row) {
		TextBox textbox;
		textbox = new TextBox() {{
			addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					ImportRules rules = importSource.getRules().get(row);	
					rules.setRelativeUrl(getValue());
					storeImportSource(new StoreImportSource(importSource));
				}
			});
		}};
		return textbox;
	}
	
	private InlinePanel createRulesLinks(final int row) {
		return new InlinePanel(" ", 
			new Anchor(messages.fileFormatLink()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						ImportRules rules = importSource.getRules().get(row);	
						showFileFormat(rules);
					}
				});
			}}, 
			new Anchor(messages.importRulesLink()) {{
			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					ImportRules rules = importSource.getRules().get(row);	
					showImportRules(rules);
				}
			});
		}});
	}

	private ActionImage createRemoveImage(final int row) {
		return new ActionImage(images.removeImmediately(), new ClickHandler() {
			public void onClick(ClickEvent event) {
				ImportRules rules = importSource.getRules().get(row);
				importSource.getRules().remove(rules);
				render();
				StoreImportSource command = new StoreImportSource(importSource);
				command.importRulesToBeRemoved = Collections.singletonList(rules);
				storeImportSource(command);
			}
		});
	}

	protected abstract void storeImportSource(StoreImportSource command);
	protected abstract void showFileFormat(ImportRules rules);
	protected abstract void showImportRules(ImportRules rules);
}

package claro.catalog.manager.client.importing;

import static com.google.common.base.Objects.equal;
import static easyenterprise.lib.util.ObjectUtil.orElse;
import claro.catalog.command.items.FindProperties;
import claro.catalog.data.PropertyInfo;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.jpa.importing.ImportProperty;
import claro.jpa.importing.ImportSource;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.gwt.client.widgets.SExprEditor;
import easyenterprise.lib.util.ObjectUtil;

public abstract class ImportSourcePropertyMappingsPanel extends Composite implements Globals {

	private VerticalPanel panel;
	private Grid propertyGrid;
	private ImportSource importSource;
	
	public ImportSourcePropertyMappingsPanel() {
		initWidget(panel = new VerticalPanel() {{
			add(propertyGrid = new Grid(0, 2));
			add(new Anchor(messages.addPropertyMapping()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						importSource.getProperties().add(new ImportProperty());
						render();
					}
				});
			}});
		}});
		
	}
	
	public void setImportSource(ImportSource importSource) {
		this.importSource = importSource;
		render();
	}

	private void render() {
		for (int row = propertyGrid.getRowCount(); row < importSource.getProperties().size(); row++) {
			propertyGrid.resizeRows(row + 1);
			final ListBox listBox = new ListBox();
			listBox.addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					int selectedIndex = listBox.getSelectedIndex();
					importSourceChanged();
				}
			});
			propertyGrid.setWidget(row, 0, listBox);
			propertyGrid.setWidget(row, 1, new SExprEditor() {{
				addValueChangeHandler(new ValueChangeHandler<String>() {
					public void onValueChange(ValueChangeEvent<String> event) {
						importSourceChanged();
					}
				});
			}});
		}
		FindProperties command = new FindProperties();
		command.catalogId = CatalogManager.getCurrentCatalogId();
		GwtCommandFacade.executeCached(command, 60 * 60 * 1000, new AsyncCallback<FindProperties.Result>() {
			public void onFailure(Throwable caught) {
			}
			public void onSuccess(FindProperties.Result result) {
				int row = 0;
				for (ImportProperty property : importSource.getProperties()) {
					ListBox listBox = (ListBox) propertyGrid.getWidget(row, 0);
					listBox.clear();
					for (PropertyInfo info : result.properties) {
						listBox.addItem(info.labels.tryGet(CatalogManager.getUiLanguage(), null));
						if (equal(info.propertyId, property.getId())) {
							listBox.setSelectedIndex(listBox.getItemCount() - 1);
						}
					}
					((SExprEditor) propertyGrid.getWidget(row, 1)).setExpression(orElse(property.getValueExpression(), ""));
					row++;
				}
			}
		});
	}
	
	protected abstract void importSourceChanged();
	
}

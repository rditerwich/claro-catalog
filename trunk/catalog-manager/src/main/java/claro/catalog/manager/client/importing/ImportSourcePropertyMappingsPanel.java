package claro.catalog.manager.client.importing;

import static com.google.common.base.Objects.equal;
import static easyenterprise.lib.util.ObjectUtil.orElse;
import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.List;

import claro.catalog.command.importing.StoreImportSource;
import claro.catalog.command.items.FindProperties;
import claro.catalog.data.PropertyInfo;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.jpa.catalog.Property;
import claro.jpa.importing.ImportProperty;
import claro.jpa.importing.ImportSource;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.gwt.client.widgets.SExprEditor;
import easyenterprise.lib.gwt.client.widgets.Table;
import easyenterprise.lib.util.Tuple;

public abstract class ImportSourcePropertyMappingsPanel extends Composite implements Globals {

	private Table propertyGrid;
	private ImportSource importSource;
	private final List<Tuple<String, Long>> properties = new ArrayList<Tuple<String,Long>>();
	
	public ImportSourcePropertyMappingsPanel() {
		initWidget(new VerticalPanel() {{
			add(propertyGrid = new Table(0, 3) {{
				setHeaderText(0, 0, messages.property());
				setHeaderText(0, 1, messages.expression());
			}});
			add(new Anchor(messages.addPropertyMapping()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						ImportProperty importProperty = new ImportProperty();
						importSource.getProperties().add(importProperty);
						importProperty.setValueExpression("");
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
		int i = propertyGrid.getRowCount();
		propertyGrid.resizeRows(importSource.getProperties().size());
		for (; i < importSource.getProperties().size(); i++) { 
			final int row = i;
			propertyGrid.setWidget(row, 0, new ListBox() {{
				addChangeHandler(new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						storeProperties();

					}
				});
			}});
			propertyGrid.setWidget(row, 1, new SExprEditor() {{
				addValueChangeHandler(new ValueChangeHandler<String>() {
					public void onValueChange(ValueChangeEvent<String> event) {
						storeProperties();
					}
				});
			}});
			propertyGrid.setWidget(row, 2, new Image(images.removeImmediately()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						ImportProperty importProperty = getImportProperty(row);
						if (importProperty.getId() != null) {
							importSource.getProperties().remove(importProperty);
							StoreImportSource command = new StoreImportSource(importSource);
							command.skipImportSource = true;
							command.importPropertiesToBeRemoved = Lists.newArrayList(importProperty);
							storeImportSource(command);
						} else {
							importSource.getProperties().remove(importProperty);
							render();
						}
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
				properties.clear();
				for (PropertyInfo info : result.properties) {
					properties.add(Tuple.create(info.labels.tryGet(CatalogManager.getUiLanguage(), null), info.propertyId));
				}
				sort(properties);
				for (ImportProperty importProperty : importSource.getProperties()) {
					ListBox listBox = (ListBox) propertyGrid.getWidget(row, 0);
					listBox.clear();
					listBox.setSelectedIndex(0);
					for (Tuple<String, Long> info : properties) {
						listBox.addItem(info.getFirst());
						if (importProperty.getProperty() != null && equal(info.getSecond(), importProperty.getProperty().getId())) {
							listBox.setSelectedIndex(listBox.getItemCount() - 1);
						}
					}
					((SExprEditor) propertyGrid.getWidget(row, 1)).setExpression(orElse(importProperty.getValueExpression(), ""));
					row++;
				}
			}
		});
	}
	
	private ImportProperty getImportProperty(int row) {
		return Iterables.get(importSource.getProperties(), row);
	}
	
	private void storeProperties() {
		boolean changed = false;
		int row = 0;
		for (ImportProperty importProperty : importSource.getProperties()) {
			int selectedIndex = ((ListBox) propertyGrid.getWidget(row, 0)).getSelectedIndex();
			Long propertyId = properties.get(selectedIndex).getSecond();
			if (!equal(propertyId, importProperty.getId())) {
				Property property = new Property();
				property.setId(propertyId);
				importProperty.setProperty(property);
				changed = true;
			}
			String expression = ((SExprEditor) propertyGrid.getWidget(row, 1)).getExpression();
			if (!equal(expression, importProperty.getValueExpression())) {
				importProperty.setValueExpression(expression);
				storeImportSource(new StoreImportSource(importSource));
				changed = true;
			}
			row++;
		}
		if (changed) {
			storeImportSource(new StoreImportSource(importSource));
		}
	}
	
	protected abstract void storeImportSource(StoreImportSource command);
	
}

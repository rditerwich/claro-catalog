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
import claro.catalog.manager.client.widgets.Help;
import claro.jpa.catalog.Property;
import claro.jpa.importing.ImportProducts;
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
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.gwt.client.widgets.SExprEditor;
import easyenterprise.lib.gwt.client.widgets.Table;
import easyenterprise.lib.util.Tuple;

public abstract class ImportDefinitionPanel extends Composite implements Globals {

	private ImportProducts importProducts = new ImportProducts();
	
	private Table propertyGrid;
	private ImportSource importSource;
	private SExprEditor importUrlEditor;
	private ListBox matchPropertyListBox;
	private final List<Tuple<String, Long>> properties = new ArrayList<Tuple<String,Long>>();

	private ValueChangeHandler<String> valueChangeHandler = new ValueChangeHandler<String>() {
		public void onValueChange(ValueChangeEvent<String> event) {
			doStore();
		}
	};
	
	protected final ChangeHandler changeHandler = new ChangeHandler() {
		public void onChange(ChangeEvent event) {
			doStore();
		}
	};
	
	public ImportDefinitionPanel() {
		initWidget(new VerticalPanel() {{
			add(new Grid(2, 3) {{
				setWidget(0, 0, new Label(messages.importUrlLabel()));
				setWidget(0, 1, importUrlEditor = new SExprEditor() {{
					addValueChangeHandler(valueChangeHandler);
				}});
				setWidget(0, 2, new Help(messages.importUrlHelp()));
				setWidget(1, 0, new Label(messages.matchProperty()));
				setWidget(1, 1, matchPropertyListBox = new ListBox() {{
					addChangeHandler(changeHandler);
				}});
				setWidget(1, 2, new Help(messages.matchPropertyHelp()));
			}});
			add(propertyGrid = new Table(0, 3) {{
				setHeaderText(0, 0, messages.property());
				setHeaderText(0, 1, messages.expression());
			}});
			add(new Anchor(messages.addPropertyMapping()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						ImportProperty importProperty = new ImportProperty();
						importProducts.getProperties().add(importProperty);
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
		importUrlEditor.setExpression(orElse(importSource.getImportUrl(), ""));
		
		int i = propertyGrid.getRowCount();
		propertyGrid.resizeRows(importProducts.getProperties().size());
		for (; i < importProducts.getProperties().size(); i++) { 
			final int row = i;
			propertyGrid.setWidget(row, 0, new ListBox() {{
				addChangeHandler(changeHandler);
			}});
			propertyGrid.setWidget(row, 1, new SExprEditor() {{
				addValueChangeHandler(valueChangeHandler);
			}});
			propertyGrid.setWidget(row, 2, new Image(images.removeImmediately()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						ImportProperty importProperty = getImportProperty(row);
						if (importProperty.getId() != null) {
							importProducts.getProperties().remove(importProperty);
							StoreImportSource command = new StoreImportSource(importSource);
//							command.skipImportSource = true;
							command.importPropertiesToBeRemoved = Lists.newArrayList(importProperty);
							storeImportSource(command);
						} else {
							importProducts.getProperties().remove(importProperty);
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
				fillPropertyListbox(matchPropertyListBox, importProducts.getMatchProperty());
				
				int row = 0;
				properties.clear();
				for (PropertyInfo info : result.properties) {
					properties.add(Tuple.create(info.labels.tryGet(CatalogManager.getUiLanguage(), null), info.propertyId));
				}
				sort(properties);
				for (ImportProperty importProperty : importProducts.getProperties()) {
					ListBox listBox = (ListBox) propertyGrid.getWidget(row, 0);
					fillPropertyListbox(listBox, importProperty.getProperty());
					SExprEditor editor = (SExprEditor) propertyGrid.getWidget(row, 1);
					String expr = orElse(importProperty.getValueExpression(), "");
					if (!editor.getExpression().equals(expr)) {
						editor.setExpression(expr);
					}
					row++;
				}
			}
		});
	}
	
	private ImportProperty getImportProperty(int row) {
		return Iterables.get(importProducts.getProperties(), row);
	}
	
	private void fillPropertyListbox(ListBox listBox, Property currentProperty) {
		listBox.clear();
		listBox.setSelectedIndex(0);
		for (Tuple<String, Long> info : properties) {
			listBox.addItem(info.getFirst());
			if (currentProperty != null && equal(info.getSecond(), currentProperty.getId())) {
				listBox.setSelectedIndex(listBox.getItemCount() - 1);
			}
		}
	}

	private void doStore() {
		boolean changed = false;
		
		System.out.println(importUrlEditor.getExpression());
		if (!importSource.getImportUrl().equals(importUrlEditor.getExpression())) {
			changed = true;
			System.out.println(importUrlEditor.getExpression());
			importSource.setImportUrl(importUrlEditor.getExpression());
		}
		
		int selectedIndex = matchPropertyListBox.getSelectedIndex();
		Long propertyId = properties.get(selectedIndex).getSecond();
		if (importProducts.getMatchProperty() == null || !propertyId.equals(importProducts.getMatchProperty().getId())) {
			changed = true;
			Property property = new Property();
			property.setId(propertyId);
			importProducts.setMatchProperty(property);
		}
		
		int row = 0;
		for (ImportProperty importProperty : importProducts.getProperties()) {
			selectedIndex = ((ListBox) propertyGrid.getWidget(row, 0)).getSelectedIndex();
			propertyId = properties.get(selectedIndex).getSecond();
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

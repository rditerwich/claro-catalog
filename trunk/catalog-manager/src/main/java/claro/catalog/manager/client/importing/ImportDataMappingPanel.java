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
import claro.catalog.manager.client.widgets.ActionImage;
import claro.catalog.manager.client.widgets.ItemSelectionWidget;
import claro.catalog.manager.client.widgets.FormTable;
import claro.jpa.catalog.Property;
import claro.jpa.importing.ImportProducts;
import claro.jpa.importing.ImportProperty;
import claro.jpa.importing.ImportRules;

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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.gwt.client.widgets.SExprEditor;
import easyenterprise.lib.gwt.client.widgets.Table;
import easyenterprise.lib.util.SMap;
import easyenterprise.lib.util.Tuple;

public class ImportDataMappingPanel extends Composite implements Globals {

	private Table propertyGrid;
	private ListBox matchPropertyListBox;
	private ListBox nestedFileListBox;
	private ItemSelectionWidget categoryPanel;
	private final List<Tuple<String, Long>> properties = new ArrayList<Tuple<String,Long>>();
	private final ImportSoureModel model;

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
	protected FormTable formTable;
	
	public ImportDataMappingPanel(ImportSoureModel s) {
		this.model = s;
		initWidget(new VerticalPanel() {{
			setStylePrimaryName("ImportDataMappingPanel");
			add(formTable = new FormTable() {{
				add(messages.selectNestedFileLabel(), nestedFileListBox = new ListBox() {{
					addChangeHandler(new ChangeHandler() {
						public void onChange(ChangeEvent event) {
							model.setRules(getSelectedIndex());
						}
					});
				}}, messages.selectNestedFileHelp());
				add(messages.matchProperty(), matchPropertyListBox = new ListBox() {{
					addChangeHandler(changeHandler);
				}}, messages.matchPropertyHelp());
				add(messages.selectCategoryLabel(), categoryPanel = new ItemSelectionWidget() {
						protected String getAddCategoryTooltip() {
							return messages.addCategoryProductDetailsTooltip("RUUD");  // TODO This is a little dirty??
						}
						protected String getRemoveCategoryTooltip(String categoryName) {
							return messages.removeCategoryProductDetailsTooltip(categoryName);
						}
						@Override
						protected void addCategory(Long categoryId, SMap<String, String> labels) {
							super.addCategory(categoryId, labels);
//							categoryAdded(itemId, categoryId);
						}
						protected void removeCategory(Long categoryId) {
							super.removeCategory(categoryId);
//							categoryRemoved(itemId, categoryId);
						}
					}, messages.selectCategoryHelp());
			}});
			add(propertyGrid = new Table(0, 3) {{
				setHeaderText(0, 0, messages.property());
				setHeaderText(0, 1, messages.expression());
			}});
			add(new Anchor(messages.addPropertyMapping()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						ImportProperty importProperty = new ImportProperty();
						model.getImportProducts().getProperties().add(importProperty);
						importProperty.setValueExpression("");
						render();
					}
				});
			}});
		}});
		
	}

	void render() {
		formTable.setRowVisible(nestedFileListBox, model.getImportSource().getMultiFileImport());
		int r = 0;
		nestedFileListBox.clear();
		for (ImportRules rules : model.getImportSource().getRules()) {
			nestedFileListBox.addItem(rules.getRelativeUrl());
			if (rules == model.getRules()) {
				nestedFileListBox.setSelectedIndex(r);
			}
			r++;
		}
		
		categoryPanel.setData(SMap.<Long, SMap<String, String>>empty(), null);

		int i = propertyGrid.getRowCount();
		propertyGrid.resizeRows(model.getImportProducts().getProperties().size());
		for (; i < model.getImportProducts().getProperties().size(); i++) { 
			final int row = i;
			propertyGrid.setWidget(row, 0, new ListBox() {{
				addChangeHandler(changeHandler);
			}});
			propertyGrid.setWidget(row, 1, new SExprEditor() {{
				addValueChangeHandler(valueChangeHandler);
			}});
			propertyGrid.setWidget(row, 2, new ActionImage(images.removeImmediately(), new ClickHandler() {
				public void onClick(ClickEvent event) {
					ImportProducts importProducts = model.getImportProducts();
					ImportProperty importProperty = getImportProperty(row);
					if (importProperty.getId() != null) {
						importProducts.getProperties().remove(importProperty);
						StoreImportSource command = new StoreImportSource(model.getImportSource());
//							command.skipImportSource = true;
						command.importPropertiesToBeRemoved = Lists.newArrayList(importProperty);
						model.store(command);
					} else {
						importProducts.getProperties().remove(importProperty);
						render();
					}
				}
			}));
		}
		FindProperties command = new FindProperties();
		command.catalogId = CatalogManager.getCurrentCatalogId();
		GwtCommandFacade.executeCached(command, 60 * 60 * 1000, new AsyncCallback<FindProperties.Result>() {
			public void onFailure(Throwable caught) {
			}
			public void onSuccess(FindProperties.Result result) {
				if (model.getImportSource() == null) return;
				ImportProducts importProducts = model.getImportProducts();
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
		return Iterables.get(model.getImportProducts().getProperties(), row);
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
		
		int selectedIndex = matchPropertyListBox.getSelectedIndex();
		Long propertyId = properties.get(selectedIndex).getSecond();
		if (model.getImportProducts().getMatchProperty() == null || !propertyId.equals(model.getImportProducts().getMatchProperty().getId())) {
			changed = true;
			Property property = new Property();
			property.setId(propertyId);
			model.getImportProducts().setMatchProperty(property);
		}

		int row = 0;
		for (ImportProperty importProperty : model.getImportProducts().getProperties()) {
			selectedIndex = ((ListBox) propertyGrid.getWidget(row, 0)).getSelectedIndex();
			propertyId = properties.get(selectedIndex).getSecond();
			if (!equal(propertyId, importProperty.getId())) {
				Property property = new Property();
				property.setId(propertyId);
				importProperty.setProperty(property);
				changed = true;
			}
			String expression = ((SExprEditor) propertyGrid.getWidget(row, 1)).getExpression();
			System.out.println("Expression: " + expression);
			if (!equal(expression, importProperty.getValueExpression())) {
				importProperty.setValueExpression(expression);
				changed = true;
			}
			row++;
		}
		if (changed) {
			model.store(new StoreImportSource(model.getImportSource()));
		}
	}
}

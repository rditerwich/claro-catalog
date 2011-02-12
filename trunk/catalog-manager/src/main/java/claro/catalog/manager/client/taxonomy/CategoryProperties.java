package claro.catalog.manager.client.taxonomy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.jpa.catalog.PropertyType;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.util.SMap;

abstract public class CategoryProperties extends Composite implements Globals {
	public enum Styles implements Style { clear, valueParent, valueWidget, categoryProperties }
	private static int NAME_COLUMN = 0;
	private static int TYPE_COLUMN = 1;
	private static int GROUP_COLUMN = 2;
	private static int CLEAR_COLUMN = 3;
	private static int NR_COLS = 4;
	private static Comparator<PropertyType> typeComparator = new Comparator<PropertyType> () {
		public int compare(PropertyType o1, PropertyType o2) {
			return o1.name().compareToIgnoreCase(o2.name());
		}
	};
	private static PropertyType[] propertyTypes = PropertyType.values();
	{
		Arrays.sort(propertyTypes, typeComparator);
	}

	private TabPanel propertyGroupPanel;
	private List<PropertyValueWidgets> valueWidgets = new ArrayList<PropertyValueWidgets>();
	private Map<Widget, PropertyInfo> propertyByValueWidget = new HashMap<Widget, PropertyInfo>();
	
	
	
	private SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> values;
	private Long itemId;
	private SMap<Long, SMap<String, String>> groups;
	private SMap<Long, SMap<String, String>> parentExtentWithSelf;
	private Grid propertyPanel;
	private TaxonomyModel model;
	
	public CategoryProperties() {
		
		initWidget(new VerticalPanel() {{
			StyleUtil.addStyle(this, Styles.categoryProperties);
			add(new Anchor("Define new property...") {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						createNewProperty();
					}
				});
			}}); // TODO i18n.
			add(propertyPanel = new Grid(0, NR_COLS));
		}});
	}
	
	public void setModel(TaxonomyModel model) {
		this.model = model;
	}
	
	/**
	 * TODO Change to only receive property/group data.
	 * set the item data and (re)render.
	 * 
	 * @param itemId
	 * @param values
	 */
	public void setItemData(Long itemId, SMap<Long, SMap<String, String>> groups, SMap<Long, SMap<String, String>> parentExtentWithSelf, SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> values) {
		this.itemId = itemId;
		this.groups = groups;
		this.parentExtentWithSelf = parentExtentWithSelf;
		this.values = values;
		render();
	}
	
	/**
	 * Called when the value of a property is set.
	 * @param itemId
	 * @param propertyInfo
	 * @param language
	 */
	protected abstract void propertyToSet(Long itemId, PropertyInfo propertyInfo, String language);

	protected abstract void propertyToRemove(Long itemId, PropertyInfo propertyInfo, String language);
	
	private void render() {
		List<PropertyGroupInfo> propertyGroups = values.getKeys();
		int requiredNrRows = getNrProperties(values);

		// Remove redundant properties
		int oldRowCount = propertyPanel.getRowCount();
		propertyPanel.resizeRows(requiredNrRows);  
		for (int j = oldRowCount - 1; j >= requiredNrRows; j--) {
			valueWidgets.remove(j);
		}
		
		// Create new rows
		for (int j = oldRowCount; j < requiredNrRows; j++) {
			final PropertyValueWidgets propertyValueWidgets = new PropertyValueWidgets();
			final int row = j;
			// name
			propertyPanel.setWidget(j, NAME_COLUMN, propertyValueWidgets.nameWidget = new TextBox() {{
				addChangeHandler(new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						propetyNameChanged(row, getText());
					}
				});
			}}); // TODO Change listener.
			
			// type
			propertyPanel.setWidget(j, TYPE_COLUMN, propertyValueWidgets.typeWidget = new ListBox() {{
				for (PropertyType type : propertyTypes) {
					addItem(type.name(), type.name());// TODO How about i18n??
				}
				addChangeHandler(new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						propertyTypeChanged(row, PropertyType.valueOf(getValue(getSelectedIndex())));
					}
				});
			}}); 
			
			// groups
			propertyPanel.setWidget(j, GROUP_COLUMN, propertyValueWidgets.propertyGroupsWidget = new ListBox() {{
				addChangeHandler(new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						propertyGroupChanged(row, getValue(getSelectedIndex()));
					}
				});
			}});
			
			// Clear button
			propertyPanel.setWidget(j, CLEAR_COLUMN, propertyValueWidgets.clearValueWidget = new Image() {{
				StyleUtil.addStyle(this, Styles.clear);
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							propertyErased(row);
						}
					});
				}
			}); 

			valueWidgets.add(propertyValueWidgets);
		}

		// (re)bind widgets
		int i = 0;
		propertyByValueWidget.clear();
		for (PropertyGroupInfo propertyGroup : propertyGroups) {
			
			
			SMap<PropertyInfo, PropertyData> properties = values.get(propertyGroup);
			List<PropertyInfo> propertyKeys = properties.getKeys();
			
			for (PropertyInfo property : propertyKeys) {
				PropertyValueWidgets propertyValueWidgets = valueWidgets.get(i);
				PropertyData propertyData = properties.get(property);
				
				// Set name
				String propertyName = property.labels.tryGet(CatalogManager.getUiLanguage(), null);
				propertyValueWidgets.nameWidget.setText(propertyName);
				
				// set type
				propertyValueWidgets.typeWidget.setSelectedIndex(Arrays.binarySearch(propertyTypes, property.getType(), typeComparator)); 
				
				
				// Groups
				propertyValueWidgets.propertyGroupsWidget.clear();
				int j = 0;
				for (Entry<Long, SMap<String, String>> group : groups) {
					// add item
					String groupName = group.getValue().tryGet(model.getSelectedLanguage(), null);
					propertyValueWidgets.propertyGroupsWidget.addItem(groupName);
					
					// check selection
					if (group.getKey().equals(propertyGroup.propertyGroupId)) {
						propertyValueWidgets.propertyGroupsWidget.setSelectedIndex(j);
					}
					j++;
				}
				
				i++;
			}
		}
	}
	
	private void createNewProperty() {
		// TODO Make a more clean implementation
		PropertyGroupInfo group = values.getFirstKey();
		SMap<PropertyInfo, PropertyData> properties = values.get(group, SMap.<PropertyInfo, PropertyData>empty());
		PropertyInfo newProperty = new PropertyInfo();
		newProperty.setType(PropertyType.String); // Type is required.
		properties = properties.add(newProperty, null);
		values = values.set(group, properties);
		render();
	}

	protected void propetyNameChanged(int row, String text) {
		PropertyInfo rowProperty = getRowProperty(row);
		if (rowProperty.labels == null) {
			rowProperty.labels = SMap.empty();
		}
		rowProperty.labels = rowProperty.labels.set(model.getSelectedLanguage(), text);
		propertyToSet(itemId, rowProperty, model.getSelectedLanguage());
	}
	private void propertyTypeChanged(int row, PropertyType type) {
		PropertyInfo rowProperty = getRowProperty(row);
		rowProperty.setType(type);
		propertyToSet(itemId, rowProperty, model.getSelectedLanguage());
	}
	private void propertyGroupChanged(int row, String group) {
		PropertyInfo rowProperty = getRowProperty(row);
		PropertyGroupInfo groupInfo = values.getKeys().get(row);
		
		// TODO
	}
	
	private void propertyErased(int row) {
		PropertyInfo rowProperty = getRowProperty(row);
		propertyToRemove(itemId, rowProperty, model.getSelectedLanguage());
	}
	
	private PropertyInfo getRowProperty(int row) {
		int i = 0;
		for (PropertyGroupInfo propertyGroup : values.getKeys()) {
			SMap<PropertyInfo, PropertyData> properties = values.get(propertyGroup);
			List<PropertyInfo> propertyKeys = properties.getKeys();
			
			for (PropertyInfo property : propertyKeys) {
				if (row == i) {
					return property;
				}
				i++;
			}
		}
		return null;
	}
	
	private static int getNrProperties(SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> values) {
		int result = 0;
		for (Entry<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> group : values) {
			result += group.getValue().getKeys().size();
		}
		return result;
	}
	
	private class PropertyValueWidgets {
		protected Widget clearValueWidget;
		public TextBox nameWidget;
		public ListBox typeWidget;
		public Grid valueParentWidget;
		public ListBox propertyGroupsWidget;
	}
}

package claro.catalog.manager.client;

import java.util.ArrayList;
import java.util.List;

import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.PropertyType;
import claro.jpa.catalog.Source;
import claro.jpa.catalog.StagingArea;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.util.SMap;

public class ItemPropertyValues extends Composite {
	private static int NAME_COLUMN = 0;
	private static int TYPE_COLUMN = 1;
	private static int VALUE_COLUMN = 2;
	private static int LANG_COLUMNS = 3;
	private static int NR_FIXED_COLS = 3;

	TabLayoutPanel propertyGroupPanel;
	List<GroupPanelWidgets> groupPanels = new ArrayList<GroupPanelWidgets>();
	
	private List<String> languages;
	private SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> values;
	private Long itemId;
	private String uiLanguage;
	
	public ItemPropertyValues() {
		propertyGroupPanel = new TabLayoutPanel(20, Unit.PX);
		initWidget(propertyGroupPanel);
	}
	
	public void setUiLanguage(String uiLanguage) {
		this.uiLanguage = uiLanguage;
		
		render();
	}
	
	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}
	
	/**
	 * set the item data and (re)render.
	 * 
	 * @param itemId
	 * @param values
	 */
	public void setItemData(Long itemId, SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> values) {
		this.itemId = itemId;
		this.values = values;
		render();
	}
	
	private void render() {
		List<PropertyGroupInfo> propertyGroups = values.getKeys();
		// Remove extraneous property groups.
		int oldPanelCount = propertyGroupPanel.getWidgetCount();
		for (int i = oldPanelCount; i >= propertyGroups.size(); i--) {
			groupPanels.remove(i);
			propertyGroupPanel.remove(i);
		}
		
		// Create new panels
		for (int i = oldPanelCount; i < propertyGroups.size(); i++) {
			GroupPanelWidgets propertyGroupWidgets = new GroupPanelWidgets();
			
			propertyGroupPanel.add(propertyGroupWidgets.panel = new Grid(0, NR_FIXED_COLS));
			
			groupPanels.add(propertyGroupWidgets);
			
		}
		
		// Rebind property groups
		int i = 0;
		for (PropertyGroupInfo propertyGroup : propertyGroups) {
			
			// TabPanel tab text:
			propertyGroupPanel.setTabText(i, propertyGroup.labels.tryGet(uiLanguage, null));

			// TabPanel tab content:
			
			GroupPanelWidgets groupPanelWidgets = groupPanels.get(i);
			SMap<PropertyInfo, PropertyData> properties = values.get(propertyGroup);
			List<PropertyInfo> propertyKeys = properties.getKeys();
			
			// Remove redundant properties
			int oldRowCount = groupPanelWidgets.panel.getRowCount();
			groupPanelWidgets.panel.resizeRows(propertyKeys.size());  // TODO Rethink this for more values per property ?? (inputsources, outputchannels, etc). 
			for (int j = oldRowCount - 1; j >= propertyKeys.size(); j--) {
				groupPanelWidgets.valueWidgets.remove(j);
			}
			
			// Create new rows
			for (int j = oldRowCount; j < propertyKeys.size(); j++) {
				PropertyValueWidgets propertyValueWidgets = new PropertyValueWidgets();
				
				// name
				groupPanelWidgets.panel.setWidget(j, NAME_COLUMN, propertyValueWidgets.nameWidget = new Label());
				
				// type
				groupPanelWidgets.panel.setWidget(j, TYPE_COLUMN, propertyValueWidgets.typeWidget = new Label());

				// Value + Clear button
				groupPanelWidgets.panel.setWidget(j, VALUE_COLUMN, propertyValueWidgets.valueParentWidget = new Grid() {{
					// Real value is added in the bind fase...
					setWidget(0, 1, new Button(Util.i18n.clearValue())); // TODO Put an image here instead.
				}});
				
				
				
				groupPanelWidgets.valueWidgets.add(propertyValueWidgets);
			}
			
			// (re)bind widgets
			int j = 0;
			for (PropertyInfo property : propertyKeys) {
				PropertyValueWidgets propertyValueWidgets = groupPanelWidgets.valueWidgets.get(j);
				PropertyData propertyData = properties.get(property);
				
				// Set name
				propertyValueWidgets.nameWidget.setText(property.labels.tryGet(uiLanguage, null));
				
				// set type
				propertyValueWidgets.typeWidget.setText(property.type.name()); // TODO How about i18n??
				
				// ensure value widget
				setValueWidget(propertyValueWidgets, j, VALUE_COLUMN, property, propertyData);
				
				j++;
			}
			
			i++;
		}
		
//		// TODO reuse widgets!!!
//		propertyGroupPanel.clear();
//		
//		propertyGroupPanel.resizeColumns(NR_FIXED_COLS + languages.size() * 2);
//
//		for (PropertyInfo property : values.getKeys()) {
//			PropertyData propertyData = values.get(property);
//
//			for (StagingArea area : propertyData.effectiveValues.getKeys()) {
//
//				SMap<OutputChannel, SMap<String, Object>> effectiveAreaValues = propertyData.effectiveValues.get(area);
//				
//				for (OutputChannel channel : propertyData.values.getKeys()) {
//					addValueRow(property, propertyData.values.get(channel), effectiveAreaValues.get(channel));
//				}
//			}
//
//			for (SMap<Source, SMap<String, Object>> outputChannelValues : propertyData.sourceValues.getAll()) {
//				for (SMap<String, Object> importSourceValues : outputChannelValues.getAll()) {
//					addValueRow(property, importSourceValues, SMap.<String, Object>empty());
//				}
//			}
//		}
	}
	
//	private void addValueRow(PropertyInfo property, SMap<String, Object> values, SMap<String, Object> effectiveValues) {
//		
//		// Find a value:
//		Object value = values.get();
//		boolean isDerived = false;
//		if (value != null) {
//			value = effectiveValues.get();
//			isDerived = true;
//		}
//
//		// Value not found, so no row
//		if (value == null) {
//			return; 
//		}
//
//		// Add a new row
//		int row = propertyGroupPanel.getRowCount();
//		propertyGroupPanel.resizeRows(row + 1);
//		
//		boolean isInherited = !itemId.equals(property.ownerItemId);
//
////		propertyBinding.setData(property);
//		propertyGroupPanel.setWidget(row, NAME_COLUMN, createNameWidget(property, null, isInherited));
//		propertyGroupPanel.setWidget(row, TYPE_COLUMN, createTypeWidget(property, isInherited));
////		mainGrid.setWidget(row, VALUE_COLUMN, createValueWidget(property, value, isDerived));
//		
//		// Make a name value pair per language.
//		int i = 0;
//		for (String language : languages) {
//			value = values.get(language);
//			isDerived = false;
//			if (value != null) {
//				value = effectiveValues.get(language);
//				isDerived = true;
//			}
//			propertyGroupPanel.setWidget(row, LANG_COLUMNS + i, createNameWidget(property, language, isInherited));
//			propertyGroupPanel.setWidget(row, LANG_COLUMNS + i + 1, createValueWidget(property, value, isDerived));
//			i++;
//		}
//	}
	

	private void setValueWidget(PropertyValueWidgets propertyValueWidgets, int row, int col, PropertyInfo property, PropertyData propertyData) {
		// TODO Auto-generated method stub
		
	}

	private Widget createNameWidget(final PropertyInfo property, final String language, boolean isReadonly) {
		if (isReadonly) {
			return new Label(property.labels.tryGet(language, null));
		} else {
			return new TextBox() {{
				setText(property.labels.tryGet(language, null));
			}};
		}
	}

	private Widget createTypeWidget(PropertyInfo property, boolean isReadonly) {
		if (isReadonly) {
			return new Label(property.type.name());
		} else {
			ListBox listbox = new ListBox();
			for (PropertyType type : PropertyType.values()) {
				listbox.addItem(type.name());
			}
			return listbox;
		}
	}

	private Widget createValueWidget(PropertyInfo property, final Object value, boolean isDerived) {
		Widget result = null;
		switch (property.type) {
//		case Enum:
			// TODO
//			EnumValuesEditWidget
//			value = ((EnumValuesEditWidget) w).bind(pvb);
//			break;
		case FormattedText:
			result = new TextBox() {{
				if (value != null) {
					setText((String)value);
				}
			}};
			break;
//		case Media:
//			value = ((MediaWidget) w).bind(pvb);
//			break;
		case String:
			result = new TextBox() {{
				if (value != null) {
					setText((String)value);
				}
			}};
			break;
		case Boolean:
			result = new CheckBox() {{
				if (value != null) {
					setValue((Boolean) value);
				}
			}};
			break;
		case Real:
			result = new TextBox() {{
				if (value != null) {
					setText(((Double)value).toString());
				}
			}};
			break;
		case Money:
			result = new TextBox() {{
				if (value != null) {
					setText(((Double)value).toString());
				}
			}};
		case Acceleration:
		case AmountOfSubstance:
		case Angle:
		case Area:
		case ElectricCurrent:
		case Energy:
		case Frequency:
		case Integer:
		case Length:
		case LuminousIntensity:
		case Power:
		case Time:
		case Mass:
		case Temperature:
		case Velocity:
		case Voltage:
		case Volume:
		default:
			result = new TextBox() {{
				if (value != null) {
					setText(((Integer)value).toString());
				}
			}};
		}
		
		Util.add(result, Styles.derived);
		
		return result;
	}
	
	private class GroupPanelWidgets {
		public Grid panel;
		public List<PropertyValueWidgets> valueWidgets = new ArrayList<PropertyValueWidgets>();
	}
	
	private class PropertyValueWidgets {
		public Label nameWidget;
		public Label typeWidget;
		public Grid valueParentWidget;
	}
}

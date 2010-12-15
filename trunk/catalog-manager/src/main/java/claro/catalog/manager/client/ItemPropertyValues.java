package claro.catalog.manager.client;

import java.util.List;

import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyInfo;
import claro.jpa.catalog.ImportSource;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyType;
import claro.jpa.catalog.StagingArea;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.util.SMap;

public class ItemPropertyValues extends Composite {
	private static int NAME_COLUMN = 0;
	private static int TYPE_COLUMN = 1;
	private static int VALUE_COLUMN = 2;
	private static int LANG_COLUMNS = 3;
	private static int NR_FIXED_COLS = 3;

	Grid mainGrid;
	private List<String> languages;
	private SMap<PropertyInfo, PropertyData> values;
	private Long itemId;
	
	public ItemPropertyValues() {
		mainGrid = new Grid();
		initWidget(mainGrid);
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
	public void setItemData(Long itemId, SMap<PropertyInfo, PropertyData> values) {
		this.itemId = itemId;
		this.values = values;
		render();
	}
	
	private void render() {
		mainGrid.clear();
		
		mainGrid.resizeColumns(NR_FIXED_COLS + languages.size() * 2);

		for (PropertyInfo property : values.getKeys()) {
			PropertyData propertyData = values.get(property);

			for (StagingArea area : propertyData.effectiveValues.getKeys()) {

				SMap<OutputChannel, SMap<String, Object>> effectiveAreaValues = propertyData.effectiveValues.get(area);
				
				for (OutputChannel channel : propertyData.values.getKeys()) {
					addValueRow(property, propertyData.values.get(channel), effectiveAreaValues.get(channel));
				}
			}

			for (SMap<ImportSource, SMap<String, Object>> outputChannelValues : propertyData.importSourceValues.getAll()) {
				for (SMap<String, Object> importSourceValues : outputChannelValues.getAll()) {
					addValueRow(property, importSourceValues, SMap.<String, Object>empty());
				}
			}
		}
	}
	
	private void addValueRow(PropertyInfo property, SMap<String, Object> values, SMap<String, Object> effectiveValues) {
		
		// Find a value:
		Object value = values.get();
		boolean isDerived = false;
		if (value != null) {
			value = effectiveValues.get();
			isDerived = true;
		}

		// Value not found, so no row
		if (value == null) {
			return; 
		}

		// Add a new row
		int row = mainGrid.getRowCount();
		mainGrid.resizeRows(row + 1);
		
		boolean isInherited = !itemId.equals(property.ownerItemId);

//		propertyBinding.setData(property);
		mainGrid.setWidget(row, NAME_COLUMN, createNameWidget(property, null, isInherited));
		mainGrid.setWidget(row, TYPE_COLUMN, createTypeWidget(property, isInherited));
//		mainGrid.setWidget(row, VALUE_COLUMN, createValueWidget(property, value, isDerived));
		
		// Make a name value pair per language.
		int i = 0;
		for (String language : languages) {
			value = values.get(language);
			isDerived = false;
			if (value != null) {
				value = effectiveValues.get(language);
				isDerived = true;
			}
			mainGrid.setWidget(row, LANG_COLUMNS + i, createNameWidget(property, language, isInherited));
			mainGrid.setWidget(row, LANG_COLUMNS + i + 1, createValueWidget(property, value, isDerived));
			i++;
		}
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
}

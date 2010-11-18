package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import agilexs.catalogxsadmin.presentation.client.binding.ExtendedItemBinding;
import agilexs.catalogxsadmin.presentation.client.binding.HasTextBinding;
import agilexs.catalogxsadmin.presentation.client.binding.ListPropertyBinding;
import agilexs.catalogxsadmin.presentation.client.binding.WidgetListBinding;
import agilexs.catalogxsadmin.presentation.client.catalog.Item;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyBinding;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValueBinding;
import claro.catalog.model.PropertyData;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyValue;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


public class ItemDetails extends Composite {
	private static int NAME_COLUMN = 0;
	private static int TYPE_COLUMN = 1;
	private static int VALUE_COLUMN = 2;
	private static int LANG_COLUMNS = 3;
	private static int NR_FIXED_COLS = 3;

	private Image productImage;
	
	ExtendedItemBinding<Item> itemBinding = new ExtendedItemBinding<Item>() {
		// TODO
	};
	
	
	ListPropertyBinding<PropertyData> properties = new ListPropertyBinding<PropertyData>() {
		protected List<PropertyData> doGetData() {
			return null; // TODO
		}

		protected void doSetData(List<PropertyData> data) {
			// TODO
		}		
	};
	
	public ItemDetails() {
		initWidget(new FlowPanel() {{
//			add(new Trail());

			// Title
			add(new TextBox() {{
				Util.add(this, Styles.itemName);
				HasTextBinding.bind(this, itemBinding.name());
			}});
			
			// Image, Price, RelatedInfo
			add(new HorizontalPanel(){{
			    add(productImage = new Image() {{
			    	setSize("150px", "150px");
			    }});
			    add(new Label() {{
			    	Util.add(this, Styles.productprice);
			    	setCellVerticalAlignment(this, HorizontalPanel.ALIGN_MIDDLE);
			    	
			    	HasTextBinding.bind(this, itemBinding.price());
			    }});
			    add(new FlowPanel() {{
			    	add(new Anchor(Util.i18n.containedProducts(0)));
			    }});
			}});
			
			// Properties
			add(new Label(Util.i18n.properties()));
			add(new Grid(){{
				new WidgetListBinding<PropertyData, PropertyData>(properties, null) {

					@Override
					protected List<PropertyData> getWidgetData() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					protected void setWidgetData(List<PropertyData> properties) {
						clear();
						
						Set<String> languages = collectLanguages(properties);
						resizeColumns(NR_FIXED_COLS + languages.size() * 2);

						for (PropertyData property : properties) {
							claro.jpa.catalog.Property p;
							
							addValueRow(p, property.values, property.derivedValues, languages);
							
							for (OutputChannel channel : collectChannels(property)) {
								addValueRow(p, property.outputChannelSpecificValues.get(channel), property.derivedOutputChannelValues.get(channel), languages);
							}

							for (Map<String, claro.jpa.catalog.PropertyValue> alternateValues : property.alternateValues.values()) {
								addValueRow(p, alternateValues, Collections.<String, claro.jpa.catalog.PropertyValue>emptyMap(), languages);
							}
							
							for (Map<String, claro.jpa.catalog.PropertyValue> importSourceValues : property.importSourceValues.values()) {
								addValueRow(p, importSourceValues, Collections.<String, claro.jpa.catalog.PropertyValue>emptyMap(), languages);
							}
						}
					}

					private void addValueRow(claro.jpa.catalog.Property property, Map<String, PropertyValue> values, Map<String, PropertyValue> derivedValues, Set<String> languages) {
						
						// Find a value:
						PropertyValue value = values.get(null);
						boolean isDerived = false;
						if (value != null) {
							value = derivedValues.get(null);
							isDerived = true;
						}

						// Value not found, so no row
						if (value == null) {
							return; 
						}

						// Add a new row
						int row = getRowCount();
						resizeRows(row + 1);
						
						boolean isInherited = !itemBinding.getData().getId().equals(property.getItem().getId());

						PropertyBinding<agilexs.catalogxsadmin.presentation.client.catalog.Property> propertyBinding = new PropertyBinding<agilexs.catalogxsadmin.presentation.client.catalog.Property>();
//						propertyBinding.setData(property);
						setWidget(row, NAME_COLUMN, createNameWidget(propertyBinding, null, isInherited));
						setWidget(row, TYPE_COLUMN, createTypeWidget(property, isInherited));
						setWidget(row, VALUE_COLUMN, createValueWidget(value, isDerived));
						
						// Make a name value pair per language.
						int i = 0;
						for (String language : languages) {
							value = values.get(language);
							isDerived = false;
							if (value != null) {
								value = derivedValues.get(language);
								isDerived = true;
							}
							setWidget(row, LANG_COLUMNS + i, createNameWidget(property, language, isInherited));
							setWidget(row, LANG_COLUMNS + i + 1, createValueWidget(value, isDerived));
							i++;
						}
					}
					
					private Widget createNameWidget(final PropertyBinding<agilexs.catalogxsadmin.presentation.client.catalog.Property> propertyBinding, String language, boolean isReadonly) {
						if (isReadonly) {
							return new Label();
						} else {
							return new TextBox() {{
								HasTextBinding.bind(this, propertyBinding.labels());
							}};
						}
					}

					private Widget createTypeWidget(Property property, boolean isReadonly) {
						// TODO Auto-generated method stub
						return null;
					}
					
					private Set<OutputChannel> collectChannels(PropertyData propData) {
						Set<OutputChannel> result = new LinkedHashSet<OutputChannel>();
						
						result.addAll(propData.outputChannelSpecificValues.keySet());
						result.addAll(propData.derivedOutputChannelValues.keySet());
						
						return result;
					}
					private Set<String> collectLanguages(List<PropertyData> properties) {
						Set<String> result = new LinkedHashSet<String>();
						
						for (PropertyData propData : properties) {
							result.addAll(propData.values.keySet());
							result.addAll(propData.derivedValues.keySet());
							
							for (Map<String, PropertyValue> values : propData.outputChannelSpecificValues.values()) {
								result.addAll(values.keySet());
							}
							for (Map<String, PropertyValue> values : propData.derivedOutputChannelValues.values()) {
								result.addAll(values.keySet());
							}
							for (Map<String, PropertyValue> values : propData.alternateValues.values()) {
								result.addAll(values.keySet());
							}
							for (Map<String, PropertyValue> values : propData.importSourceValues.values()) {
								result.addAll(values.keySet());
							}
						}
						
						return result;
					}


					
					public WidgetListBinding bind() {
						super.bind();
						return this;
					}
					
				}.bind();
			}});
			
		}});
	}
	
	
}

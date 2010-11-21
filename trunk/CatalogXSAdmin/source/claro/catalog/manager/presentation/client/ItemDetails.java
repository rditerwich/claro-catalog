package claro.catalog.manager.presentation.client;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import claro.catalog.PropertyData;
import claro.catalog.manager.presentation.client.binding.ExtendedItemBinding;
import claro.catalog.manager.presentation.client.binding.HasTextBinding;
import claro.catalog.manager.presentation.client.binding.ListPropertyBinding;
import claro.catalog.manager.presentation.client.binding.WidgetListBinding;
import claro.catalog.manager.presentation.client.catalog.Item;
import claro.catalog.manager.presentation.client.catalog.PropertyBinding;
import claro.catalog.util.SMap;
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
							claro.jpa.catalog.Property p = null;
							
							addValueRow(p, property.values, property.derivedValues, languages);
							
							for (OutputChannel channel : collectChannels(property)) {
								addValueRow(p, property.outputChannelSpecificValues.getValue(channel), property.derivedOutputChannelValues.getValue(channel), languages);
							}

							for (SMap<String, claro.jpa.catalog.PropertyValue> alternateValues : property.alternateValues.getValues()) {
								addValueRow(p, alternateValues, SMap.<String, PropertyValue>empty(), languages);
							}
							
							for (SMap<String, claro.jpa.catalog.PropertyValue> importSourceValues : property.importSourceValues.getValues()) {
								addValueRow(p, importSourceValues, SMap.<String, PropertyValue>empty(), languages);
							}
						}
					}

					private void addValueRow(claro.jpa.catalog.Property property, SMap<String, PropertyValue> values, SMap<String, PropertyValue> derivedValues, Set<String> languages) {
						
						// Find a value:
						PropertyValue value = values.getValue(null);
						boolean isDerived = false;
						if (value != null) {
							value = derivedValues.getValue(null);
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

						PropertyBinding<claro.catalog.manager.presentation.client.catalog.Property> propertyBinding = new PropertyBinding<claro.catalog.manager.presentation.client.catalog.Property>();
//						propertyBinding.setData(property);
						setWidget(row, NAME_COLUMN, createNameWidget(propertyBinding, null, isInherited));
						setWidget(row, TYPE_COLUMN, createTypeWidget(property, isInherited));
//						setWidget(row, VALUE_COLUMN, createValueWidget(value, isDerived));
						
						// Make a name value pair per language.
						int i = 0;
						for (String language : languages) {
							value = values.getValue(language);
							isDerived = false;
							if (value != null) {
								value = derivedValues.getValue(language);
								isDerived = true;
							}
//							setWidget(row, LANG_COLUMNS + i, createNameWidget(property, language, isInherited));
//							setWidget(row, LANG_COLUMNS + i + 1, createValueWidget(value, isDerived));
							i++;
						}
					}
					
					private Widget createNameWidget(final PropertyBinding<claro.catalog.manager.presentation.client.catalog.Property> propertyBinding, String language, boolean isReadonly) {
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
						
						result.addAll(propData.outputChannelSpecificValues.getKeys());
						result.addAll(propData.derivedOutputChannelValues.getKeys());
						
						return result;
					}
					private Set<String> collectLanguages(List<PropertyData> properties) {
						Set<String> result = new LinkedHashSet<String>();
						
						for (PropertyData propData : properties) {
							result.addAll(propData.values.getKeys());
							result.addAll(propData.derivedValues.getKeys());
							
							for (SMap<String, PropertyValue> values : propData.outputChannelSpecificValues.getValues()) {
								result.addAll(values.getKeys());
							}
							for (SMap<String, PropertyValue> values : propData.derivedOutputChannelValues.getValues()) {
								result.addAll(values.getKeys());
							}
							for (SMap<String, PropertyValue> values : propData.alternateValues.getValues()) {
								result.addAll(values.getKeys());
							}
							for (SMap<String, PropertyValue> values : propData.importSourceValues.getValues()) {
								result.addAll(values.getKeys());
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

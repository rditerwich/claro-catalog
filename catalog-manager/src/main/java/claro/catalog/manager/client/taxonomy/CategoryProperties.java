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
import claro.catalog.manager.client.widgets.ConfirmationDialog;
import claro.jpa.catalog.PropertyType;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
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
	private static int ISMANY_COLUMN = 2;
	private static int GROUP_COLUMN = 3;
	private static int CLEAR_COLUMN = 4;
	private static int NR_COLS = 5;
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
	private final TaxonomyModel model;
	
	public CategoryProperties(TaxonomyModel model) {
		
		this.model = model;
		initWidget(new ScrollPanel() {{
		  add(new FlowPanel() {{add(new VerticalPanel() {{
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
		}});
		}});
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
	 */
	protected abstract void propertyToSet(Long itemId, PropertyInfo propertyInfo);
	
	protected abstract void propertyGroupToSet(Long itemId, PropertyInfo propertyInfo, PropertyGroupInfo group);

	protected abstract void propertyToRemove(Long itemId, PropertyInfo propertyInfo);
	
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
			final PropertyValueWidgets pvwidgets = new PropertyValueWidgets();
			final int row = j;
			// name
			propertyPanel.setWidget(j, NAME_COLUMN, pvwidgets.nameWidget = new TextBox() {{
				addChangeHandler(new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						propetyNameChanged(row, getText());
					}
				});
			}});
			
			// type
			propertyPanel.setWidget(j, TYPE_COLUMN, new VerticalPanel() {{
			  add(pvwidgets.typeWidget = new ListBox() {{
  				for (PropertyType type : propertyTypes) {
  					addItem(type.name(), type.name());// TODO How about i18n??
  				}
  				addChangeHandler(new ChangeHandler() {
  					public void onChange(ChangeEvent event) {
  						propertyTypeChanged(row, PropertyType.valueOf(getValue(getSelectedIndex())));
  					}
  				});
  			}}); 
			  add(pvwidgets.typeExtraWidget = new FlowPanel());
			  pvwidgets.typeExtraWidget.add(new FlowPanel()); // dummy, there should always be 1 child
			}}); 

			// ismany
			propertyPanel.setWidget(j, ISMANY_COLUMN, pvwidgets.propertyIsManyWidget = new ListBox() {{
				addItem(messages.propertyIsSingle());
				addItem(messages.propertyIsMany());
				addChangeHandler(new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						propertyIsManyChanged(row, getSelectedIndex() == 1);
					}
				});
			}});
			
			// groups
			propertyPanel.setWidget(j, GROUP_COLUMN, pvwidgets.propertyGroupsWidget = new ListBox() {{
				addChangeHandler(new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						propertyGroupChanged(row, getValue(getSelectedIndex()));
					}
				});
			}});
			
			// Clear button
			propertyPanel.setWidget(j, CLEAR_COLUMN, pvwidgets.clearValueWidget = new Image() {{
				StyleUtil.addStyle(this, Styles.clear);
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
						  new ConfirmationDialog(images.removeIcon()) {
						    protected String getMessage() {
						      return "Delete property?";
						    };
						    protected void yesPressed() {
						      propertyErased(row);
						    }
						  }.show();
						}
					});
				}
			}); 

			valueWidgets.add(pvwidgets);
		}

		// (re)bind widgets
		int i = 0;
		propertyByValueWidget.clear();
		for (PropertyGroupInfo propertyGroup : propertyGroups) {
			
			SMap<PropertyInfo, PropertyData> properties = values.get(propertyGroup);
			List<PropertyInfo> propertyKeys = properties.getKeys();
			
			for (PropertyInfo property : propertyKeys) {
			  final int row = i;
				final PropertyValueWidgets pvwidgets = valueWidgets.get(i);
				
				// Set name
				String propertyName = property.labels.tryGet(model.getSelectedLanguage(), null);
				pvwidgets.nameWidget.setText(propertyName);
				
				// set type
				pvwidgets.typeWidget.setSelectedIndex(Arrays.binarySearch(propertyTypes, property.getType(), typeComparator));
				pvwidgets.typeExtraWidget.setVisible(false);
				
				// enum editor
				if (property.getType() == PropertyType.Enum) {
				  if (!(pvwidgets.typeExtraWidget.getWidget(0) instanceof EnumEditor)) {
  				  pvwidgets.typeExtraWidget.clear();
  				  pvwidgets.typeExtraWidget.add(new EnumEditor() {
  				    void changed(PropertyInfo property) {
  				      propertyToSet(itemId, property);
  				    }
  				  });
				  }
          ((EnumEditor) pvwidgets.typeExtraWidget.getWidget(0)).render(model, getRowProperty(row));
				  pvwidgets.typeExtraWidget.setVisible(true);
				}
				
				// set ismany
				pvwidgets.propertyIsManyWidget.setSelectedIndex(property.isMany?1:0); 
				
				// Groups
				pvwidgets.propertyGroupsWidget.clear();
				int j = 0;
				for (Entry<Long, SMap<String, String>> group : groups) {
					// add item
					String groupName = group.getValue().tryGet(model.getSelectedLanguage(), null);
					pvwidgets.propertyGroupsWidget.addItem(groupName);
					
					// check selection
					if (group.getKey().equals(propertyGroup.propertyGroupId)) {
						pvwidgets.propertyGroupsWidget.setSelectedIndex(j);
					}
					j++;
				}
				
				i++;
			}
		}
	}
	
	private void createNewProperty() {
		// TODO Make a more clean implementation
		PropertyGroupInfo group = values.getKey(0);
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
		propertyToSet(itemId, rowProperty);
	}
	private void propertyTypeChanged(int row, PropertyType type) {
		PropertyInfo rowProperty = getRowProperty(row);
		rowProperty.setType(type);
		propertyToSet(itemId, rowProperty);
	}
	
	private void propertyIsManyChanged(int row, Boolean value) {
		PropertyInfo rowProperty = getRowProperty(row);
		rowProperty.isMany = value;
		propertyToSet(itemId, rowProperty);
	}
	private void propertyGroupChanged(int row, String group) {
		PropertyInfo rowProperty = getRowProperty(row);
		PropertyGroupInfo groupInfo = values.getKeys().get(row);
		
		propertyGroupToSet(itemId, rowProperty, groupInfo);
	}
	
	private void propertyErased(int row) {
		PropertyInfo rowProperty = getRowProperty(row);
		propertyToRemove(itemId, rowProperty);
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
    public ListBox propertyIsManyWidget;
		protected Widget clearValueWidget;
		public TextBox nameWidget;
		public ListBox typeWidget;
		public FlowPanel typeExtraWidget;
		public ListBox propertyGroupsWidget;
	}
}

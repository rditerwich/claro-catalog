package claro.catalog.manager.client.taxonomy;

import static claro.catalog.manager.client.CatalogManager.propertyStringConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import claro.catalog.data.MediaValue;
import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.items.ItemPageModel;
import claro.catalog.manager.client.widgets.ItemSelectionWidget;
import claro.catalog.manager.client.widgets.MediaWidget;
import claro.catalog.util.CatalogModelUtil;
import claro.jpa.catalog.OutputChannel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.gwt.client.widgets.MoneyWidget;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.Money;
import easyenterprise.lib.util.SMap;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.SingleUploader;

abstract public class ItemPropertyValues extends Composite implements Globals {
	public enum Styles implements Style { clear, valueParent, valueWidget, itemPropertyValues, overridden, source, itemPropertyValueRow, groupInherited, itemPropertyValueName, valueParentTD, mediaValue }
	private static int NAME_COLUMN = 0;
	private static int VALUE_COLUMN = 1;
	private static int GROUP_COLUMN = 2;
//	private static int SOURCE_COLUMN = 3;
	private static int NR_FIXED_COLS = 2;

	private final ItemPageModel model;
	private final boolean showPropertySources;
	private final boolean canReassignGroups;
	private final int nrCols;

	private TabPanel propertyGroupPanel;
	private List<GroupPanelWidgets> groupPanels = new ArrayList<GroupPanelWidgets>();
	private Map<Widget, PropertyInfo> propertyByValueWidget = new HashMap<Widget, PropertyInfo>();
	
	private SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> values;
	private Long itemId;
	private SMap<Long, SMap<String, String>> groups;
	private SMap<Long, SMap<String, String>> parentExtentWithSelf;
	
	public ItemPropertyValues(final ItemPageModel model, final boolean canReassignGroups, boolean showPropertySources) {
		this.model = model;
		this.canReassignGroups = canReassignGroups;
		this.showPropertySources = showPropertySources;

		nrCols = NR_FIXED_COLS + ((canReassignGroups?1:0) + (showPropertySources?1:0));
		
		propertyGroupPanel = new TabPanel();
		StyleUtil.addStyle(propertyGroupPanel, Styles.itemPropertyValues);
		initWidget(new ScrollPanel() {{ add(propertyGroupPanel); }});
	}
	
	/**
	 * set the item data and (re)render.
	 * 
	 * @param itemId
	 * @param groups only necessary when groups can be reassigned.
	 * @param parentExtentWithSelf only necessary if sources are shown
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
	 * @param value
	 */
	protected abstract void propertyValueSet(Long itemId, PropertyInfo propertyInfo, Object value);

	protected abstract void propertyValueErased(Long itemId, PropertyInfo propertyInfo);
	
	private void render() {
		List<PropertyGroupInfo> propertyGroups = values.getKeys();
		// Remove extraneous property groups.
		int oldPanelCount = propertyGroupPanel.getWidgetCount();
		for (int i = oldPanelCount - 1; i >= propertyGroups.size(); i--) {
			groupPanels.remove(i);
			propertyGroupPanel.remove(i);
		}
		
		// Create new panels
		for (int i = oldPanelCount; i < propertyGroups.size(); i++) {
			GroupPanelWidgets propertyGroupWidgets = new GroupPanelWidgets();
			
			propertyGroupPanel.add(propertyGroupWidgets.panel = new Grid(0, nrCols), "");
			
			groupPanels.add(propertyGroupWidgets);
			
			// If there was no panel, select a default one now.
			if (oldPanelCount == 0) {
				propertyGroupPanel.selectTab(0);
			}
			
		}
		
		// Rebind property groups
		int i = 0;
		propertyByValueWidget.clear();
		for (PropertyGroupInfo propertyGroup : propertyGroups) {
			
			// TabPanel tab text:
			propertyGroupPanel.getTabBar().setTabText(i, propertyGroup.labels.tryGet(CatalogManager.getUiLanguage(), null));

			// TabPanel tab content:
			
			GroupPanelWidgets groupPanelWidgets = groupPanels.get(i);
			SMap<PropertyInfo, PropertyData> properties = values.get(propertyGroup);
			List<PropertyInfo> propertyKeys = properties.getKeys();
			
			// Remove redundant properties
			int oldRowCount = groupPanelWidgets.panel.getRowCount();
			groupPanelWidgets.panel.resizeRows(propertyKeys.size());  
			for (int j = oldRowCount - 1; j >= propertyKeys.size(); j--) {
				groupPanelWidgets.valueWidgets.remove(j);
			}
			
			// Create new rows
			for (int j = oldRowCount; j < propertyKeys.size(); j++) {
				final PropertyValueWidgets propertyValueWidgets = new PropertyValueWidgets();

				// Style
				StyleUtil.add(groupPanelWidgets.panel.getRowFormatter(), j, Styles.itemPropertyValueRow);
				
				// name and source
				groupPanelWidgets.panel.setWidget(j, NAME_COLUMN, new Grid(1, 2) {{
					StyleUtil.addStyle(this, Styles.itemPropertyValueName);
					// name
					setWidget(0, 0, propertyValueWidgets.nameWidget = new Label());

					// property source
					if (showPropertySources) {
						setWidget(0, 1, propertyValueWidgets.propertySourceWidget = new Label() {{
							StyleUtil.addStyle(this, Styles.source);
						}});
					}
					
				}});
				
				// Value + Clear button
				groupPanelWidgets.panel.setWidget(j, VALUE_COLUMN, propertyValueWidgets.valueParentWidget = new Grid(1, 2) {{
					StyleUtil.addStyle(this, Styles.valueParent);
					// Real value is added in the bind fase...
					setWidget(0, 1, propertyValueWidgets.clearValueWidget = new Image() {{
						StyleUtil.addStyle(this, Styles.clear);
							addClickHandler(new ClickHandler() {
								public void onClick(ClickEvent event) {
									clearValue((Widget) event.getSource());
								}
							});
						}
					}); 
				}});
				StyleUtil.addClass(propertyValueWidgets.valueParentWidget.getElement().getParentElement(), Styles.valueParentTD);
				
				// groups
				if (canReassignGroups) {
					groupPanelWidgets.panel.setWidget(j, GROUP_COLUMN, propertyValueWidgets.propertyGroupsWidget = new Label());
				}
				
				
				
				groupPanelWidgets.valueWidgets.add(propertyValueWidgets);
			}
			
			// (re)bind widgets
			int j = 0;
			for (PropertyInfo property : propertyKeys) {
				PropertyValueWidgets propertyValueWidgets = groupPanelWidgets.valueWidgets.get(j);
				PropertyData propertyData = properties.get(property);
				
				// Set name
				String propertyName = property.labels.tryGet(model.getSelectedLanguage(), null);
				propertyValueWidgets.nameWidget.setText(propertyName);
				
				// ensure value widget
				Widget valueWidget = setValueWidget(propertyValueWidgets, j, VALUE_COLUMN, property, propertyData);
				
				// Groups
//				propertyValueWidgets.propertyGroupsWidget.clear();
//				int k = 0;
//				for (Entry<Long, SMap<String, String>> group : groups) {
//					// add item
//					String groupName = group.getValue().tryGet(language, null);
//					propertyValueWidgets.propertyGroupsWidget.addItem(groupName);
//					
//					// check selection
//					if (group.getKey().equals(propertyGroup.propertyGroupId)) {
//						propertyValueWidgets.propertyGroupsWidget.setSelectedIndex(k);
//					}
//					k++;
//				}
				
				if (canReassignGroups) {
					String groupName = CollectionUtil.notNull(groups).getOrEmpty(propertyGroup.propertyGroupId).tryGet(model.getSelectedLanguage(), null);
					propertyValueWidgets.propertyGroupsWidget.setText(groupName);
					if (itemId != null && itemId != propertyData.groupAssignmentItemId) {
						StyleUtil.addStyle(propertyValueWidgets.propertyGroupsWidget, Styles.groupInherited);
					} else {
						StyleUtil.remove(propertyValueWidgets.propertyGroupsWidget, Styles.groupInherited);
					}
				}
				
				// property source
				if (showPropertySources) {
					String sourceName = CollectionUtil.notNull(parentExtentWithSelf).getOrEmpty(property.ownerItemId).tryGet(model.getSelectedLanguage(), null);
					if (sourceName != null) {
						propertyValueWidgets.propertySourceWidget.setText("(" + sourceName + ")");
						propertyValueWidgets.propertySourceWidget.setTitle(messages.propertySourceTooltip(propertyName, sourceName));
					}
					else {
						propertyValueWidgets.propertySourceWidget.setText("(unknown)");
						propertyValueWidgets.propertySourceWidget.setTitle(messages.unknownPropertySourceTooltip(propertyName));
						
					}
				}
				
				// Remember binding
				if (valueWidget != null) {
					propertyByValueWidget.put(valueWidget, property);
					propertyByValueWidget.put(propertyValueWidgets.clearValueWidget, property);
				}
				
				j++;
			}
			
			i++;
		}
	}
	
	private Widget setValueWidget(PropertyValueWidgets propertyValueWidgets, int row, int col, PropertyInfo property, PropertyData propertyData) {
		
		// ensure widget:
		Widget widget = ensureWidget(propertyValueWidgets, property);

		// Set actual value
		// TODO what about overriding values to null?  Use undefined in the code below?
		// TODO many multiplicity properties.
		
		SMap<String, Object> values =  CollectionUtil.notNull(propertyData.values).getOrEmpty(model.getSelectedShop()); // TODO why is there no staging area here?. and: No fallback to null outputchannel?
		Object value;
		boolean isInherited = false;
		if (property.isMany) {
			value = values.tryGetAll(model.getSelectedLanguage(), null);
		} else {
			value = values.get(model.getSelectedLanguage());
			if (value == null) {
				isInherited = true;
				value = values.get();
			}
		}

		SMap<OutputChannel, SMap<String, Object>> effectiveValues = CollectionUtil.notNull(propertyData.effectiveValues).getOrEmpty(null); // Use the default staging area.
		SMap<String, Object> effectiveLanguageValues = CollectionUtil.notNull(effectiveValues.tryGet(model.getSelectedShop(), null));
		Object inheritedValue;
		if (property.isMany) {
			inheritedValue = effectiveLanguageValues.tryGetAll(model.getSelectedLanguage(), null);
		} else {
			inheritedValue = effectiveLanguageValues.tryGet(model.getSelectedLanguage(), null);
		}

		// TODO The many-case for inherited is *not* right.  
		if (value == null || property.isMany && ((Collection<?>)value).isEmpty()) {
			value = inheritedValue;
			if (value != null) {
				isInherited = true; 
			}
		}
		
		setValue(widget, value, property);
		
		if (isInherited) {
			StyleUtil.addStyle(propertyValueWidgets.valueParentWidget, CatalogManager.Styles.inherited);
//			StyleUtil.remove(propertyValueWidgets.valueParentWidget, Styles.overridden);
		} else {
			StyleUtil.remove(propertyValueWidgets.valueParentWidget, CatalogManager.Styles.inherited);
			if (value != null && inheritedValue != null) {
				// Value is overridden
//				StyleUtil.add(propertyValueWidgets.valueParentWidget, Styles.overridden);
			}
		}
		
		return widget;
	}

	// TODO: Enums, Formatted Text.
	private Widget ensureWidget(PropertyValueWidgets propertyValueWidgets, final PropertyInfo property) {
		Widget oldWidget = propertyValueWidgets.valueParentWidget.getWidget(0, 0);
		Widget result = null;
		
		switch (property.getType()) {
		case Boolean:
			if (oldWidget instanceof CheckBox) {
				result = oldWidget;
			} else {
				result = new CheckBox() {
					final CheckBox me = this;
					{
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							valueChanged(me, getValue());
						}
					});
				}};
			}
			break;
		case Item:
		case Category:
		case Product:
			if (oldWidget instanceof ItemSelectionWidget) {
				ItemSelectionWidget itemWidget = (ItemSelectionWidget) oldWidget;
				itemWidget.setItemType(CatalogModelUtil.convert(property.getType())); 
				itemWidget.setMultiSelect(property.isMany);
				
				result = oldWidget;
			} else {
				result = new ItemSelectionWidget(false, CatalogModelUtil.convert(property.getType()), property.isMany) {// TODO itemtype should be set depending on the property.
					protected void selectionChanged() {
						List<Long> selection = getSelection();
						if (isMultiSelect()) {
							valueChanged(this, selection);
						} else {
							valueChanged(this, selection.isEmpty() ? null : selection.get(0));
						}
					}
				}; 
			}
			break;
		case Media:
			if (oldWidget instanceof HorizontalPanel && ((HorizontalPanel)oldWidget).getWidgetCount() > 1 && ((HorizontalPanel)oldWidget).getWidget(0) instanceof MediaWidget) {
				result = oldWidget;
			} else {
				result = new HorizontalPanel() {
					final HorizontalPanel me = this;
					{
					setVerticalAlignment(ALIGN_MIDDLE);
					add(new MediaWidget(false, true));
					add(new SingleUploader() {{
						setAutoSubmit(true);
						addOnFinishUploadHandler(new OnFinishUploaderHandler() {
							public void onFinish(IUploader uploader) {
								if (uploader.getStatus() == Status.SUCCESS) {
									valueChanged(me, uploader.getServerInfo().field); 
								}
							}
						});
					}});
					
				}};
			}
			break;
		case Money:
			if (oldWidget instanceof MoneyWidget) {
				result = oldWidget;
			} else {
				result = new MoneyWidget() {
					protected void valueChanged(Money newValue) {
						ItemPropertyValues.this.valueChanged(this, newValue);
					}
				};
			}
			break;
		case Enum:
		  if (oldWidget instanceof ListBox) {
		    result = oldWidget;
		  } else {
		    result = new ListBox() {{
		      final ListBox thisWidget = this;
		      addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
              valueChanged(thisWidget, Integer.parseInt(getValue(getSelectedIndex())));
            }
          });
		    }};
		  }
		  break;
		default:
			if (oldWidget instanceof TextBox) {
				result = oldWidget;
			} else {
				result = new TextBox() {{
          final TextBox thisWidget = this;
					addChangeHandler(new ChangeHandler() {
						public void onChange(ChangeEvent event) {
							valueChanged(thisWidget, getText());
						}
					});
				}};
			}
		}
		
		if (result != oldWidget) {
			propertyValueWidgets.valueParentWidget.setWidget(0, 0, result);
		}
		
		StyleUtil.addStyle(result, Styles.valueWidget);
		
		return result;
	}

	private void setValue(Widget widget, Object value, PropertyInfo property) {
		switch (property.getType()) {
		case Boolean:
			CheckBox checkBox = (CheckBox) widget;
			checkBox.setValue((Boolean) value);
			break;
		case Item:
		case Category:
		case Product:
			ItemSelectionWidget selectionWidget = (ItemSelectionWidget)widget;
			if (value != null) {
				selectionWidget.setData((Collection<Long>) (property.isMany ? value : Collections.singleton(value)), model.getSelectedLanguage());  // TODO Get the proper value. (List<Long>)
			} else {
				selectionWidget.setData(Collections.<Long>emptyList(), model.getSelectedLanguage());
			}
			break;
		case Media:
		  StyleUtil.setStyle(widget, Styles.mediaValue);
			MediaWidget mediaWidget = (MediaWidget) ((HorizontalPanel)widget).getWidget(0);
			if (value instanceof MediaValue) {
				MediaValue mediaValue = (MediaValue) value;
//				mediaWidget.setUploadData(itemId, property.propertyId, mediaValue.propertyValueId, language);
				mediaWidget.setData(mediaValue.mediaContentId, mediaValue.mimeType, mediaValue.name);
			} else {
//				mediaWidget.setUploadData(itemId, property.propertyId, null, language);
				mediaWidget.setData(null, null, null);
			}
			break;
		case Money:
			MoneyWidget moneyWidget = (MoneyWidget) widget;
			moneyWidget.setValue((Money) value);
			break;
		case Enum:
		  ListBox listbox = (ListBox) widget;
		  try {
        listbox.clear();
        for (Entry<Integer, SMap<String, String>> entry : property.enumValues) {
          System.out.println("EnumValues  " + entry.getKey() + ": " + entry.getValue());
          listbox.addItem(entry.getValue().tryGet(model.getSelectedLanguage(), null));
          if (entry.getKey().equals(value)) {
            listbox.setSelectedIndex(listbox.getItemCount() - 1);
          }
        }
		  }
		  catch (Throwable t) {
		    listbox.setSelectedIndex(-1);
		  }
		  break;
		default:
			TextBox textBox = (TextBox) widget;
			textBox.setText(value != null? propertyStringConverter.toString(property.getType(), value) : null);
		}
	}

	private void valueChanged(Widget widget, Object newValue) {
		propertyValueSet(itemId, propertyByValueWidget.get(widget), newValue);
	}
	
	private void clearValue(Widget eraseButton) {
		propertyValueErased(itemId, propertyByValueWidget.get(eraseButton));
	}
	
	private static class GroupPanelWidgets {
		public Grid panel;
		public List<PropertyValueWidgets> valueWidgets = new ArrayList<PropertyValueWidgets>();
	}
	
	private static class PropertyValueWidgets {
		protected Image clearValueWidget;
		public Label nameWidget;
		public Grid valueParentWidget;
		public Label propertyGroupsWidget;
		public Label propertySourceWidget;
	}
}

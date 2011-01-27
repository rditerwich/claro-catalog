package claro.catalog.manager.client.taxonomy;

import static claro.catalog.manager.client.CatalogManager.propertyStringConverter;

import java.util.ArrayList;
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
import claro.catalog.manager.client.widgets.MediaWidget;
import claro.jpa.catalog.OutputChannel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
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

abstract public class CategoryProperties extends Composite implements Globals {
	public enum Styles implements Style { clear, valueParent, valueWidget, categoryProperties }
	private static int NAME_COLUMN = 0;
	private static int TYPE_COLUMN = 1;
	private static int VALUE_COLUMN = 2;
	private static int GROUP_COLUMN = 3;
	private static int SOURCE_COLUMN = 4;
	private static int NR_FIXED_COLS = 5;

	private TabPanel propertyGroupPanel;
	private List<GroupPanelWidgets> groupPanels = new ArrayList<GroupPanelWidgets>();
	private Map<Widget, PropertyInfo> propertyByValueWidget = new HashMap<Widget, PropertyInfo>();
	
	
	
	private SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> values;
	private Long itemId;
	private String language;
	private OutputChannel outputChannel;
	private SMap<Long, SMap<String, String>> groups;
	private SMap<Long, SMap<String, String>> parentExtent;
	
	public CategoryProperties(String language, OutputChannel outputChannel) {
		this.language = language;
		this.outputChannel = outputChannel;
		
		propertyGroupPanel = new TabPanel();
		initWidget(new VerticalPanel() {{
			StyleUtil.add(this, Styles.categoryProperties);
			add(new Anchor("Define new property...")); // TODO i18n.
			add(propertyGroupPanel);
		}});
	}
	
	public void setLanguage(String language) {
		this.language = language;
		
		render();
	}
	
	public void setOutputChannel(OutputChannel outputChannel) {
		this.outputChannel = outputChannel;
		
		render();
	}
	
	/**
	 * set the item data and (re)render.
	 * 
	 * @param itemId
	 * @param values
	 */
	public void setItemData(Long itemId, SMap<Long, SMap<String, String>> groups, SMap<Long, SMap<String, String>> parentExtent, SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> values) {
		this.itemId = itemId;
		this.groups = groups;
		this.parentExtent = parentExtent;
		this.values = values;
		render();
	}
	
	/**
	 * Called when the value of a property is set.
	 * @param itemId
	 * @param propertyInfo
	 * @param language
	 * @param value
	 */
	protected abstract void propertyValueSet(Long itemId, PropertyInfo propertyInfo, String language, Object value);

	protected abstract void propertyValueErased(Long itemId, PropertyInfo propertyInfo, String language);
	
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
			
			propertyGroupPanel.add(propertyGroupWidgets.panel = new Grid(0, NR_FIXED_COLS), "");
			
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
				
				// name
				groupPanelWidgets.panel.setWidget(j, NAME_COLUMN, propertyValueWidgets.nameWidget = new Label());
				
				// type
				groupPanelWidgets.panel.setWidget(j, TYPE_COLUMN, propertyValueWidgets.typeWidget = new Label());

				// Value + Clear button
				groupPanelWidgets.panel.setWidget(j, VALUE_COLUMN, propertyValueWidgets.valueParentWidget = new Grid(1, 2) {{
					StyleUtil.add(this, Styles.valueParent);
					// Real value is added in the bind fase...
					setWidget(0, 1, propertyValueWidgets.clearValueWidget = new Image() {{
						StyleUtil.add(this, Styles.clear);
							final Widget me = this;
							addClickHandler(new ClickHandler() {
								public void onClick(ClickEvent event) {
									clearValue(me);
								}
							});
						}
					}); 
				}});
				
				// groups
				groupPanelWidgets.panel.setWidget(j, GROUP_COLUMN, propertyValueWidgets.propertyGroupsWidget = new ListBox());
				
				groupPanelWidgets.valueWidgets.add(propertyValueWidgets);
			}
			
			// (re)bind widgets
			int j = 0;
			for (PropertyInfo property : propertyKeys) {
				PropertyValueWidgets propertyValueWidgets = groupPanelWidgets.valueWidgets.get(j);
				PropertyData propertyData = properties.get(property);
				
				// Set name
				String propertyName = property.labels.tryGet(CatalogManager.getUiLanguage(), null);
				propertyValueWidgets.nameWidget.setText(propertyName);
				
				// set type
				propertyValueWidgets.typeWidget.setText(property.type.name()); // TODO How about i18n??
				
				// ensure value widget
				Widget valueWidget = setValueWidget(propertyValueWidgets, j, VALUE_COLUMN, property, propertyData);
				
				// Groups
				propertyValueWidgets.propertyGroupsWidget.clear();
				int k = 0;
				for (Entry<Long, SMap<String, String>> group : groups) {
					// add item
					String groupName = group.getValue().tryGet(language, null);
					propertyValueWidgets.propertyGroupsWidget.addItem(groupName);
					
					// check selection
					if (group.getKey().equals(propertyGroup.propertyGroupId)) {
						propertyValueWidgets.propertyGroupsWidget.setSelectedIndex(k);
					}
					k++;
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
		
		SMap<String, Object> values =  CollectionUtil.notNull(propertyData.values).getOrEmpty(outputChannel); // TODO why is there no staging area here?. and: No fallback to null outputchannel?
		Object value = values.tryGet(language, null);
		boolean isDerived = false;

		if (value == null) {
			SMap<OutputChannel, SMap<String, Object>> effectiveValues = CollectionUtil.notNull(propertyData.effectiveValues).getOrEmpty(null); // Use the default staging area.
			SMap<String, Object> effectiveLanguageValues = CollectionUtil.notNull(effectiveValues.tryGet(outputChannel, null));
			
			value = effectiveLanguageValues.tryGet(language, null);
			isDerived = true; 
		}
		
		setValue(widget, value, property);
		
		if (isDerived) {
			StyleUtil.add(propertyValueWidgets.valueParentWidget, CatalogManager.Styles.derived);
		} else {
			// TODO Maybe changelistener to remove derived?
			StyleUtil.remove(propertyValueWidgets.valueParentWidget, CatalogManager.Styles.derived);
		}
		return widget;
	}

	// TODO: Enums, Formatted Text, Money.
	private Widget ensureWidget(PropertyValueWidgets propertyValueWidgets, final PropertyInfo property) {
		Widget oldWidget = propertyValueWidgets.valueParentWidget.getWidget(0, 0);
		Widget result = null;
		
		switch (property.type) {
		case Boolean:
			if (oldWidget instanceof CheckBox) {
				result = oldWidget;
			} else {
				result = new CheckBox() {{
					final CheckBox me = this;
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							valueChanged(me, getValue());
						}
					});
				}};
			}
			break;
		case Media:
			if (oldWidget instanceof HorizontalPanel && ((HorizontalPanel)oldWidget).getWidgetCount() > 1 && ((HorizontalPanel)oldWidget).getWidget(0) instanceof MediaWidget) {
				result = oldWidget;
			} else {
				result = new HorizontalPanel() {{
					final HorizontalPanel me = this;
					setVerticalAlignment(ALIGN_MIDDLE);
					add(new MediaWidget(false, true));
					add(new SingleUploader() {{
//						addHoverVisibility(getForm());
//						addHoverVisibility(this.getFileInput());
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
						CategoryProperties.this.valueChanged(this, newValue);
					}
				};
			}
			break;
		default:
			if (oldWidget instanceof TextBox) {
				result = oldWidget;
			} else {
				result = new TextBox() {{
					final TextBox me = this; 
					addChangeHandler(new ChangeHandler() {
						public void onChange(ChangeEvent event) {
							valueChanged(me, getText());
						}
					});
				}};
			}
		}
		
		if (result != oldWidget) {
			propertyValueWidgets.valueParentWidget.setWidget(0, 0, result);
		}
		
		StyleUtil.add(result, Styles.valueWidget);
		
		return result;
	}

	private void setValue(Widget widget, Object value, PropertyInfo property) {
		switch (property.type) {
		case Boolean:
			CheckBox checkBox = (CheckBox) widget;
			checkBox.setValue((Boolean) value);
			break;
		case Media:
			MediaWidget mediaWidget = (MediaWidget) ((HorizontalPanel)widget).getWidget(0);
			if (value instanceof MediaValue) {
				MediaValue mediaValue = (MediaValue) value;
//				mediaWidget.setUploadData(itemId, property.propertyId, mediaValue.propertyValueId, language);
				mediaWidget.setData(mediaValue.propertyValueId, mediaValue.mimeType, mediaValue.filename);
			} else {
//				mediaWidget.setUploadData(itemId, property.propertyId, null, language);
				mediaWidget.setData(null, null, null);
			}
			break;
		case Money:
			MoneyWidget moneyWidget = (MoneyWidget) widget;
			moneyWidget.setValue((Money) value);
			break;
		default:
			TextBox textBox = (TextBox) widget;
			textBox.setText(value != null? propertyStringConverter.toString(property.type, value) : null);
		}
	}

	private void valueChanged(Widget widget, Object newValue) {
		propertyValueSet(itemId, propertyByValueWidget.get(widget), language, newValue);
	}
	
	private void clearValue(Widget eraseButton) {
		propertyValueErased(itemId, propertyByValueWidget.get(eraseButton), language);
	}
	
	private class GroupPanelWidgets {
		public Grid panel;
		public List<PropertyValueWidgets> valueWidgets = new ArrayList<PropertyValueWidgets>();
	}
	
	private class PropertyValueWidgets {
		protected Widget clearValueWidget;
		public Label nameWidget;
		public Label typeWidget;
		public Grid valueParentWidget;
		public ListBox propertyGroupsWidget;
	}
}
package claro.catalog.manager.client.catalog;

import static claro.catalog.manager.client.CatalogManager.propertyStringConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.gwt.client.widgets.MoneyWidget;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.Money;
import easyenterprise.lib.util.SMap;
import gwtupload.client.IFileInput;
import gwtupload.client.IUploadStatus.Status;
import gwtupload.client.IUploader;
import gwtupload.client.SingleUploader;

@Deprecated
abstract public class ProductPropertyValues extends Composite implements Globals {
	public enum Styles implements Style { clear, valueParent, valueWidget }
	private static int NAME_COLUMN = 0;
	private static int TYPE_COLUMN = 1;
	private static int VALUE_COLUMN = 2;
	private static int NR_FIXED_COLS = 3;

	private TabPanel propertyGroupPanel;
	private List<GroupPanelWidgets> groupPanels = new ArrayList<GroupPanelWidgets>();
	private Map<Widget, PropertyInfo> propertyByValueWidget = new HashMap<Widget, PropertyInfo>();
	
	
	
	private SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> values;
	private Long itemId;
	private String language;
	private OutputChannel outputChannel;
	
	public ProductPropertyValues(String language, OutputChannel outputChannel) {
		this.language = language;
		this.outputChannel = outputChannel;
		
		propertyGroupPanel = new TabPanel();
		initWidget(propertyGroupPanel);
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
	public void setItemData(Long itemId, SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> values) {
		this.itemId = itemId;
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
					StyleUtil.addStyle(this, Styles.valueParent);
					// Real value is added in the bind fase...
					setWidget(0, 1, propertyValueWidgets.clearValueWidget = new Image() {{
						StyleUtil.addStyle(this, Styles.clear);
							final Widget me = this;
							addClickHandler(new ClickHandler() {
								public void onClick(ClickEvent event) {
									clearValue(me);
								}
							});
						}
					}); 
				}});
				
				
				groupPanelWidgets.valueWidgets.add(propertyValueWidgets);
			}
			
			// (re)bind widgets
			int j = 0;
			for (PropertyInfo property : propertyKeys) {
				PropertyValueWidgets propertyValueWidgets = groupPanelWidgets.valueWidgets.get(j);
				PropertyData propertyData = properties.get(property);
				
				// Set name
				propertyValueWidgets.nameWidget.setText(property.labels.tryGet(CatalogManager.getUiLanguage(), null));
				
				// set type
				propertyValueWidgets.typeWidget.setText(property.getType().name()); // TODO How about i18n??
				
				// ensure value widget
				Widget valueWidget = setValueWidget(propertyValueWidgets, j, VALUE_COLUMN, property, propertyData);
				
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
			StyleUtil.addStyle(propertyValueWidgets.valueParentWidget, CatalogManager.Styles.inherited);
		} else {
			// TODO Maybe changelistener to remove derived?
			StyleUtil.remove(propertyValueWidgets.valueParentWidget, CatalogManager.Styles.inherited);
		}
		return widget;
	}

	// TODO: Enums, Formatted Text, Money.
	private Widget ensureWidget(PropertyValueWidgets propertyValueWidgets, final PropertyInfo property) {
		Widget oldWidget = propertyValueWidgets.valueParentWidget.getWidget(0, 0);
		Widget result = null;
		
		switch (property.getType()) {
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
						setAutoSubmit(true);
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
						ProductPropertyValues.this.valueChanged(this, newValue);
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
		
		StyleUtil.addStyle(result, Styles.valueWidget);
		
		return result;
	}
	
	private void addHoverVisibility(final IFileInput widget) {
		if (widget instanceof HasMouseOverHandlers) {
			HasMouseOverHandlers hasMouseOver = (HasMouseOverHandlers) widget;
			
			hasMouseOver.addMouseOverHandler(new MouseOverHandler() {
				public void onMouseOver(MouseOverEvent event) {
					if (widget instanceof FlowPanel) {
						((FlowPanel)widget).getWidget(1).setVisible(true);
					} else {
						widget.getWidget().getParent().addStyleName("hoverStyle");
						widget.getWidget().getParent().removeStyleName("noHoverStyle");
//						widget.setVisible(true);
					}
				}
			});
		}
		if (widget instanceof HasMouseOutHandlers) {
			HasMouseOutHandlers hasMouseOut = (HasMouseOutHandlers) widget;
			
			hasMouseOut.addMouseOutHandler(new MouseOutHandler() {
				public void onMouseOut(MouseOutEvent event) {
					if (widget instanceof FlowPanel) {
						((FlowPanel)widget).getWidget(1).setVisible(false);
					} else {
						widget.getWidget().getParent().removeStyleName("hoverStyle");
						widget.getWidget().getParent().addStyleName("noHoverStyle");
//						widget.setVisible(false);
					}
				}
			});
		}
		
	}
	private void addHoverVisibility(final Widget widget) {
		widget.addHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				if (widget instanceof FlowPanel) {
					((FlowPanel)widget).getWidget(1).addStyleName("hoverStyle");
					((FlowPanel)widget).getWidget(1).removeStyleName("noHoverStyle");
				} else {
					((FormPanel)widget).getWidget().addStyleName("hoverStyle");
					((FormPanel)widget).getWidget().removeStyleName("noHoverStyle");
					widget.setVisible(true);
				}
			}
		}, MouseOverEvent.getType());
		
		widget.addHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				if (widget instanceof FlowPanel) {
					((FlowPanel)widget).getWidget(1).removeStyleName("hoverStyle");
					((FlowPanel)widget).getWidget(1).addStyleName("noHoverStyle"); 
				} else {
					((FormPanel)widget).getWidget().removeStyleName("hoverStyle");
					((FormPanel)widget).getWidget().addStyleName("noHoverStyle"); 
					widget.setVisible(false);
				}
			}
		}, MouseOutEvent.getType());
	}

	
	private void setValue(Widget widget, Object value, PropertyInfo property) {
		switch (property.getType()) {
		case Boolean:
			CheckBox checkBox = (CheckBox) widget;
			checkBox.setValue((Boolean) value);
			break;
		case Media:
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
		default:
			TextBox textBox = (TextBox) widget;
			textBox.setText(value != null? propertyStringConverter.toString(property.getType(), value) : null);
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
	}
}

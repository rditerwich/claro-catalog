package claro.catalog.manager.client;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.util.PropertyStringConverter;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyValue;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.util.SMap;


abstract public class ProductDetails extends Composite {
	
	private TextBox productNameBox;
	private FlowPanel categoryPanel;
	private Label productPrice;
	private Image productImage;
	
	private ItemPropertyValues propertyValues;
	private String uiLanguage;
	private String language;
	private OutputChannel outputChannel;
	private SMap<Long, SMap<String, String>> categories;
	private Long itemId;
	private final PropertyInfo nameProperty;
	private final PropertyInfo variantProperty;
	private final PropertyInfo priceProperty;
	private final PropertyInfo imageProperty;
	private SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> values;
	
	public ProductDetails(final String uiLanguage, final String language, final OutputChannel outputChannel, PropertyInfo nameProperty, PropertyInfo variantProperty, PropertyInfo priceProperty, PropertyInfo imageProperty) {
		this.uiLanguage = uiLanguage;
		this.language = language;
		this.outputChannel = outputChannel;
		this.nameProperty = nameProperty;
		this.variantProperty = variantProperty;
		this.priceProperty = priceProperty;
		this.imageProperty = imageProperty;
		
		initWidget(new FlowPanel() {{
//			add(new Trail());

			// Title
			add(new FlowPanel(){{
				Styles.add(this, Styles.productDetailsTitle);
				add(productNameBox = new TextBox() {{
					Styles.add(this, Styles.itemName);
				}});
				add(categoryPanel = new FlowPanel());
			}});
			
			// Image, Price, RelatedInfo
			add(new HorizontalPanel(){{
			    add(productImage = new Image() {{
			    	setSize("150px", "150px");
			    }});
			    add(productPrice = new Label() {{
			    	Util.add(this, Styles.productprice);
			    	setCellVerticalAlignment(this, HorizontalPanel.ALIGN_MIDDLE);
			    }});
			    add(new FlowPanel() {{
			    	add(new Anchor(Util.i18n.containedProducts(0)));
			    }});
			}});
			
			// Properties
			add(new Label(Util.i18n.properties()));
			add(propertyValues = new ItemPropertyValues(uiLanguage, language, outputChannel) {
				protected void propertyValueSet(Long itemId, PropertyInfo propertyInfo, String language, Object value) {
					ProductDetails.this.propertyValueSet(itemId, propertyInfo, language, value);
				}
				protected void propertyValueErased(Long itemId, PropertyInfo propertyInfo, String language) {
					ProductDetails.this.propertyValueRemoved(itemId, propertyInfo, language);
				}
			});		
			
			// TODO Add a popup panel at the bottom with property definitions (+ values??).
			// TODO add a popup panel at the bottom with property groups?
			// TODO add a popup panel with dangling properties.
		}});
	}
	
	
	public void setUiLanguage(String uiLanguage) {
		this.uiLanguage = uiLanguage;
		propertyValues.setUiLanguage(uiLanguage);
		
		render();
	}
	
	public void setLanguage(String language) {
		this.language = language;
		propertyValues.setLanguage(language);
		
		render();
	}
	
	public void setOutputChannel(OutputChannel outputChannel) {
		this.outputChannel = outputChannel;
		propertyValues.setOutputChannel(outputChannel);
		
		render();
	}

	
	/**
	 * set the item data and (re)render.
	 * 
	 * @param item
	 * @param values
	 */
	public void setItemData(Long itemId, SMap<Long, SMap<String, String>> categories, SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> values) {
		this.itemId = itemId;
		this.categories = categories;
		this.values = values;
		propertyValues.setItemData(itemId, values);
		
		render();
	}
	
	abstract protected void propertyValueSet(Long itemId, PropertyInfo propertyInfo, String language, Object value);

	abstract protected void propertyValueRemoved(Long itemId, PropertyInfo propertyInfo, String language);
	
	abstract protected void categoryRemoved(Long itemId, Long categoryId);
	
	private void render() {
		
		SMap<PropertyInfo, PropertyData> properties = stripGroupInfo(values);

		final Object productName = getValue(nameProperty, properties);
		productNameBox.setText(productName instanceof String ? productName.toString() : "");
		
//		final Object productVariant = getValue(variantProperty, properties);
//		rowWidgets.productVariantLabel.setText(productVariant instanceof String ? productVariant.toString() : "");
		
//		final Object productNr = getValue(artNoProperty, properties);
//		rowWidgets.productNrLabel.setText(productNr instanceof String ?  "Art. nr. " + productNr.toString() : "");
		
		// price
		final Object price = getValue(priceProperty, properties);
		if (price != null) {
			// TODO Use locale in the following format??
			productPrice.setText(PropertyStringConverter.toString(priceProperty.type, price));
		} else {
			productPrice.setText("");
		}

		List<Long> categoryKeys = categories.getKeys();

		categoryPanel.clear();
		for (Long categoryId : categoryKeys) {
			final Long catId = categoryId;
			final String categoryName = categories.getOrEmpty(categoryId).tryGet(language, null);
			categoryPanel.add(new Grid(1, 2) {{
				setWidget(0, 0, new Label(categoryName));
				setWidget(0, 1, new Anchor("X"){{
					// TODO set title/tooltip.
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							categoryRemoved(itemId, catId);
						}
					});
				}});
			}});
		}
	}
	
	private Object getValue(PropertyInfo property, SMap<PropertyInfo, PropertyData> properties) {
		PropertyData data = properties.get(property);
		if (data != null) {
			SMap<String, Object> channelValues = data.values.tryGet(outputChannel, null);
			if (channelValues != null) {
				Object candidate = channelValues.tryGet(language, null);
				if (candidate != null) {
					return candidate;
				}
			}
			
			// Fall back on effective values
			channelValues = data.effectiveValues.getOrEmpty(null).tryGet(outputChannel, null);
			if (channelValues != null) {
				Object candidate = channelValues.tryGet(language, null);
				if (candidate != null) {
					return candidate;
				}
			}
		}
		
		return null;
	}
	
	private static SMap<PropertyInfo, PropertyData> stripGroupInfo(SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> values) { 
		SMap<PropertyInfo, PropertyData> result = SMap.empty();
		
		for (Entry<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> group : values) {
			for (Entry<PropertyInfo, PropertyData> propertyData : group.getValue()) {
				result.add(propertyData.getKey(), propertyData.getValue());
			}
		}
		
		return result;
	}

}

package claro.catalog.manager.client;

import static claro.catalog.manager.client.CatalogManager.propertyStringConverter;

import java.util.Map.Entry;

import claro.catalog.data.MediaValue;
import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.manager.client.widgets.MediaWidget;
import claro.jpa.catalog.OutputChannel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.util.SMap;


abstract public class ProductDetails extends Composite implements Globals {
	private enum Styles implements Style { productDetails }
	
	private Label productNameBox;
	private CategoriesWidget categoryPanel;
	private Label productPrice;
	private MediaWidget productImage;
	
	private ItemPropertyValues propertyValues;
	private String language;
	private OutputChannel outputChannel;
	private SMap<Long, SMap<String, String>> categories;
	private Long itemId;
	private final PropertyInfo nameProperty;
	private final PropertyInfo variantProperty;
	private final PropertyInfo priceProperty;
	private final PropertyInfo imageProperty;
	private SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> values;
	
	public ProductDetails(final String language, final OutputChannel outputChannel, PropertyInfo nameProperty, PropertyInfo variantProperty, PropertyInfo priceProperty, PropertyInfo imageProperty) {
		this.language = language;
		this.outputChannel = outputChannel;
		this.nameProperty = nameProperty;
		this.variantProperty = variantProperty;
		this.priceProperty = priceProperty;
		this.imageProperty = imageProperty;
		
		initWidget(new DockLayoutPanel(Unit.PX) {{
			StyleUtil.add(this, Styles.productDetails);
			
			addNorth(new FlowPanel() {{
//			add(new Trail());
				
				// Title
				add(new Grid(1, 2) {{
					StyleUtil.add(this, CatalogManager.Styles.productDetailsTitle);
					setWidget(0, 0, productNameBox = new Label() {{
						StyleUtil.add(this, ProductMasterDetail.Styles.productname);
					}});
					setWidget(0, 1, categoryPanel = new CategoriesWidget() {
						protected String getAddCategoryTooltip() {
							return messages.addCategoryProductDetailsTooltip(productNameBox.getText());  // TODO This is a little dirty??
						}
						protected String getRemoveCategoryTooltip(String categoryName) {
							return messages.removeCategoryProductDetailsTooltip(categoryName);
						}
						protected void removeCategory(Long categoryId) {
							categoryRemoved(itemId, categoryId);
						}
					});
				}});
				
				// Image, Price
				add(new HorizontalPanel(){{
					add(productImage = new MediaWidget(false));
					add(productPrice = new Label() {{
						StyleUtil.add(this, ProductMasterDetail.Styles.productprice);
						setCellVerticalAlignment(this, HorizontalPanel.ALIGN_MIDDLE);
					}});
				}});
				
				// Properties
				add(new Label(messages.properties()));
				
			}}, 200);
			add(propertyValues = new ItemPropertyValues(language, outputChannel) {
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
		
		final Object productImageData = getValue(imageProperty, properties);
		if (productImageData instanceof MediaValue) {
			MediaValue productImageMediaValue = (MediaValue) productImageData;
			
			productImage.setData(productImageMediaValue.propertyValueId, productImageMediaValue.mimeType, productImageMediaValue.filename);
		} else {
			productImage.setData(null, null, null);
			
		}
		
		// price
		final Object price = getValue(priceProperty, properties);
		if (price != null) {
			// TODO Use locale in the following format??
			productPrice.setText(propertyStringConverter.toString(priceProperty.type, price));
		} else {
			productPrice.setText("");
		}

		categoryPanel.setData(categories, language);
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
				result = result.add(propertyData.getKey(), propertyData.getValue());
			}
		}
		
		return result;
	}

}

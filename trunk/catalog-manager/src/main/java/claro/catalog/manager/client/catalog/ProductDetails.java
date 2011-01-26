package claro.catalog.manager.client.catalog;

import static claro.catalog.manager.client.CatalogManager.propertyStringConverter;

import java.util.Collections;
import java.util.Map.Entry;

import claro.catalog.command.items.StoreProduct;
import claro.catalog.data.MediaValue;
import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.CatalogManager.Styles;
import claro.catalog.manager.client.widgets.CategoriesWidget;
import claro.catalog.manager.client.widgets.MediaWidget;
import claro.catalog.manager.client.widgets.StatusMessage;
import claro.jpa.catalog.OutputChannel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.gwt.client.widgets.MoneyFormatUtil;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.Money;
import easyenterprise.lib.util.SMap;


abstract public class ProductDetails extends Composite implements Globals {
	private enum Styles implements Style { productDetails, imagePrice }
	
	private Label productNameBox;
	private CategoriesWidget categoryPanel;
	private Label productPrice;
	private MediaWidget productImage;
	
	private ProductPropertyValues propertyValues;
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
		
		initWidget(new ScrollPanel(new FlowPanel() {{
			StyleUtil.add(this, Styles.productDetails);
			
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
						@Override
						protected void addCategory(Long categoryId, SMap<String, String> labels) {
							super.addCategory(categoryId, labels);
							categoryAdded(itemId, categoryId);
						}
						protected void removeCategory(Long categoryId) {
							super.removeCategory(categoryId);
							categoryRemoved(itemId, categoryId);
						}
					});
				}});
				
				// Image, Price
				add(new HorizontalPanel(){{
					StyleUtil.add(this, Styles.imagePrice);
					setVerticalAlignment(ALIGN_MIDDLE);
					add(productImage = new MediaWidget(false, true));
					add(productPrice = new Label() {{
						StyleUtil.add(this, ProductMasterDetail.Styles.productprice);
						setCellVerticalAlignment(this, HorizontalPanel.ALIGN_MIDDLE);
					}});
					productImage.setImageSize("125px", "125px");
				}});
				
				add(propertyValues = new ProductPropertyValues(language, outputChannel) {
					protected void propertyValueSet(Long itemId, PropertyInfo propertyInfo, String language, Object value) {
						ProductDetails.this.propertyValueSet(itemId, propertyInfo, language, value);
					}
					protected void propertyValueErased(Long itemId, PropertyInfo propertyInfo, String language) {
						ProductDetails.this.propertyValueRemoved(itemId, propertyInfo, language);
					}
				});		
			}}));
			
			// TODO Add a popup panel at the bottom with property definitions (+ values??).
			// TODO add a popup panel at the bottom with property groups?
			// TODO add a popup panel with dangling properties.
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
	
	abstract protected void storeItem(StoreProduct cmd);
	
	private void propertyValueSet(Long itemId, PropertyInfo propertyInfo, String language, Object value) {
		StoreProduct cmd = new StoreProduct();
		
		cmd.productId = itemId;
		cmd.valuesToSet = SMap.create(propertyInfo, SMap.create(language, value));

		// Always include name if this is a new item.
		if (itemId == null && !propertyInfo.equals(nameProperty)) {
			addNamePropertyValue(cmd);
		}
		// Always include categories for new items:
		if (itemId == null) {
			cmd.categoriesToSet = categoryPanel.getCategories().getKeys();
		}
		
		storeItem(cmd);
	}

	private void propertyValueRemoved(Long itemId, PropertyInfo propertyInfo, String language) {
		
		// Only remove values if the item exists:
		if (itemId != null) {
			StoreProduct cmd = new StoreProduct();
			cmd.productId = itemId;

			cmd.valuesToRemove = SMap.create(propertyInfo, Collections.singletonList(language));
			
			storeItem(cmd);
		}
	}
	
	private void categoryAdded(Long itemId, Long categoryId) {
		StoreProduct cmd = new StoreProduct();
		cmd.productId = itemId;
		
		cmd.categoriesToSet = categoryPanel.getCategories().getKeys();
		if (itemId == null) {
			// Always include name for a new item.
			addNamePropertyValue(cmd);
		}
		
		storeItem(cmd);
	}

	private void addNamePropertyValue(StoreProduct cmd) {
		SMap<PropertyInfo, PropertyData> properties = stripGroupInfo(values);
		Object productName = getValue(nameProperty, properties);
		cmd.valuesToSet.add(nameProperty, SMap.create(language, productName));
	}
	
	private void categoryRemoved(Long itemId, Long categoryId) {
		// if the item is new, do not store
		if (itemId != null) {
			StoreProduct cmd = new StoreProduct();
			cmd.productId = itemId;
			cmd.categoriesToSet = categoryPanel.getCategories().getKeys();
			
			storeItem(cmd);
		}
	}
	
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
			productPrice.setText(MoneyFormatUtil.full((Money)price));
		} else {
			productPrice.setText("");
		}

		categoryPanel.setData(categories, language);
	}
	
	private Object getValue(PropertyInfo property, SMap<PropertyInfo, PropertyData> properties) {
		PropertyData data = properties.get(property);
		if (data != null) {
			SMap<String, Object> channelValues = CollectionUtil.notNull(data.values).tryGet(outputChannel, null);
			if (channelValues != null) {
				Object candidate = channelValues.tryGet(language, null);
				if (candidate != null) {
					return candidate;
				}
			}
			
			// Fall back on effective values
			channelValues = CollectionUtil.notNull(data.effectiveValues).getOrEmpty(null).tryGet(outputChannel, null);
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

package claro.catalog.manager.client.catalog;

import java.util.Collections;
import java.util.Map.Entry;

import claro.catalog.command.items.ItemType;
import claro.catalog.command.items.StoreItemDetails;
import claro.catalog.data.MediaValue;
import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.data.RootProperties;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.taxonomy.ItemPropertyValues;
import claro.catalog.manager.client.webshop.ShopModel;
import claro.catalog.manager.client.widgets.CategoriesWidget;
import claro.catalog.manager.client.widgets.ConfirmationDialog;
import claro.catalog.manager.client.widgets.MediaWidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.gwt.client.widgets.Header;
import easyenterprise.lib.gwt.client.widgets.MoneyFormatUtil;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.Money;
import easyenterprise.lib.util.SMap;


abstract public class ProductDetails extends Composite implements Globals {
	private enum Styles implements Style { productDetails, imagePrice }
	
	private Header productNameBox;
	private CategoriesWidget categoryPanel;
	private Label productPrice;
	private MediaWidget productImage;
	private Anchor promotionAnchor;

	private ItemPropertyValues propertyValues;
	private SMap<Long, SMap<String, String>> categories;
	private Long itemId;
	private PropertyInfo nameProperty;
	private PropertyInfo variantProperty;
	private PropertyInfo priceProperty;
	private PropertyInfo imageProperty;
	private SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> values;
	private CatalogPageModel model;
	
	
	private ConfirmationDialog removeWithConfirmation = new ConfirmationDialog(images.removeIcon()) {
		protected String getMessage() {
			if (productNameBox != null) {
				return messages.removeProductConfirmationMessage(productNameBox.getText());
			}
			return null;
		};
		protected void yesPressed() {
			removeProduct(itemId);
		}
	};
	

	
	public ProductDetails(PropertyInfo nameProperty, PropertyInfo variantProperty, PropertyInfo priceProperty, PropertyInfo imageProperty) {
		this.nameProperty = nameProperty;
		this.variantProperty = variantProperty;
		this.priceProperty = priceProperty;
		this.imageProperty = imageProperty;
		
		initWidget(new ScrollPanel(new FlowPanel() {{
			StyleUtil.addStyle(this, Styles.productDetails);
			
//			add(new Trail());
				
				// Title
				add(new Grid(1, 3) {{
					StyleUtil.addStyle(this, CatalogManager.Styles.productDetailsTitle);
					setWidget(0, 0, productNameBox = new Header(1, "") {{
						StyleUtil.addStyle(this, ProductMasterDetail.Styles.productname);
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
					setWidget(0, 2, new Anchor(messages.removeProductLink()) {{
						addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								removeWithConfirmation.show();
							}
						});
					}});
				}});
				
				// Image, Price, Promotion
				
				add(new HorizontalPanel(){{
					StyleUtil.addStyle(this, Styles.imagePrice);
					setVerticalAlignment(ALIGN_MIDDLE);
					add(productImage = new MediaWidget(false, true));
					add(productPrice = new Label() {{
						StyleUtil.addStyle(this, ProductMasterDetail.Styles.productprice);
						setCellVerticalAlignment(this, HorizontalPanel.ALIGN_MIDDLE);
					}});
					productImage.setImageSize("125px", "125px");
					add(promotionAnchor = new Anchor(messages.addPromotionLink()) {{
						addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								doPromotion();
							}
						});
					}});
				}});
				
				add(propertyValues = new ItemPropertyValues(false, false) {
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
	
	public void setModel(CatalogPageModel m) {
		this.model = m;
		propertyValues.setModel(m);
	}
	
	public void setRootProperties(SMap<String, PropertyInfo> rootProperties) {
		this.nameProperty = rootProperties.get(RootProperties.NAME);
		this.variantProperty = rootProperties.get(RootProperties.VARIANT);
		this.priceProperty = rootProperties.get(RootProperties.PRICE);
		this.imageProperty = rootProperties.get(RootProperties.IMAGE);
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
		propertyValues.setItemData(itemId, null, null, values);
		
		render();
	}
	

	public void resetTabState() {
		propertyValues.resetTabState();
	}


	
	abstract protected void storeItem(StoreItemDetails cmd);
	
	private void removeProduct(Long itemId) {
		StoreItemDetails cmd = new StoreItemDetails();
		
		cmd.itemId = itemId;
		cmd.itemType = ItemType.product;
		cmd.remove = true;
		
		storeItem(cmd);
	}

	private void propertyValueSet(Long itemId, PropertyInfo propertyInfo, String language, Object value) {
		StoreItemDetails cmd = new StoreItemDetails();
		
		cmd.itemId = itemId;
		cmd.itemType = ItemType.product;
		cmd.valuesToSet = SMap.create(propertyInfo, SMap.create(language, value));

		// Always include name if this is a new item.
		if (itemId == null && !propertyInfo.equals(nameProperty)) {
			addNamePropertyValue(cmd);
		}
		// Always include categories for new items:
		if (itemId == null) {
			cmd.parentsToSet = categoryPanel.getCategories().getKeys();
		}
		
		storeItem(cmd);
	}

	private void propertyValueRemoved(Long itemId, PropertyInfo propertyInfo, String language) {
		
		// Only remove values if the item exists:
		if (itemId != null) {
			StoreItemDetails cmd = new StoreItemDetails();
			cmd.itemId = itemId;
			cmd.itemType = ItemType.product;

			cmd.valuesToRemove = SMap.create(propertyInfo, Collections.singletonList(language));
			
			storeItem(cmd);
		}
	}
	
	private void categoryAdded(Long itemId, Long categoryId) {
		StoreItemDetails cmd = new StoreItemDetails();
		cmd.itemId = itemId;
		cmd.itemType = ItemType.product;
		
		cmd.parentsToSet = categoryPanel.getCategories().getKeys();
		if (itemId == null) {
			// Always include name for a new item.
			addNamePropertyValue(cmd);
		}
		
		storeItem(cmd);
	}

	private void addNamePropertyValue(StoreItemDetails cmd) {
		SMap<PropertyInfo, PropertyData> properties = stripGroupInfo(values);
		Object productName = getValue(nameProperty, properties);
		cmd.valuesToSet = cmd.valuesToSet.add(nameProperty, SMap.create(model.getSelectedLanguage(), productName));
	}
	
	private void categoryRemoved(Long itemId, Long categoryId) {
		// if the item is new, do not store
		if (itemId != null) {
			StoreItemDetails cmd = new StoreItemDetails();
			cmd.itemId = itemId;
			cmd.parentsToSet = categoryPanel.getCategories().getKeys();
			
			storeItem(cmd);
		}
	}
	
	private void render() {
		// Make sure we have the root properties:
		if (nameProperty == null) {
			return;
		}
		
		if (values == null) {
			return;
		}
		
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

		// Update categories.
		categoryPanel.setData(categories, model.getSelectedLanguage());
		
		// Update promotions
		SMap<Long, SMap<String, String>> promotionsForShop = model.getPromotions() != null?model.getPromotions().get(model.getSelectedShop()) : null;
		if (promotionsForShop != null) {
			String promotionText = promotionsForShop.getFirst().tryGet(model.getSelectedLanguage(), null);
			promotionAnchor.setText(messages.promotionsLink(promotionText));
		} else {
			promotionAnchor.setText(messages.addPromotionLink());
		}
		
		promotionAnchor.setVisible(model.getSelectedShop() != null);
	}
	
	private Object getValue(PropertyInfo property, SMap<PropertyInfo, PropertyData> properties) {
		PropertyData data = properties.get(property);
		if (data != null) {
			SMap<String, Object> channelValues = CollectionUtil.notNull(data.values).tryGet(model.getSelectedShop(), null);
			if (channelValues != null) {
				Object candidate = channelValues.tryGet(model.getSelectedLanguage(), null);
				if (candidate != null) {
					return candidate;
				}
			}
			
			// Fall back on effective values
			channelValues = CollectionUtil.notNull(data.effectiveValues).getOrEmpty(null).tryGet(model.getSelectedShop(), null);
			if (channelValues != null) {
				Object candidate = channelValues.tryGet(model.getSelectedLanguage(), null);
				if (candidate != null) {
					return candidate;
				}
			}
		}
		
		return null;
	}
	
	private void doPromotion() {
		// Goto webshop model and lookup/add promotion
		
		SMap<Long, SMap<String, String>> promotionsForShop = model.getPromotions() != null ? model.getPromotions().get(model.getSelectedShop()) : null;
		if (promotionsForShop != null) {
			model.getShopModel().showPromotion(itemId);
		} else {
			model.getShopModel().addNewPromotion(model.getSelectedShop(), itemId);
		}
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

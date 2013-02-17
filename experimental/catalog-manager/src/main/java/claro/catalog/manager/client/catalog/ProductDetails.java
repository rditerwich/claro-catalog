package claro.catalog.manager.client.catalog;

import java.util.Collections;
import java.util.Map.Entry;

import claro.catalog.command.items.StoreItemDetails;
import claro.catalog.data.ItemType;
import claro.catalog.data.MediaValue;
import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.taxonomy.ItemPropertyValues;
import claro.catalog.manager.client.widgets.ConfirmationDialog;
import claro.catalog.manager.client.widgets.ItemSelectionWidget;
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

public class ProductDetails extends Composite implements Globals {
	private enum Styles implements Style { productDetails, imagePrice }
	
	private final CatalogPage page;
	private final CatalogPageModel model;
	
	private Header productNameBox;
	private ItemSelectionWidget categoryPanel;
	private Label productPrice;
	private MediaWidget productImage;
	private Anchor promotionAnchor;

	private ItemPropertyValues propertyValues;
	
	
	private ConfirmationDialog removeWithConfirmation = new ConfirmationDialog(images.removeIcon()) {
		protected String getMessage() {
			if (productNameBox != null) {
				return messages.removeProductConfirmationMessage(productNameBox.getText());
			}
			return null;
		};
		protected void yesPressed() {
			removeProduct(model.getSelectedProductId());
		}
	};
	
	public ProductDetails(CatalogPage page, final CatalogPageModel model) {
		this.page = page;
		this.model = model;
		
		initWidget(new ScrollPanel(new FlowPanel() {{
			StyleUtil.addStyle(this, Styles.productDetails);
			
//			add(new Trail());
				
				// Title
				add(new Grid(1, 3) {{
					StyleUtil.addStyle(this, CatalogManager.Styles.productDetailsTitle);
					setWidget(0, 0, productNameBox = new Header(1, "") {{
						StyleUtil.addStyle(this, CatalogPage.Styles.productname);
					}});
					setWidget(0, 1, categoryPanel = new ItemSelectionWidget() {
						protected String getAddSelectionTooltip() {
							return messages.addCategoryProductDetailsTooltip(productNameBox.getText());  // TODO This is a little dirty??
						}
						protected String getRemoveSelectedObjectTooltip(String categoryName) {
							return messages.removeCategoryProductDetailsTooltip(categoryName);
						}
						@Override
						protected void addItem(Long categoryId, SMap<String, String> labels) {
							super.addItem(categoryId, labels);
							categoryAdded(model.getSelectedProductId(), categoryId);
						}
						protected void removeItem(Long categoryId) {
							super.removeItem(categoryId);
							categoryRemoved(model.getSelectedProductId(), categoryId);
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
				
				add(new HorizontalPanel() {{
					StyleUtil.addStyle(this, Styles.imagePrice);
					setVerticalAlignment(ALIGN_MIDDLE);
					add(productImage = new MediaWidget(false, true));
					add(productPrice = new Label() {{
						StyleUtil.addStyle(this, CatalogPage.Styles.productprice);
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
				
				add(propertyValues = new ItemPropertyValues(model, false, false) {
					protected void propertyValueSet(Long itemId, PropertyInfo propertyInfo, Object value) {
						ProductDetails.this.propertyValueSet(itemId, propertyInfo, value);
					}
					protected void propertyValueErased(Long itemId, PropertyInfo propertyInfo) {
						ProductDetails.this.propertyValueRemoved(itemId, propertyInfo);
					}
				});		
			}}));
			
			// TODO Add a popup panel at the bottom with property definitions (+ values??).
			// TODO add a popup panel at the bottom with property groups?
			// TODO add a popup panel with dangling properties.
	}
	
	
	private void removeProduct(Long itemId) {
		StoreItemDetails cmd = new StoreItemDetails();
		
		cmd.itemId = itemId;
		cmd.itemType = ItemType.product;
		cmd.remove = true;
		
		page.storeItem(cmd);
	}

	private void propertyValueSet(Long itemId, PropertyInfo propertyInfo, Object value) {
		StoreItemDetails cmd = new StoreItemDetails();
		
		cmd.itemId = itemId;
		cmd.itemType = ItemType.product;
		cmd.valuesToSet = SMap.create(propertyInfo, SMap.create(model.getSelectedLanguage(), value));

		// Always include name if this is a new item.
		if (itemId == null && !propertyInfo.equals(model.getNameProperty())) {
			addNamePropertyValue(cmd);
		}
		// Always include categories for new items:
		if (itemId == null) {
			cmd.parentsToSet = categoryPanel.getSelection();
		}
		
		page.storeItem(cmd);
	}

	private void propertyValueRemoved(Long itemId, PropertyInfo propertyInfo) {
		
		// Only remove values if the item exists:
		if (itemId != null) {
			StoreItemDetails cmd = new StoreItemDetails();
			cmd.itemId = itemId;
			cmd.itemType = ItemType.product;

			cmd.valuesToRemove = SMap.create(propertyInfo, Collections.singletonList(model.getSelectedLanguage()));
			
			page.storeItem(cmd);
		}
	}
	
	private void categoryAdded(Long itemId, Long categoryId) {
		StoreItemDetails cmd = new StoreItemDetails();
		cmd.itemId = itemId;
		cmd.itemType = ItemType.product;
		
		cmd.parentsToSet = categoryPanel.getSelection();
		if (itemId == null) {
			// Always include name for a new item.
			addNamePropertyValue(cmd);
		}
		
		page.storeItem(cmd);
	}

	private void addNamePropertyValue(StoreItemDetails cmd) {
		SMap<PropertyInfo, PropertyData> properties = stripGroupInfo(model.getSelectedProductPropertyValues());
		Object productName = getValue(model.getNameProperty(), properties);
		cmd.valuesToSet = cmd.valuesToSet.add(model.getNameProperty(), SMap.create(model.getSelectedLanguage(), productName));
	}
	
	private void categoryRemoved(Long itemId, Long categoryId) {
		// if the item is new, do not store
		if (itemId != null) {
			StoreItemDetails cmd = new StoreItemDetails();
			cmd.itemId = itemId;
			cmd.itemType = ItemType.product;
			cmd.parentsToSet = categoryPanel.getSelection();
			
			page.storeItem(cmd);
		}
	}
	
	void render() {
		// Make sure we have the root properties:
		if (model.getNameProperty() == null) {
			return;
		}
		
		if (model.getSelectedProductPropertyValues() == null) {
			return;
		}
		
		SMap<PropertyInfo, PropertyData> properties = stripGroupInfo(model.getSelectedProductPropertyValues());

		final Object productName = getValue(model.getNameProperty(), properties);
		productNameBox.setText(productName instanceof String ? productName.toString() : "");
		
//		final Object productVariant = getValue(variantProperty, properties);
//		rowWidgets.productVariantLabel.setText(productVariant instanceof String ? productVariant.toString() : "");
		
//		final Object productNr = getValue(artNoProperty, properties);
//		rowWidgets.productNrLabel.setText(productNr instanceof String ?  "Art. nr. " + productNr.toString() : "");
		
		final Object productImageData = getValue(model.getImageProperty(), properties);
		if (productImageData instanceof MediaValue) {
			MediaValue productImageMediaValue = (MediaValue) productImageData;
			
			productImage.setData(productImageMediaValue.mediaContentId, productImageMediaValue.mimeType, productImageMediaValue.name);
		} else {
			productImage.setData(null, null, null);
			
		}
		
		// price
		final Object price = getValue(model.getPriceProperty(), properties);
		if (price != null) {
			// TODO Use locale in the following format??
			productPrice.setText(MoneyFormatUtil.full((Money)price));
		} else {
			productPrice.setText("");
		}

		// Update categories.
		categoryPanel.setData(model.getSelectedProductCategories(), model.getSelectedLanguage());
		
		propertyValues.setItemData(model.getSelectedProductId(), model.getSelectedProductCategories(), model.getSelectedParentExtentWithSelf(), model.getSelectedProductPropertyValues());
		
		
		// Update promotions
		SMap<Long, SMap<String, String>> promotionsForShop = model.getSelectedProductPromotions() != null ? model.getSelectedProductPromotions().get(model.getSelectedShop()) : null;
		if (promotionsForShop != null) {
			String promotionText = promotionsForShop.get(0).tryGet(model.getSelectedLanguage(), null);
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
		
		SMap<Long, SMap<String, String>> promotionsForShop = model.getSelectedProductPromotions() != null ? model.getSelectedProductPromotions().get(model.getSelectedShop()) : null;
		if (promotionsForShop != null) {
			model.getShopModel().showPromotion(model.getSelectedProductId());
		} else {
			model.getShopModel().addNewPromotion(model.getSelectedShop(),model.getSelectedProductId());
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

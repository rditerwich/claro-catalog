package claro.catalog.manager.client.taxonomy;

import java.util.Collections;
import java.util.Map.Entry;

import claro.catalog.command.items.StoreItemDetails;
import claro.catalog.data.ItemType;
import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.data.RootProperties;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.ConfirmationDialog;
import claro.catalog.manager.client.widgets.ItemSelectionWidget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.gwt.client.widgets.EEButton;
import easyenterprise.lib.gwt.client.widgets.PullUpTabs;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.SMap;


abstract public class CategoryDetails extends Composite implements Globals, RequiresResize {
	private enum Styles implements Style { categoryDetails, imagePrice, categoryname }
	
	private HasText categoryNameBox;
	private ItemSelectionWidget categoryPanel;
	private Anchor removeLink;

	
//	private Label productPrice;
//	private MediaWidget productImage;
	
	private ItemPropertyValues inheritedPropertyValuesComponent;
	private SMap<Long, SMap<String, String>> parents;
	private Long itemId;
	private PropertyInfo nameProperty;
	private PropertyInfo variantProperty;
	private PropertyInfo priceProperty;
	private PropertyInfo imageProperty;
	private SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> inheritedPropertyValues;
	protected CategoryProperties propertiesComponent;
	private SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> propertyValues;
	private SMap<Long, SMap<String, String>> groups;
	private PullUpTabs pullups;
	private TaxonomyModel model;
	
	private ConfirmationDialog removeWithConfirmation = new ConfirmationDialog(images.removeIcon()) {
		protected String getMessage() {
			if (categoryNameBox != null) {
				return messages.removeCategoryConfirmationMessage(categoryNameBox.getText());
			}
			return null;
		};
		protected void yesPressed() {
			removeCategory(itemId);
		}
	};
	

	
	public CategoryDetails(final TaxonomyModel model, PropertyInfo nameProperty, PropertyInfo variantProperty, PropertyInfo priceProperty, PropertyInfo imageProperty) {
		this.model = model;
		this.nameProperty = nameProperty;
		this.variantProperty = variantProperty;
		this.priceProperty = priceProperty;
		this.imageProperty = imageProperty;
		
		initWidget(pullups = new PullUpTabs(26, 5) {{
			StyleUtil.addStyle(this, Styles.categoryDetails);
			setMainWidget(new ScrollPanel(new FlowPanel() {{
				
//			add(new Trail());
				
				// Title
				add(new Grid(1, 3) {{
					StyleUtil.addStyle(this, CatalogManager.Styles.productDetailsTitle);
					setWidget(0, 0, (Widget)(categoryNameBox = new TextBox() {{
						StyleUtil.addStyle(this, Styles.categoryname);
						addChangeHandler(new ChangeHandler() {
							public void onChange(ChangeEvent event) {
								CategoryDetails.this.propertyValueSet(itemId, CategoryDetails.this.nameProperty, categoryNameBox.getText());
							}
						});
					}}));
					setWidget(0, 1, categoryPanel = new ItemSelectionWidget() {
						protected String getAddToSelectionLabel() {
							return messages.addParentCategoriesLink();
						};
						protected String getAddSelectionTooltip() {
							return messages.addCategoryProductDetailsTooltip(categoryNameBox.getText());  // TODO This is a little dirty??
						}
						protected String getRemoveSelectedObjectTooltip(String categoryName) {
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
					setWidget(0, 2, removeLink = new Anchor(messages.removeCategoryLink()) {{
						addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								removeWithConfirmation.show();
							}
						});
					}});

				}});
				
//				// Image, Price
//				add(new HorizontalPanel(){{
//					StyleUtil.add(this, Styles.imagePrice);
//					setVerticalAlignment(ALIGN_MIDDLE);
//					add(productImage = new MediaWidget(false, true));
//					add(productPrice = new Label() {{
//						StyleUtil.add(this, CategoryMasterDetail.Styles.productprice);
//						setCellVerticalAlignment(this, HorizontalPanel.ALIGN_MIDDLE);
//					}});
//					productImage.setImageSize("125px", "125px");
//				}});
				
				add(propertiesComponent = new CategoryProperties(model) {
					protected void propertyToSet(Long itemId, PropertyInfo propertyInfo) {
						CategoryDetails.this.propertySet(itemId, propertyInfo);
					}
					protected void propertyGroupToSet(Long itemId, PropertyInfo propertyInfo, PropertyGroupInfo group) {
						CategoryDetails.this.propertyGroupSet(itemId, propertyInfo, group);
					}
					protected void propertyToRemove(Long itemId, PropertyInfo propertyInfo) {
						CategoryDetails.this.propertyRemoved(itemId, propertyInfo);
					}
				});	

			}}));
			addTab(new EEButton(messages.defaultValuesTab()), 150, inheritedPropertyValuesComponent = new ItemPropertyValues(model, true, true) {
				protected void propertyValueSet(Long itemId, PropertyInfo propertyInfo, Object value) {
					CategoryDetails.this.propertyValueSet(itemId, propertyInfo, value);
				}
				protected void propertyValueErased(Long itemId, PropertyInfo propertyInfo) {
					CategoryDetails.this.propertyValueRemoved(itemId, propertyInfo);
				}
			});
			showTab(0);
			hideTab();
			
		}});
			
			// TODO add a popup panel with dangling properties.
	}
	
	public void setRootProperties(SMap<String, PropertyInfo> rootProperties) {
		this.nameProperty = rootProperties.get(RootProperties.NAME);
		this.variantProperty = rootProperties.get(RootProperties.VARIANT);
		this.priceProperty = rootProperties.get(RootProperties.PRICE);
		this.imageProperty = rootProperties.get(RootProperties.IMAGE);
		
		render();
	}

	/**
	 * set the item data and (re)render.
	 * 
	 * @param item
	 * @param values
	 */
	public void setItemData(Long itemId, SMap<Long, SMap<String, String>> groups, SMap<Long, SMap<String, String>> parentExtentWithSelf, SMap<Long, SMap<String, String>> parents, SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> values) {
		this.itemId = itemId;
		this.groups = groups;
		this.parents = parents;
		
		this.propertyValues = splitValues(values, itemId, false);
		
		this.inheritedPropertyValues = values;// splitValues(values, itemId, true);
		
		propertiesComponent.setItemData(itemId, groups, parentExtentWithSelf, propertyValues);
		
		inheritedPropertyValuesComponent.setItemData(itemId, groups, parentExtentWithSelf, inheritedPropertyValues);
		
		render();	
	}

	public void onResize() {
		pullups.onResize();
	}
	
	private static SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> splitValues(SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> values, Long itemId, boolean inheritedProperty) {
		SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> result = SMap.empty();
		
		for (Entry<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> group : values) {
			for (Entry<PropertyInfo, PropertyData> property : group.getValue()) {
				if ((property.getKey().ownerItemId != itemId) == inheritedProperty) {
					SMap<PropertyInfo, PropertyData> properties = result.getOrEmpty(group.getKey());
					
					properties = properties.add(property.getKey(), property.getValue());
					
					result = result.set(group.getKey(), properties);
				}
			}
		}
		
		return result;
	}

	abstract protected void storeItem(StoreItemDetails cmd);
	
	private void removeCategory(Long itemId) {
		StoreItemDetails cmd = new StoreItemDetails();
		
		cmd.itemId = itemId;
		cmd.itemType = ItemType.category;
		cmd.remove = true;
		
		storeItem(cmd);
	}
	
	private void propertyValueSet(Long itemId, PropertyInfo propertyInfo, Object value) {
		StoreItemDetails cmd = new StoreItemDetails();
		
		cmd.itemId = itemId;
		cmd.itemType = ItemType.category;
		cmd.valuesToSet = SMap.create(propertyInfo, SMap.create(model.getSelectedLanguage(), value));

		// Always include name if this is a new item.
		if (itemId == null && !propertyInfo.equals(nameProperty)) {
			addNamePropertyValue(cmd);
		}
		// Always include categories for new items:
		if (itemId == null) {
			cmd.parentsToSet = categoryPanel.getSelection();
		}
		
		storeItem(cmd);
	}

	private void propertyValueRemoved(Long itemId, PropertyInfo propertyInfo) {
		
		// Only remove values if the item exists:
		if (itemId != null) {
			StoreItemDetails cmd = new StoreItemDetails();
			cmd.itemId = itemId;
			cmd.itemType = ItemType.category;

			cmd.valuesToRemove = SMap.create(propertyInfo, Collections.singletonList(model.getSelectedLanguage()));
			
			storeItem(cmd);
		}
	}
	
	private void propertySet(Long itemId, PropertyInfo propertyInfo) {
		StoreItemDetails cmd = new StoreItemDetails();
		
		cmd.itemId = itemId;
		cmd.itemType = ItemType.category;
		cmd.propertiesToSet = Collections.singletonList(propertyInfo);
		
		storeItem(cmd);
	}
	
	private void propertyGroupSet(Long itemId, PropertyInfo propertyInfo, PropertyGroupInfo groupInfo) {
		StoreItemDetails cmd = new StoreItemDetails();
		
		cmd.itemId = itemId;
		cmd.itemType = ItemType.category;
		cmd.groupsToSet = SMap.create(propertyInfo, groupInfo);
		
		storeItem(cmd);
	}
	
	private void propertyRemoved(Long itemId, PropertyInfo propertyInfo) {
		
		// Only remove values if the item exists:
		if (itemId != null) {
			StoreItemDetails cmd = new StoreItemDetails();
			cmd.itemId = itemId;
			cmd.itemType = ItemType.category;
			
			cmd.propertiesToRemove = Collections.singletonList(propertyInfo.propertyId);
			
			storeItem(cmd);
		}
	}
	
	private void categoryAdded(Long itemId, Long categoryId) {
		StoreItemDetails cmd = new StoreItemDetails();
		cmd.itemId = itemId;
		cmd.itemType = ItemType.category;
		
		cmd.parentsToSet = categoryPanel.getSelection();
		if (itemId == null) {
			// Always include name for a new item.
			addNamePropertyValue(cmd);
		}
		
		storeItem(cmd);
	}

	private void addNamePropertyValue(StoreItemDetails cmd) {
		SMap<PropertyInfo, PropertyData> properties = stripGroupInfo(inheritedPropertyValues);
		Object productName = getValue(nameProperty, properties);
		cmd.valuesToSet = cmd.valuesToSet.add(nameProperty, SMap.create(model.getSelectedLanguage(), productName));
	}
	
	private void categoryRemoved(Long itemId, Long categoryId) {
		// if the item is new, do not store
		if (itemId != null) {
			StoreItemDetails cmd = new StoreItemDetails();
			cmd.itemId = itemId;
			cmd.itemType = ItemType.category;
			cmd.parentsToSet = categoryPanel.getSelection();
			
			storeItem(cmd);
		}
	}
	
	private void render() {
		// Make sure we have the root properties:
		if (nameProperty == null) {
			return;
		}
		
		if (inheritedPropertyValues == null) {
			return;
		}
		
		SMap<PropertyInfo, PropertyData> properties = stripGroupInfo(inheritedPropertyValues);

		final Object productName = getValue(nameProperty, properties);
		categoryNameBox.setText(productName instanceof String ? productName.toString() : "");
		
		// Disable removed link for the root category.
		removeLink.setVisible(itemId != model.getRootCategoryId());
		
		// Also disable parent selection
		categoryPanel.setVisible(itemId != model.getRootCategoryId());
		
//		final Object productVariant = getValue(variantProperty, properties);
//		rowWidgets.productVariantLabel.setText(productVariant instanceof String ? productVariant.toString() : "");
		
//		final Object productNr = getValue(artNoProperty, properties);
//		rowWidgets.productNrLabel.setText(productNr instanceof String ?  "Art. nr. " + productNr.toString() : "");
		
//		final Object productImageData = getValue(imageProperty, properties);
//		if (productImageData instanceof MediaValue) {
//			MediaValue productImageMediaValue = (MediaValue) productImageData;
//			
//			productImage.setData(productImageMediaValue.propertyValueId, productImageMediaValue.mimeType, productImageMediaValue.filename);
//		} else {
//			productImage.setData(null, null, null);
//			
//		}
//		
//		// price
//		final Object price = getValue(priceProperty, properties);
//		if (price != null) {
//			// TODO Use locale in the following format??
//			productPrice.setText(MoneyFormatUtil.full((Money)price));
//		} else {
//			productPrice.setText("");
//		}

		categoryPanel.setData(parents, model.getSelectedLanguage());
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

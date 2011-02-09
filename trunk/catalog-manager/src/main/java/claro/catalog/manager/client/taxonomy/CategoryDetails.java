package claro.catalog.manager.client.taxonomy;

import java.util.Collections;
import java.util.Map.Entry;

import claro.catalog.command.items.ItemType;
import claro.catalog.command.items.StoreItemDetails;
import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.data.RootProperties;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.CategoriesWidget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
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
	private CategoriesWidget categoryPanel;
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
	
	public CategoryDetails( PropertyInfo nameProperty, PropertyInfo variantProperty, PropertyInfo priceProperty, PropertyInfo imageProperty) {
		this.nameProperty = nameProperty;
		this.variantProperty = variantProperty;
		this.priceProperty = priceProperty;
		this.imageProperty = imageProperty;
		
		initWidget(pullups = new PullUpTabs(30, 5) {{
			StyleUtil.addStyle(this, Styles.categoryDetails);
			setMainWidget(new ScrollPanel(new FlowPanel() {{
				
//			add(new Trail());
				
				// Title
				add(new Grid(1, 2) {{
					StyleUtil.addStyle(this, CatalogManager.Styles.productDetailsTitle);
					setWidget(0, 0, (Widget)(categoryNameBox = new TextBox() {{
						StyleUtil.addStyle(this, Styles.categoryname);
						addChangeHandler(new ChangeHandler() {
							public void onChange(ChangeEvent event) {
								CategoryDetails.this.propertyValueSet(itemId, CategoryDetails.this.nameProperty, model.getSelectedLanguage(), categoryNameBox.getText());
							}
						});
					}}));
					setWidget(0, 1, categoryPanel = new CategoriesWidget() {
						protected String getAddCategoryLabel() {
							return messages.addParentCategoriesLink();
						};
						protected String getAddCategoryTooltip() {
							return messages.addCategoryProductDetailsTooltip(categoryNameBox.getText());  // TODO This is a little dirty??
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
				
				add(propertiesComponent = new CategoryProperties() {
					protected void propertyValueSet(Long itemId, PropertyInfo propertyInfo, String language, Object value) {
						CategoryDetails.this.propertyValueSet(itemId, propertyInfo, language, value);
					}
					protected void propertyValueErased(Long itemId, PropertyInfo propertyInfo, String language) {
						CategoryDetails.this.propertyValueRemoved(itemId, propertyInfo, language);
					}
				});	
			}}));
			addTab(new EEButton(messages.defaultValuesTab()), 150, inheritedPropertyValuesComponent = new ItemPropertyValues(true, true) {
				protected void propertyValueSet(Long itemId, PropertyInfo propertyInfo, String language, Object value) {
					CategoryDetails.this.propertyValueSet(itemId, propertyInfo, language, value);
				}
				protected void propertyValueErased(Long itemId, PropertyInfo propertyInfo, String language) {
					CategoryDetails.this.propertyValueRemoved(itemId, propertyInfo, language);
				}
			});
			showTab(0);
			hideTab();
			
		}});
			
			// TODO add a popup panel with dangling properties.
	}
	
	public void setModel(TaxonomyModel model) {
		this.model = model;
		propertiesComponent.setModel(model);
		inheritedPropertyValuesComponent.setModel(model);
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
	

	public void resetTabState() {
		pullups.hideTab();
		inheritedPropertyValuesComponent.resetTabState();
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
	
	private void propertyValueSet(Long itemId, PropertyInfo propertyInfo, String language, Object value) {
		StoreItemDetails cmd = new StoreItemDetails();
		
		cmd.itemId = itemId;
		cmd.itemType = ItemType.catagory;
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
			cmd.itemType = ItemType.catagory;

			cmd.valuesToRemove = SMap.create(propertyInfo, Collections.singletonList(language));
			
			storeItem(cmd);
		}
	}
	
	private void categoryAdded(Long itemId, Long categoryId) {
		StoreItemDetails cmd = new StoreItemDetails();
		cmd.itemId = itemId;
		cmd.itemType = ItemType.catagory;
		
		cmd.parentsToSet = categoryPanel.getCategories().getKeys();
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
			cmd.parentsToSet = categoryPanel.getCategories().getKeys();
			
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

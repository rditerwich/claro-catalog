package claro.catalog.impl.items;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import claro.catalog.data.ItemType;
import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.Label;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.PropertyGroup;
import claro.jpa.catalog.PropertyType;
import claro.jpa.catalog.StagingArea;
import claro.jpa.shop.VolumeDiscountPromotion;
import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.Cloner;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.util.Money;
import easyenterprise.lib.util.SMap;

public class ItemUtil {
	static View view = new BasicView();
	
	public static SMap<String, String> getNameLabels(ItemModel item, CatalogModel catalogModel, OutputChannel outputChannel, StagingArea stagingArea) {
		SMap<String, String> result = SMap.empty();
		
		// Lookup name property
		PropertyModel nameProperty = item.findProperty(catalogModel.nameProperty.getPropertyId(), true);
		
		// get the name value for each language
		if (nameProperty != null) {
			SMap<String,Object> values = nameProperty.getEffectiveValues(stagingArea, outputChannel);
			for (String language : values.getKeys()) {
				result = result.add(language, (String) values.get(language));
			}
		}
		
		return result;
	}
	
	public static SMap<String, String> getNameLabels(PropertyGroup group) {
		SMap<String, String> result = SMap.empty();
		
		for (Label label : group.getLabels()) {
			result = result.add(label.getLanguage(), label.getLabel());
		}
		
		return result;
	}
	

	public static SMap<PropertyInfo, SMap<String, Object>> effectivePropertyValues(ItemModel candidate, StagingArea stagingArea, OutputChannel outputChannel) {
		SMap<PropertyInfo, SMap<String, Object>> propertyValues = SMap.empty();
		for (Entry<PropertyGroupInfo, PropertyModel> property : candidate.getPropertyExtent()) {
			SMap<String, Object> convertedValues = SMap.empty();
			for (Entry<String, Object> value : property.getValue().getEffectiveValues(stagingArea, outputChannel)) {
				convertedValues = convertedValues.add(value.getKey(), convertModelValue(property.getValue().getPropertyInfo().getType(), value.getValue()));
			}

			propertyValues = propertyValues.add(property.getValue().getPropertyInfo(), convertedValues);
		}
		return propertyValues;
	}
	

	public static SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> propertyData(CatalogModel catalogModel, ItemModel itemModel, StagingArea area, OutputChannel channel) {
		SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> resultPropertyData = SMap.empty();
		area = Cloner.clone(area, view);
		channel = Cloner.clone(channel, view);
		// Obtain groups
		// TODO Is there no property hiding???
		
		SMap<PropertyGroupInfo, PropertyModel> propertyExtent = itemModel.getPropertyExtent();
		for (PropertyGroupInfo group : propertyExtent.getKeys()) {
			SMap<PropertyInfo, PropertyData> properties = SMap.empty();
			for (PropertyModel property : propertyExtent.getAll(group)) {
				PropertyData propertyData = new PropertyData();
				
				// fill propertyData
				propertyData.groupAssignmentItemId = property.getGroupAssignmentItemId();
				if (propertyData.groupAssignmentItemId != null) {
					propertyData.groupItemNameLabels = getNameLabels(catalogModel.getItem(propertyData.groupAssignmentItemId), catalogModel, channel, area);
				}
				
				// Values
				SMap<String, Object> values = property.getValues(area, channel);
				if (values != null && !values.isEmpty()) {
					SMap<String, Object> convertedValues = SMap.empty();
					for (Entry<String, Object> value : values) {
						convertedValues = convertedValues.add(value.getKey(), convertModelValue(property.getPropertyInfo().getType(), value.getValue()));
					}
					propertyData.values = SMap.create(channel, convertedValues);
				}
				
				// Effective Values
				SMap<String, Object> effectiveValues = property.getEffectiveValues(area, channel);
				if (effectiveValues != null && !effectiveValues.isEmpty()) {
					SMap<String, Object> convertedValues = SMap.empty();
					for (Entry<String, Object> value : effectiveValues) {
						convertedValues = convertedValues.add(value.getKey(), convertModelValue(property.getPropertyInfo().getType(), value.getValue()));
					}
					propertyData.effectiveValues = SMap.create(area, SMap.create(channel, convertedValues));
				}
				
				// TODO How to do this???
//			    propertyData.sourceValues = SMap.create(channel, property.getSourceValues(null)));
				
				PropertyInfo propertyInfo = property.getPropertyInfo();
//				System.out.println("property " + propertyInfo.propertyId + " type " + propertyInfo.type + " ord: " + propertyInfo.type.ordinal());
//				System.out.println("property " + propertyInfo.propertyId + " type " + propertyInfo.type + " ord: " + propertyInfo.type);//.ordinal());
				properties = properties.add(propertyInfo, propertyData);
			}
			resultPropertyData = resultPropertyData.add(group, properties);
			
		}
		return resultPropertyData;
	}


	private static Object convertModelValue(PropertyType type, Object value) {
		switch (type) {
		case Item:
		case Category:
		case Product:
			return value != null ? ((Item)value).getId() : null;
		}
		return value;
	}

	public static SMap<Long, SMap<String, String>> parents(ItemModel itemModel, CatalogModel catalogModel, StagingArea area, OutputChannel channel, boolean includeRootCategory) {
		SMap<Long, SMap<String, String>> categories = SMap.empty();
		for (ItemModel category : itemModel.getParents()) {
			if (includeRootCategory || !category.equals(catalogModel.getRootItem())) {
				categories = categories.add(category.getItemId(), ItemUtil.getNameLabels(category, catalogModel, channel, area));
			}
		}
		return categories;
	}

	public static SMap<Long, SMap<String, String>> parentExtent(ItemModel itemModel, CatalogModel catalogModel, StagingArea area, OutputChannel channel, boolean includeSelf) {
		SMap<Long, SMap<String, String>> categories = SMap.empty();
		for (ItemModel category : parentExtent(itemModel, includeSelf)) {
			categories = categories.add(category.getItemId(), ItemUtil.getNameLabels(category, catalogModel, channel, area));
		}
		return categories;
	}
	
	public static SMap<Long, SMap<String, String>> groups(ItemModel itemModel, CatalogModel catalogModel, StagingArea area, OutputChannel channel) {
		SMap<Long, SMap<String, String>> groups = SMap.empty();
		
		for (PropertyGroup group : catalogModel.getCatalog().getPropertyGroups()) {
			groups = groups.set(group.getId(), ItemUtil.getNameLabels(group));
		}
		
		return groups;
	}
	
	public static Set<ItemModel> parentExtent(ItemModel item, boolean includeSelf) {
		Set<ItemModel> result;
		if (includeSelf) {
			result = new LinkedHashSet<ItemModel>();
			result.add(item);
			result.addAll(item.getParentExtent());
		} else {
			result = item.getParentExtent();
		}
		
		return result;
	}
	
	

	public static SMap<OutputChannel, SMap<Long, SMap<String, String>>> promotions(ItemModel productModel, CatalogModel catalogModel, StagingArea area, OutputChannel channel) {
		SMap<OutputChannel, SMap<Long, SMap<String, String>>> result = SMap.empty();

		List<VolumeDiscountPromotion> attachedPromotions = catalogModel.dao.findPromotionsForProduct(catalogModel.catalog.getId(), productModel.getItemId());
		for (VolumeDiscountPromotion promotion : attachedPromotions) {
			SMap<Long, SMap<String, String>> productsWithPromotion = result.get(promotion.getShop(), SMap.<Long, SMap<String, String>>empty());
			productsWithPromotion = productsWithPromotion.set(productModel.getItemId(), ItemUtil.getNameLabels(promotion, catalogModel, channel, area));
			result = result.set(Cloner.clone(promotion.getShop(), view), productsWithPromotion);  // TODO this clones too many times.
		}
		return result;
	}

	// TODO How to do i18n??
	private static SMap<String, String> getNameLabels(VolumeDiscountPromotion promotion, CatalogModel catalogModel, OutputChannel channel, StagingArea area) {
		return SMap.create(null, "Volume discount: " + new Money(promotion.getPrice(), promotion.getPriceCurrency()).toString() + " (min " + promotion.getVolumeDiscount() + " products)");
	}
}

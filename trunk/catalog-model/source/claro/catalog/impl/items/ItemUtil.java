package claro.catalog.impl.items;

import java.util.Map.Entry;

import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.StagingArea;
import easyenterprise.lib.util.SMap;

public class ItemUtil {
	public static SMap<String, String> getNameLabels(ItemModel item, CatalogModel catalogModel, OutputChannel outputChannel, StagingArea stagingArea) {
		SMap<String, String> result = SMap.empty();
		
		// Lookup name property
		PropertyModel nameProperty = item.findProperty(catalogModel.nameProperty.getPropertyId(), true);
		
		// get the name value for each language
		if (nameProperty != null) {
			SMap<String,Object> values = nameProperty.getValues(stagingArea, outputChannel);
			for (String language : values.getKeys()) {
				result = result.add(language, (String) values.get(language));
			}
		}
		
		return result;
	}
	

	public static SMap<PropertyInfo, SMap<String, Object>> effectivePropertyValues(ItemModel candidate, StagingArea stagingArea, OutputChannel outputChannel) {
		SMap<PropertyInfo, SMap<String, Object>> propertyValues = SMap.empty();
		for (Entry<PropertyGroupInfo, PropertyModel> property : candidate.getPropertyExtent()) {
			propertyValues = propertyValues.add(property.getValue().getPropertyInfo(), property.getValue().getEffectiveValues(stagingArea, outputChannel));
		}
		return propertyValues;
	}
	

	public static SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> propertyData(ItemModel itemModel, StagingArea area, OutputChannel channel) {
		SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> resultPropertyData = SMap.empty();
		
		// Obtain groups
		// TODO Is there no property hiding???
		
		SMap<PropertyGroupInfo, PropertyModel> propertyExtent = itemModel.getPropertyExtent();
		for (PropertyGroupInfo group : propertyExtent.getKeys()) {
			SMap<PropertyInfo, PropertyData> properties = SMap.empty();
			for (PropertyModel property : propertyExtent.getAll(group)) {
				PropertyData propertyData = new PropertyData();
				
				// fill propertyData
				propertyData.values = SMap.create(channel, property.getValues(area, channel));
				propertyData.effectiveValues = SMap.create(area, SMap.create(channel, property.getEffectiveValues(area, channel)));
				
				// TODO How to do this???
//			propertyData.importSourceValues = SMap.create(channel, property.getImportSourceValues(null)));
				
				properties = properties.add(property.getPropertyInfo(), propertyData);
			}
			resultPropertyData = resultPropertyData.add(group, properties);
			
		}
		return resultPropertyData;
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


}

package claro.catalog.impl.items;

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

}

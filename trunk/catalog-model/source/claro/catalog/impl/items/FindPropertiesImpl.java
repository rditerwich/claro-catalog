package claro.catalog.impl.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import claro.catalog.CatalogModelService;
import claro.catalog.command.items.FindProperties;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.PropertyModel;
import claro.jpa.catalog.Category;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;

public class FindPropertiesImpl extends FindProperties implements CommandImpl<FindProperties.Result> {

	private static final long serialVersionUID = 1L;

	@Override
	public Result execute() throws CommandException {

		Result result = new Result();
		result.properties = new ArrayList<PropertyInfo>();
		CatalogModel catalogModel = CatalogModelService.getCatalogModel(catalogId);
		findProperties(catalogModel.getRootItem(), result.properties);
		return result;
	}
	
	private void findProperties(ItemModel item, List<PropertyInfo> properties) {
		if (item.getEntity() instanceof Category) {
			for (Entry<PropertyGroupInfo, PropertyModel> property : item.getProperties()) {
				PropertyInfo info = property.getValue().getPropertyInfo();
				info.labels = info.labels.add("en", "some english label");
				properties.add(info);
			}
			for (ItemModel child : item.getChildren()) {
				findProperties(child, properties);
			}
		}
	}
}
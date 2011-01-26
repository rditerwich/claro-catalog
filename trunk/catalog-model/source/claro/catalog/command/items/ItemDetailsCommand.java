package claro.catalog.command.items;

import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.SMap;

public class ItemDetailsCommand implements Command<ItemDetailsCommand.Result>{

	private static final long serialVersionUID = 1L;
	
	public Long catalogId;
	public Long stagingAreaId;
	public Long outputChannelId;
	public String language;
	public Long itemId;
	public boolean includeRootCategory;
	
	public void checkValid() throws CommandValidationException {
	}
	
	public static class Result implements CommandResult {

		private static final long serialVersionUID = 1L;
		
		// The direct parents with their names per language.
		public SMap<Long, SMap<String, String>> parents;
		
		// The parent extent with their names per language.
		public SMap<Long, SMap<String, String>> parentExtent;
		
		// All groups with their names per language.
		public SMap<Long, SMap<String, String>> groups;
		
		// Property values per property, per group.
		public SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> propertyData;

		public SMap<PropertyInfo, PropertyData> danglingPropertyData;
		
	}

}

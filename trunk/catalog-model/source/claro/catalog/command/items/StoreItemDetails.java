package claro.catalog.command.items;

import static easyenterprise.lib.command.CommandValidationException.validate;
import static easyenterprise.lib.util.CollectionUtil.isEmpty;

import java.util.List;

import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.jpa.catalog.OutputChannel;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.SMap;

public class StoreItemDetails implements Command<StoreItemDetails.Result> {

	private static final long serialVersionUID = -1L;
	
	public Long catalogId;
	
	public Long stagingAreaId;
	
	public Long outputChannelId;
	

	// Item wide
	
	/** Compulsory when a new item is created, optional otherwise. */
	public ItemType itemType; 
	public Long itemId;
	public boolean remove;
	
	// parents
	public List<Long> parentsToSet;
	
	
	// Properties
	public List<PropertyInfo> propertiesToSet;
	public List<Long> propertiesToRemove;
	
	// Groups
	public SMap<PropertyInfo, PropertyGroupInfo> groupsToSet;
	public SMap<PropertyInfo, PropertyGroupInfo> groupsToRemove;
	
	// Values
	public SMap<PropertyInfo, SMap<String, Object>> valuesToSet;
	
	/**
	 * Removed values.  A value is identified by a property and a language.
	 */
	public SMap<PropertyInfo, List<String>> valuesToRemove;

	
	@Override
	public void checkValid() throws CommandValidationException {
		validate (catalogId != null);
		
		// New items require a type.
		validate (itemId != null || itemType != null);
		
		// For now, we treat item as abstract
		validate (itemType != null && itemType != ItemType.item);
		
		if (remove) {
			validate (isEmpty(valuesToSet));
			validate (isEmpty(valuesToRemove));
			validate (isEmpty(propertiesToSet));
			validate (isEmpty(propertiesToRemove));
			validate (isEmpty(groupsToSet));
			validate (isEmpty(groupsToRemove));
		}
		
		for (PropertyInfo property : CollectionUtil.notNull(propertiesToSet)) {
			// New properties require a type.
			validate(property.propertyId != null || property.getType() != null);
			
			// Make sure we are not also throwing them away:  // TODO This could arguably be correct.
			validate(!CollectionUtil.notNull(propertiesToRemove).contains(property));
		}
		
		for (PropertyInfo property : CollectionUtil.notNull(valuesToSet).getKeys()) {
			
			// Make sure we are not also throwing them away:
			validate(!CollectionUtil.notNull(propertiesToRemove).contains(property));
		}
		
		for (PropertyInfo property : CollectionUtil.notNull(groupsToSet).getKeys()) {
			
			// Make sure we are not also throwing them away:
			validate(!CollectionUtil.notNull(propertiesToRemove).contains(property));
		}
	}
	
	public static class Result implements CommandResult {
		// The direct parents with their names per language.
		public SMap<Long, SMap<String, String>> parents;
		
		// The parent extent with their names per language.
		public SMap<Long, SMap<String, String>> parentExtentWithSelf;
		
		// All groups with their names per language.
		public SMap<Long, SMap<String, String>> groups;

		// Property values per property, per group.
		public SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> propertyData;
		
		// Promotions by channel, most current first.  
		public SMap<OutputChannel, SMap<Long, SMap<String, String>>> promotions;

		private static final long serialVersionUID = 1L;
		public Long storedItemId;
		
		
		// product result
		public SMap<PropertyInfo, SMap<String, Object>> masterValues;
		
		// category results
		public SMap<String, String> categoryLabels;
	}
}

package claro.catalog.command.items;

import java.util.List;

import static easyenterprise.lib.command.CommandValidationException.validate;
import static easyenterprise.lib.util.CollectionUtil.isEmpty;

import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.SMap;

public class StoreProduct implements Command<StoreProduct.Result> {

	private static final long serialVersionUID = -1L;

	public Long productId;
	
	public boolean remove;
	
	public SMap<PropertyInfo, SMap<String, Object>> setValues;
	
	/**
	 * Removed values.  A value is identified by a property and a language.
	 */
	public SMap<PropertyInfo, List<String>> removedValues;
	
	public List<Long> addedCategories;
	
	public List<Long> removedCategories;
	
	@Override
	public void checkValid() throws CommandValidationException {
		if (remove) {
			validate (isEmpty(setValues));
			validate (isEmpty(removedValues));
			validate (isEmpty(addedCategories));
			validate (isEmpty(removedCategories));
		}
	}

	public static class Result implements CommandResult {
		private static final long serialVersionUID = 1L;
		public Long storedProductId;
		public SMap<PropertyInfo, SMap<String, Object>> masterValues;
		public SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> detailValues;
		public SMap<Long, SMap<String, String>> categories;
	}
}

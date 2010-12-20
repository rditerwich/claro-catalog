
package claro.catalog.model;

import static com.google.common.base.Objects.equal;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import claro.catalog.data.MediaValue;
import claro.catalog.data.MoneyValue;
import claro.catalog.data.PropertyInfo;
import claro.jpa.catalog.EnumValue;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.Label;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyType;
import claro.jpa.catalog.PropertyValue;
import claro.jpa.catalog.StagingArea;
import claro.jpa.importing.ImportSource;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import easyenterprise.lib.util.SMap;

/**
 * <p>Examples:<p>
 * Get the values for a specific output channel: 
 * <pre>values = propertyModel.getValues().getValue(outputChannel);</pre>
 * Prevent null checks on values:
 * <pre>values = propertyModel.getValues().getValue(outputChannel, SMap.&lt;String, Object>empty());</pre>
 * Get output-channel independent values:
 * <pre>propertyModel.getValues().getValue();</pre>
 * Gets the output-channel and language independent value:
 * <pre>Object value = propertyModel.getValues().getValue().getValue();</pre>
 *
 * @author ruud.diterwich
 */
public abstract class PropertyModel {

	private static final SMap<String, Object> emptyValues = SMap.empty();
	private static final SMap<OutputChannel, SMap<String, Object>> emptyOCValues = SMap.empty();
	private static final Object undefined = new Object();
	
	private SMap<StagingArea, SMap<OutputChannel, SMap<String, Object>>> values = SMap.empty();
	private SMap<StagingArea, SMap<OutputChannel, SMap<String, Object>>> effectiveValues = SMap.empty();
	private SMap<ImportSource, SMap<OutputChannel, SMap<String, Object>>> importSourceValues = SMap.empty();
	private final ItemModel item;

	static PropertyModel create(final PropertyModel root, ItemModel item) {
		return new PropertyModel(item) {
			PropertyModel getRoot() {
				return root; 
			}
		};
	}

	static PropertyModel createRoot(final Long propertyId, final boolean isDangling, final ItemModel item) {
		return new PropertyModel(item) {
			private PropertyInfo propertyInfo = createPropertyInfo(item, getEntity(), isDangling);
			PropertyModel getRoot() {
				return this;
			}
			@Override
			public Long getPropertyId() {
				return propertyId;
			}
			@Override
			public PropertyInfo getPropertyInfo() {
				return propertyInfo;
			}
		};			
	}
	
	public PropertyModel(ItemModel item) {
		this.item = item;
	}
	
	public Long getPropertyId() {
		return getPropertyInfo().propertyId;
	}
	
	public ItemModel getOwnerItem() {
		return getRoot().getItem();
	}
	
	public ItemModel getItem() {
		return item;
	}

	public PropertyInfo getPropertyInfo() {
		return getRoot().getPropertyInfo();
	}
	
	public boolean isDangling() {
		return getPropertyInfo().isDangling;
	}

	public Property getEntity() {
		return CatalogAccess.getDao().getProperty(getPropertyId());
	}
	
	public boolean setValue(StagingArea stagingArea, OutputChannel outputChannel, String language, Object value) {
		synchronized(getItem().catalog) {

			// Do we have a property value already?
			Item itemEntity = item.getEntity();
			Property propertyEntity = getEntity();
			PropertyValue propertyValue = CatalogAccess.getDao().getPropertyValue(stagingArea, outputChannel, itemEntity, propertyEntity, language);
			if (propertyValue != null) {
				
				// Are we changing anything?
				Object existingValue = getTypedValue(propertyValue);
				if (Objects.equal(value, existingValue)) {
					return false;

				}
				
				// Set the actual value
				setTypedValue(propertyValue, value);
			} else {
				
				// No candidate found, create a new one
				propertyValue = new PropertyValue();
				itemEntity.getPropertyValues().add(propertyValue);
				propertyValue.setItem(itemEntity);
				
				propertyValue.setProperty(propertyEntity);
				propertyValue.setStagingArea(stagingArea);
				propertyValue.setOutputChannel(outputChannel);
				
				// Set the actual value.
				setTypedValue(propertyValue, value);
				
				CatalogAccess.getDao().getEntityManager().persist(propertyValue);
			}
			
			return true;
		}
	}
	
	/**
	 * @return values or empty SMap
	 */
	public SMap<String, Object> getValues(StagingArea stagingArea, OutputChannel outputChannel) {
		synchronized (getItem().catalog) {
			SMap<OutputChannel, SMap<String, Object>> ocValues = values.get(stagingArea, emptyOCValues);
			SMap<String, Object> langValues = ocValues.get(outputChannel);
			if (langValues == null) {
				langValues = SMap.empty();
				for (PropertyValue value : propertyValues) {
					if (equal(stagingArea, value.getStagingArea()) && equal(outputChannel, value.getOutputChannel()) && value.getImportSource() == null) {
						langValues = langValues.add(value.getLanguage(), getTypedValue(value));
					}
				}
				ocValues.set(outputChannel, langValues);
				values.set(stagingArea, ocValues);
			}
			return langValues;
		}
	}
	
	/**
	 * 
	 * @return Effective values or empty SMap
	 */
	public SMap<String, Object> getEffectiveValues(StagingArea stagingArea, OutputChannel outputChannel) {
		synchronized (getItem().catalog) {
				
			SMap<OutputChannel, SMap<String, Object>> ocValues = effectiveValues.get(stagingArea, emptyOCValues);
			SMap<String, Object> langValues = ocValues.get(outputChannel);
			if (langValues == null) {
				langValues = SMap.empty();
				
				// calculate effective value for each output channel, language combination
				EffectiveValueHelper helper = new EffectiveValueHelper(stagingArea, propertyValues);
				for (String language : helper.getLanguages()) {

					// find best value in this item
					Object effectiveValue = helper.getBestValue(outputChannel, language, undefined);
					
					// look in parent items, first one wins, parents are ordered
					if (effectiveValue == undefined) {
						for (ItemModel parent : getItem().getParents()) {
							PropertyModel property = parent.findProperty(getPropertyId(), false);
							if (property != null) {
								effectiveValue = property.getEffectiveValues(stagingArea, outputChannel).get(language, undefined);
								if (effectiveValue != undefined) break;
							}
						}
					}
					// found effective value? 
					if (effectiveValue != undefined) {
						langValues = langValues.add(language, effectiveValue);
					}
				}
				ocValues = ocValues.set(outputChannel, langValues);
				effectiveValues = effectiveValues.set(stagingArea, ocValues);
			}
			return langValues;
		}
	}

	/**
	 * @return Import source values or the empty map
	 */
	public SMap<OutputChannel, SMap<String, Object>> getImportSourceValues(ImportSource importSource) {
		synchronized (getItem().catalog) {
			SMap<OutputChannel, SMap<String, Object>> ocValues = importSourceValues.get(importSource);
			if (ocValues == null) {
				ocValues = SMap.empty();
				for (PropertyValue value : propertyValues) {
					if (equal(value.getImportSource(), importSource) && value.getStagingArea() == null) {
						SMap<String, Object> langValues = ocValues.get(value.getOutputChannel(), emptyValues);
						langValues = langValues.add(value.getLanguage(), getTypedValue(value));
						ocValues = ocValues.set(value.getOutputChannel(), langValues);
					}
				}
				importSourceValues.set(importSource, ocValues);
			}
			return ocValues;
		}
	}

	public static Set<Property> getEntities(Collection<PropertyModel> properties) {
		Set<Property> result = new LinkedHashSet<Property>();
		for (PropertyModel property : properties) {
			result.add(property.getEntity());
		}
		return result;
	}

	abstract PropertyModel getRoot();

	private Iterable<PropertyValue> propertyValues = new Iterable<PropertyValue>() {
		public Iterator<PropertyValue> iterator() {
			return Iterators.filter(getItem().getEntity().getPropertyValues().iterator(), new Predicate<PropertyValue>() {
				public boolean apply(PropertyValue input) {
	        return input.getProperty() != null && equal(input.getProperty().getId(), getPropertyId());
        }
			});
		}
	};
	
	
	static void setTypedValue(PropertyValue propertyValue, Object value) {
		switch(propertyValue.getProperty().getType()) {
		// TODO add actual cases...
		default:
			propertyValue.setStringValue(value.toString());
			
		}
	}


	
	static Object getTypedValue(PropertyValue value) {
		switch (value.getProperty().getType()) {
		case String: return value.getStringValue();
		case Media: return new MediaValue(value.getId(), value.getMimeType(), value.getStringValue());
		case Money: return new MoneyValue(value.getMoneyValue(), value.getMoneyCurrency());
		}
		return null;
	}
	
	private static PropertyInfo createPropertyInfo(ItemModel ownerItem, Property entity, boolean isDangling) {
		PropertyInfo info = new PropertyInfo(); 
		info.ownerItemId = ownerItem.getItemId();
		info.propertyId = entity.getId();
		info.type = entity.getType();
		info.isMany = entity.getIsMany() != null ? entity.getIsMany() : false;
		info.isDangling = isDangling;
		for (Label label : entity.getLabels()) {
			info.labels = info.labels.add(label.getLanguage(), label.getLabel());
		}
		if (entity.getType() == PropertyType.Enum) {
			for (EnumValue enumValue : entity.getEnumValues()) {
				SMap<String,String> enumLabels = SMap.empty();
				for (Label label : enumValue.getLabels()) {
					enumLabels = enumLabels.add(label.getLanguage(), label.getLabel());
				}
				info.enumValues = info.enumValues.add(enumValue.getValue(), enumLabels);
			}
		}
		return info;
	}
}

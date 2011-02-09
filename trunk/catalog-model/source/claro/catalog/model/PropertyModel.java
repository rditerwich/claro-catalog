
package claro.catalog.model;

import static com.google.common.base.Objects.equal;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import claro.catalog.data.MediaValue;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.jpa.catalog.EnumValue;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.Label;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyType;
import claro.jpa.catalog.PropertyValue;
import claro.jpa.catalog.Source;
import claro.jpa.catalog.StagingArea;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import easyenterprise.lib.util.Money;
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
	private SMap<Source, SMap<OutputChannel, SMap<String, Object>>> sourceValues = SMap.empty();
	private final ItemModel item;

	static PropertyModel create(final PropertyModel root, ItemModel item, final Long groupAssignmentItemId) {
		return new PropertyModel(item) {
			PropertyModel getRoot() {
				return root; 
			}
			@Override
			public Long getGroupAssignmentItemId() {
				return groupAssignmentItemId;
			}
		};
	}

	static PropertyModel createRoot(final Long propertyId, final boolean isDangling, final ItemModel item, final Long groupAssignmentItemId) {
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
			@Override
			public Long getGroupAssignmentItemId() {
				return groupAssignmentItemId;
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
	
	public abstract Long getGroupAssignmentItemId();
	
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
		return item.catalog.dao.getProperty(getPropertyId());
	}
	
	public void setValue(Source source, StagingArea stagingArea, OutputChannel outputChannel, String language, Object value) {
		if (source != null && stagingArea != null) throw new IllegalArgumentException("Can't have both a source and a staging area");
		synchronized(getItem().catalog) {

			// Do we have a property value already?
			Item itemEntity = item.getEntity();
			Property propertyEntity = getEntity();
			PropertyValue propertyValue = item.catalog.dao.getPropertyValue(itemEntity, propertyEntity, source, stagingArea, outputChannel, language);
			if (propertyValue != null) {
				
				// Are we changing anything?
				Object existingValue = getTypedValue(propertyValue);
				if (!Objects.equal(value, existingValue)) {
					
					// Set the actual value
					setTypedValue(propertyValue, value);
					
					// Item has changed:
					item.invalidateChildExtent(true);
				}
			} else {
				
				// No candidate found, create a new one
				propertyValue = new PropertyValue();
				itemEntity.getPropertyValues().add(propertyValue);
				propertyValue.setItem(itemEntity);
				
				propertyValue.setProperty(propertyEntity);
				propertyValue.setSource(source);
				propertyValue.setStagingArea(stagingArea);
				propertyValue.setOutputChannel(outputChannel);
				propertyValue.setLanguage(language);
				
				// Set the actual value.
				setTypedValue(propertyValue, value);
				
				item.catalog.dao.getEntityManager().persist(propertyValue);
				
				// Item has changed:
				item.invalidateChildExtent(true);
			}
		}
	}
	
	public void removeValue(Source source, StagingArea stagingArea, OutputChannel outputChannel, String language) {
		synchronized(getItem().catalog) {

			// Do we have a property value already?
			Item itemEntity = item.getEntity();
			Property propertyEntity = getEntity();
			PropertyValue propertyValue = item.catalog.dao.getPropertyValue(itemEntity, propertyEntity, source, stagingArea, outputChannel, language);
			if (propertyValue != null) {
				item.catalog.dao.getEntityManager().remove(propertyValue);
				// Item has changed:
				item.invalidateChildExtent(true);
			}
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
					if (equal(stagingArea, value.getStagingArea()) && equal(outputChannel, value.getOutputChannel()) && value.getSource() == null) {
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
				for (String language : item.catalog.getAllLanguages()) {

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
	public SMap<OutputChannel, SMap<String, Object>> getSourceValues(Source source) {
		synchronized (getItem().catalog) {
			SMap<OutputChannel, SMap<String, Object>> ocValues = sourceValues.get(source);
			if (ocValues == null) {
				ocValues = SMap.empty();
				for (PropertyValue value : propertyValues) {
					if (equal(value.getSource(), source) && value.getStagingArea() == null) {
						SMap<String, Object> langValues = ocValues.get(value.getOutputChannel(), emptyValues);
						langValues = langValues.add(value.getLanguage(), getTypedValue(value));
						ocValues = ocValues.set(value.getOutputChannel(), langValues);
					}
				}
				sourceValues.set(source, ocValues);
			}
			return ocValues;
		}
	}

	public static Set<Property> getEntities(SMap<PropertyGroupInfo, PropertyModel> properties) {
		Set<Property> result = new LinkedHashSet<Property>();
		for (Entry<PropertyGroupInfo, PropertyModel> property : properties) {
			result.add(property.getValue().getEntity());
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
		case Media: 
			if (value instanceof MediaValue) {
				MediaValue mediaValue = (MediaValue) value;
				propertyValue.setMimeType(mediaValue.mimeType);
				propertyValue.setStringValue(mediaValue.filename);
				propertyValue.setMediaValue(mediaValue.content);
			} else if (value == null) {
				propertyValue.setMimeType(null);
				propertyValue.setStringValue(null);
				propertyValue.setMediaValue(null);
			}
			else {
				throw new IllegalArgumentException("Property " + propertyValue.getProperty() + " can only be set using a MediaValue");
			}
			break;
		case Money:
			if (value instanceof Money) {
				Money moneyValue = (Money) value;
				propertyValue.setMoneyValue(moneyValue.value);
				propertyValue.setMoneyCurrency(moneyValue.currency);
			} else if (value == null) {
				propertyValue.setMoneyValue(null);
				propertyValue.setMoneyCurrency(null);
			} else {
				throw new IllegalArgumentException("Property " + propertyValue.getProperty() + " can only be set using a MoneyValue");
			}
			break;
		case Boolean: propertyValue.setBooleanValue((Boolean) value); break;
		case Enum: propertyValue.setEnumValue((Integer) value); break;
		case Integer: propertyValue.setIntegerValue((Integer) value); break;
		case Item: propertyValue.setItemValue((Item) value); break;
		case Real: propertyValue.setRealValue((Double) value);break;
			
		// TODO add other non-string values
		default:
			propertyValue.setStringValue(value != null? value.toString() : null);
		}
	}


	
	// TODO map all non-string values
	static Object getTypedValue(PropertyValue value) {
		switch (value.getProperty().getType()) {
		case Media: return MediaValue.create(value.getId(), value.getMimeType(), value.getStringValue());
		case Money: return new Money(value.getMoneyValue(), value.getMoneyCurrency());
		case Boolean: return value.getBooleanValue();
		case Enum: return value.getEnumValue();
		case Integer: return value.getIntegerValue();
		case Item: return  value.getItemValue();
		case Real: return value.getRealValue();
		default: return value.getStringValue();
		}
	}
	
	private static PropertyInfo createPropertyInfo(ItemModel ownerItem, Property entity, boolean isDangling) {
		PropertyInfo info = new PropertyInfo(); 
		info.ownerItemId = ownerItem.getItemId();
		info.propertyId = entity.getId();
		info.setType(entity.getType());
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

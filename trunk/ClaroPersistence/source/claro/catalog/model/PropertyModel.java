
package claro.catalog.model;

import static com.google.common.base.Objects.equal;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import claro.catalog.data.PropertyInfo;
import claro.jpa.catalog.Alternate;
import claro.jpa.catalog.EnumValue;
import claro.jpa.catalog.ImportSource;
import claro.jpa.catalog.Label;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyType;
import claro.jpa.catalog.PropertyValue;

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
	
	private SMap<Alternate, SMap<OutputChannel, SMap<String, Object>>> values = SMap.empty();
	private SMap<Alternate, SMap<OutputChannel, SMap<String, Object>>> effectiveValues = SMap.empty();
	private SMap<ImportSource, SMap<OutputChannel, SMap<String, Object>>> importSourceValues = SMap.empty();
	private final ItemModel item;

	static PropertyModel create(final PropertyModel root, ItemModel item) {
		return new PropertyModel(item) {
			PropertyModel getRoot() {
				return root; 
			}
		};
	}

	static PropertyModel createRoot(final Long propertyId, final boolean isDangling, ItemModel item) {
		return new PropertyModel(item) {
			private PropertyInfo propertyInfo = createPropertyInfo(getItem(), getEntity(), isDangling);
			PropertyModel getRoot() {
				return this;
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
	
	/**
	 * @return values or empty SMap
	 */
	public SMap<String, Object> getValues(Alternate alternate, OutputChannel outputChannel) {
		synchronized (getItem().catalog) {
			SMap<OutputChannel, SMap<String, Object>> ocValues = values.getValue(alternate, emptyOCValues);
			SMap<String, Object> langValues = ocValues.getValue(outputChannel);
			if (langValues == null) {
				langValues = SMap.empty();
				for (PropertyValue value : propertyValues) {
					if (equal(alternate, value.getAlternate()) && equal(outputChannel, value.getOutputChannel()) && value.getImportSource() == null) {
						langValues = langValues.add(value.getLanguage(), getTypedValue(value));
					}
				}
				ocValues.set(outputChannel, langValues);
				values.set(alternate, ocValues);
			}
			return langValues;
		}
	}
	
	/**
	 * 
	 * @return Effective values or empty SMap
	 */
	public SMap<String, Object> getEffectiveValues(Alternate alternate, OutputChannel outputChannel) {
		synchronized (getItem().catalog) {
				
			SMap<OutputChannel, SMap<String, Object>> ocValues = effectiveValues.getValue(alternate, emptyOCValues);
			SMap<String, Object> langValues = ocValues.getValue(outputChannel);
			if (langValues == null) {
				langValues = SMap.empty();
				
				// calculate effective value for each output channel, language combination
				EffectiveValueHelper helper = new EffectiveValueHelper(alternate, propertyValues);
				for (String language : helper.getLanguages()) {

					// find best value in this item
					Object effectiveValue = helper.getBestValue(outputChannel, language, undefined);
					
					// look in parent items, first one wins, parents are ordered
					if (effectiveValue == undefined) {
						for (ItemModel parent : getItem().getParents()) {
							PropertyModel property = parent.findProperty(getPropertyId());
							if (property != null) {
								effectiveValue = property.getEffectiveValues(alternate, outputChannel).getValue(language, undefined);
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
				effectiveValues = effectiveValues.set(alternate, ocValues);
			}
			return langValues;
		}
	}

	/**
	 * @return Import source values or the empty map
	 */
	public SMap<OutputChannel, SMap<String, Object>> getImportSourceValues(ImportSource importSource) {
		synchronized (getItem().catalog) {
			SMap<OutputChannel, SMap<String, Object>> ocValues = importSourceValues.getValue(importSource);
			if (ocValues == null) {
				ocValues = SMap.empty();
				for (PropertyValue value : propertyValues) {
					if (equal(value.getImportSource(), importSource) && value.getAlternate() == null) {
						SMap<String, Object> langValues = ocValues.getValue(value.getOutputChannel(), emptyValues);
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
	
	static Object getTypedValue(PropertyValue value) {
		switch (value.getProperty().getType()) {
		case String: return value.getStringValue(); 
		}
		return null;
	}
	
	private static PropertyInfo createPropertyInfo(ItemModel ownerItem, Property entity, boolean isDangling) {
		PropertyInfo info = new PropertyInfo(); 
		info.ownerItemId = ownerItem.getItemId();
		info.propertyId = entity.getId();
		info.type = entity.getType();
		info.isMany = entity.getIsMany();
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

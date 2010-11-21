
package claro.catalog.model;

import static com.google.common.base.Objects.equal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import claro.jpa.catalog.Alternate;
import claro.jpa.catalog.ImportSource;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyValue;

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
public class PropertyModel {

	public static final SMap<String, Object> emptyValues = SMap.empty();
	private static final Object undefined = new Object();
	
	final ItemModel item;
	final ItemModel ownerItem;
	final Long propertyId;
	private SMap<ImportSource, SMap<String, Object>> importSourceValues;
	private SMap<OutputChannel, SMap<String, Object>> values;
	private SMap<OutputChannel, SMap<String, Object>> effectiveValues;

	public PropertyModel(ItemModel item, ItemModel ownerItem, Long propertyId) {
		this.item = item;
		this.ownerItem = ownerItem;
		this.propertyId = propertyId;
  }
	
	public Long getPropertyId() {
		return propertyId;
	}
	
	public ItemModel getItem() {
	  return item;
  }
	
	public ItemModel getOwnerItem() {
	  return ownerItem;
  }

	public Property getEntity() {
		return CatalogAccess.getDao().getProperty(propertyId);
	}
	
	public boolean isDangling() {
		return ownerItem == null;
	}

	/**
	 * Returns the values for this property. These are the values that are maintained by
	 * users of the catalog manager, they are not imported, nor are they alternates (like
	 * from another stage/supplier).
	 * <p>The values are grouped by output channel. The 'null' output channel denotes
	 * the output-channel independent values. These are the most 'normal' values in the catalog.  
	 * 
	 * @return
	 */
	public SMap<OutputChannel, SMap<String, Object>> getValues() {
		synchronized (item.catalog) {
			if (values == null) {
				values = SMap.empty();
				values.add(null, SMap.<String, Object>empty());
				for (PropertyValue value : propertyValues) {
					if (value.getImportSource() == null && value.getAlternate() == null) {
						SMap<String, Object> osValues = values.getValue(value.getOutputChannel());
						osValues = osValues.add(value.getLanguage(), getTypedValue(value));
						values = values.add(value.getOutputChannel(), osValues);
					}
				}
			}
			return values;
		}
	}
	
	/**
	 * Returns the effective values of this property. This is the calculated value
	 * taking language overrides, import source priorities, item inheritance into
	 * account. This value is calculated for each combination of output channel and 
	 * language.
	 * 
	 * <p>The result is grouped by output channel. The 'null' output channel denotes
	 * the output-channel independent values.
	 * 
	 * @return
	 */
	public SMap<OutputChannel, SMap<String, Object>> getEffectiveValues() {
		synchronized (item.catalog) {
			if (effectiveValues == null) {
				effectiveValues = SMap.empty();
				
				// determine all output channels, make sure to include the null output channel
				Set<OutputChannel> outputChannels = new HashSet<OutputChannel>(item.getUsedOutputChannel());
				outputChannels.add(null);
				
				// determine all languages, make sure to include the null language
				Set<String> languages = new HashSet<String>(item.getUsedLanguages());
				languages.add(null);

				// calculate effective value for each output channel, language combination
				EffectiveValueHelper helper = new EffectiveValueHelper(propertyValues);
				for (OutputChannel outputChannel : outputChannels) {
					SMap<String, Object> outputChannelValues = emptyValues;
					for (String language : languages) {
						// try (outputChannel, language)
						Object effectiveValue = helper.getBestValue(outputChannel, language, undefined);
						// try (null, language)
						if (effectiveValue == undefined && outputChannel != null) {
							effectiveValue = helper.getBestValue(null, language, undefined);
						}
						// try (outputChannel, null)
						if (effectiveValue == undefined && language != null) {
							effectiveValue = helper.getBestValue(outputChannel, null, undefined);
							// try (null, null)
							if (effectiveValue == undefined && outputChannel != null) {
								effectiveValue = helper.getBestValue(null, null, undefined);
							}
						}
						// look in parent items, first one wins, parents are ordered
						if (effectiveValue == undefined) {
							for (ItemModel parent : item.getParents()) {
								PropertyModel property = parent.findProperty(propertyId);
								if (property != null) {
									effectiveValue = property.getEffectiveValues().getValue(outputChannel, emptyValues).getValue(language, undefined);
									if (effectiveValue != undefined) break;
								}
							}
						}
						// found effective value? 
						if (effectiveValue != undefined) {
							outputChannelValues = outputChannelValues.add(language, effectiveValue);
						}
					}
					effectiveValues = effectiveValues.add(outputChannel, outputChannelValues);
				}
			}
			return effectiveValues;
		}
	}

	/**
	 * Imported sources are ordered by priority, highest first. For example, result.getValues() 
	 * returns the values for the import source with the highest priority.  
	 * @return
	 */
	public SMap<ImportSource, SMap<String, Object>> getImportSourceValues() {
		synchronized (item.catalog) {
			if (importSourceValues == null) {
				importSourceValues = SMap.empty();
				Map<ImportSource, SMap<String, Object>> valuesByImportSource = new TreeMap<ImportSource, SMap<String,Object>>(ImportSourceComparator.instance); 
				for (PropertyValue value : propertyValues) {
					ImportSource importSource = value.getImportSource();
					if (importSource != null) {
						SMap<String, Object> map = valuesByImportSource.get(importSource);
						if (map == null) {
							map = SMap.empty();
						}
						map = map.add(value.getLanguage(), getTypedValue(value));
						valuesByImportSource.put(importSource, map);
					}
				}
				importSourceValues = importSourceValues.addAll(valuesByImportSource);
			}
			return importSourceValues;
		}
	}
	
	public static Set<Property> getEntities(Collection<PropertyModel> properties) {
		Set<Property> result = new LinkedHashSet<Property>();
		for (PropertyModel property : properties) {
			result.add(property.getEntity());
		}
		return result;
	}

	@SuppressWarnings("serial")
  private static class EffectiveValueHelper extends TreeMap<ImportSource, List<PropertyValue>> {
		public EffectiveValueHelper(Iterable<PropertyValue> propertyValues) {
			for (PropertyValue value : propertyValues) {
				if (value.getAlternate() == null) {
					ImportSource importSource = value.getImportSource();
					List<PropertyValue> values = get(importSource);
					if (values == null) {
						values = new ArrayList<PropertyValue>();
						put(importSource, values);
					}
					values.add(value);
				}
			}
    }
		public Object getBestValue(OutputChannel outputChannel, String language, Object defaultValue) {
			for (List<PropertyValue> importValues : values()) {
				for (PropertyValue importValue : importValues) {
					if (equal(outputChannel, importValue.getOutputChannel()) && equal(language, importValue.getLanguage())) {
						return getTypedValue(importValue);
					}
				}
			}
			return defaultValue;
		}
	}

	private Iterable<PropertyValue> propertyValues = new Iterable<PropertyValue>() {
		public Iterator<PropertyValue> iterator() {
			return Iterators.filter(item.getEntity().getPropertyValues().iterator(), new Predicate<PropertyValue>() {
				public boolean apply(PropertyValue input) {
	        return input.getProperty() != null && equal(input.getProperty().getId(), propertyId);
        }
			});
		}
	};
	
	private static Object getTypedValue(PropertyValue value) {
		switch (value.getProperty().getType()) {
		case String: return value.getStringValue(); 
		}
		return null;
	}
}

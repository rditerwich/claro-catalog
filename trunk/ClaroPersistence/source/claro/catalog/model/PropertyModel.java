
package claro.catalog.model;

import static com.google.common.base.Objects.equal;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import claro.jpa.catalog.Alternate;
import claro.jpa.catalog.ImportSource;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Property;
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

	public abstract Long getPropertyId();
	public abstract ItemModel getItem();
	public abstract ItemModel getOwnerItem();
	public abstract SMap<String, String> getLabels();
	public abstract SMap<Integer, SMap<String, String>> getEnumValues();

	public Property getEntity() {
		return CatalogAccess.getDao().getProperty(getPropertyId());
	}
	
	public boolean isDangling() {
		return getOwnerItem() == null;
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
}

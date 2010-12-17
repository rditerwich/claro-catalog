package claro.catalog.model;

import static com.google.common.base.Objects.equal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import claro.jpa.catalog.ImportSource;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.PropertyValue;
import claro.jpa.catalog.StagingArea;

@SuppressWarnings("serial")
class EffectiveValueHelper extends TreeMap<ImportSource, List<PropertyValue>> {
	
	private static final Object undefined = new Object();

	Set<String> languages = new HashSet<String>();
	
	public EffectiveValueHelper(StagingArea stagingArea, Iterable<PropertyValue> propertyValues) {
		super(ImportSourceComparator.instance);
		for (PropertyValue value : propertyValues) {
			languages.add(value.getLanguage());
			if (equal(stagingArea, value.getStagingArea())) {
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
	public Set<String> getLanguages() {
		return languages;
	}

	public Object getBestValue(OutputChannel outputChannel, String language, Object defaultValue) {
		// try (outputChannel, language)
		Object effectiveValue = getBestValue2(outputChannel, language);
		// try (null, language)
		if (effectiveValue == undefined && outputChannel != null) {
			effectiveValue = getBestValue2(null, language);
		}
		// try (outputChannel, null)
		if (effectiveValue == undefined && language != null) {
			effectiveValue = getBestValue2(outputChannel, null);
			// try (null, null)
			if (effectiveValue == undefined && outputChannel != null) {
				effectiveValue = getBestValue2(null, null);
			}
		}
		if (effectiveValue == undefined) {
			return defaultValue;
		}
		return effectiveValue;
	}
	
	private Object getBestValue2(OutputChannel outputChannel, String language) {
		for (List<PropertyValue> importValues : values()) {
			for (PropertyValue importValue : importValues) {
				if (equal(outputChannel, importValue.getOutputChannel()) && equal(language, importValue.getLanguage())) {
					return PropertyModel.getTypedValue(importValue);
				}
			}
		}
		return undefined;
	}
}

package claro.catalog.util;

import static com.google.common.base.Objects.equal;

import java.util.Collection;

import claro.jpa.catalog.Label;
import claro.jpa.catalog.Property;

import com.google.common.base.Objects;

public class CatalogModelUtil {
	public static Label find(Collection<Label> labels, String label, String language) {
		for (Label candidate : labels) {
			if (equal(language, candidate.getLanguage()) && equal(label, candidate.getLabel())) {
				return candidate;
			}
		}
		
		return null;
	}

	public static String propertyLabel(Property property, String language, String defaultValue) {
		return label(property.getLabels(), language, defaultValue);
	}
	
	public static String label(Collection<Label> labels, String language, String defaultValue) {
		for (Label label : labels) {
			if (Objects.equal(label.getLanguage(), language)) {
				return label.getLabel();
			}
		}
		if (language != null) {
			return label(labels, null, defaultValue);
		}
		return defaultValue;
	}
}

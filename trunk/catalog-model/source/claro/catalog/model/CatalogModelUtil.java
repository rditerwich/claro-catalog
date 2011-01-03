package claro.catalog.model;

import java.util.Collection;

import com.google.common.base.Objects;

import static com.google.common.base.Objects.equal;

import claro.jpa.catalog.Label;
import claro.jpa.catalog.Property;


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
		for (Label label : property.getLabels()) {
			if (Objects.equal(label.getLanguage(), language)) {
				return label.getLabel();
			}
		}
		return defaultValue;
	}
}

package claro.catalog.util;

import static com.google.common.base.Objects.equal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
	
	public static List<String> splitLanguages(String languages) {
		if (languages == null || languages.trim().equals("")) {
			return Collections.emptyList();
		}
		
		List<String> result = new ArrayList<String>();
		for (String language : languages.split(",")) {
			if (language != null && !language.trim().equals("")) {
				result.add(language);
			}
		}
		return result;
	}
	
	/**
	 * Merge the given language,comma separated.  Strips null or empty languages.  If no language remains, null is returned.
	 * @param languages
	 * @return
	 */
	public static String mergeLanguages(Iterable<String> languages) {
		StringBuilder result = new StringBuilder();
		
		String sep = "";
		for (String language : languages) {
			if (language != null && !language.trim().equals("")) {
				result.append(sep); sep = ",";
				result.append(language);
			}
		}
		
		if (result.length() != 0) {
			return result.toString();
		} else {
			return null;
		}
	}
}

package claro.catalog.model;

import java.util.Collection;

import static com.google.common.base.Objects.equal;

import claro.jpa.catalog.Label;


public class CatalogModelUtil {
	public static Label find(Collection<Label> labels, String label, String language) {
		for (Label candidate : labels) {
			if (equal(language, candidate.getLanguage()) && equal(label, candidate.getLabel())) {
				return candidate;
			}
		}
		
		return null;
	}
}

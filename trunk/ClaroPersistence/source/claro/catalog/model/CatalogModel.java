package claro.catalog.model;

import java.util.List;

import claro.jpa.catalog.Alternate;
import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.ImportSource;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.OutputChannel;

public class CatalogModel {

	/**
	 * Finds items in the catalog
	 * Only return items that are visible in outputChannel, when set.
	 * Only items are returned that have at least a single property value  (for any language).
	 * @param catalog Catalog to search in
	 * @param outputChannel Output channel filter, or null
	 * @param searchString String to search for, or the empty string
	 * @param searchLanguage Language to search in, or null. Should be null when searchString is empty
	 * @param ImportSource Import source filter, or null
	 * @param alternate Alternate filter, or null
	 * @throws NullPointerException when searchString == null
	 * @return
	 */
	public List<Item> findItems(Catalog catalog, OutputChannel outputChannel, String searchString, String searchLanguage, ImportSource ImportSource, Alternate alternate) {
		return null;
	}
}

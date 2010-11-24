package claro.catalog;

import java.util.List;

import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.ImportSource;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.StagingArea;

public class CatalogService {

	/**
	 * Finds items in the catalog
	 * Only return items that are visible in outputChannel, when set.
	 * Only items are returned that have at least a single property value  (for any language).
	 * @param catalog Catalog to search in
	 * @param outputChannel Output channel filter, or null
	 * @param searchString String to search for, or the empty string
	 * @param searchLanguage Language to search in, or null. Should be null when searchString is empty
	 * @param ImportSource Import source filter, or null
	 * @param StagingArea stagingArea filter, or null
	 * @throws NullPointerException when searchString == null
	 * @return
	 */
	public List<Item> findItems(Catalog catalog, OutputChannel outputChannel, String searchString, String searchLanguage, ImportSource ImportSource, StagingArea stagingArea) {
		return null;
	}
	
	public Item getItemDetails(Catalog catalog, OutputChannel outputChannel, String language, ImportSource importSource, StagingArea stagingArea) {
		return null;
		
	}
}

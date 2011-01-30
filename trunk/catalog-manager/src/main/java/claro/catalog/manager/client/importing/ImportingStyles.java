package claro.catalog.manager.client.importing;

import claro.catalog.manager.client.CatalogManagerResources;

import com.google.gwt.resources.client.CssResource;

public interface ImportingStyles extends CssResource {
	
	public static final ImportingStyles instance = CatalogManagerResources.instance.importingStyles();
	
	@ClassName("ImportLogPanel") String importLogPanel();
	@ClassName("ImportMainPanel") String importMainPanel();
	@ClassName("statusTable") String statusTable();
	@ClassName("logText") String logText();
}

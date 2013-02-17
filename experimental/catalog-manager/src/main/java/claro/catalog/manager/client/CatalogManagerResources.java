package claro.catalog.manager.client;

import claro.catalog.manager.client.importing.ImportingStyles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface CatalogManagerResources extends ClientBundle {

	CatalogManagerResources instance = GWT.create(CatalogManagerResources.class);
	
	@Source("GlobalStyles.css")
	GlobalStyles globalStyles();
	
	@Source("importing/ImportingStyles.css")
	ImportingStyles importingStyles();
	
	
}

package claro.catalog.manager.client;

import claro.catalog.manager.client.i18n.I18NCatalog;
import claro.catalog.manager.client.images.Images;

import com.google.gwt.core.client.GWT;

public interface Globals {

	I18NCatalog messages = GWT.create(I18NCatalog.class);
	ResourceBundle deprecatedImages = GWT.create(ResourceBundle.class);
	Images images = GWT.create(Images.class);
}

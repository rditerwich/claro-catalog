package claro.catalog.manager.client;

import claro.catalog.manager.client.i18n.I18NCatalog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.UIObject;

public class Util {
	public final static I18NCatalog i18n = GWT.create(I18NCatalog.class);
	
	public static void add(UIObject uiObject, Styles style) {
		uiObject.addStyleName(style.toString());
	}

}

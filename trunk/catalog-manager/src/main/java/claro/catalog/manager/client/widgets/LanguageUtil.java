package claro.catalog.manager.client.widgets;

import com.google.gwt.i18n.client.LocaleInfo;

public class LanguageUtil {
	public static String displayName(String language) {
		String displayName = LocaleInfo.getLocaleNativeDisplayName(language);
		if (displayName == null) {
			displayName = language;
		}
		return displayName;
	}

}

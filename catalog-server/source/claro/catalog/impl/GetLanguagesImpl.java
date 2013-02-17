package claro.catalog.impl;

import java.util.Locale;

import claro.catalog.command.GetLanguages;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.util.SMap;

public class GetLanguagesImpl extends GetLanguages implements CommandImpl<GetLanguages.Result>{

	private static final long serialVersionUID = 1L;

	@Override
	public Result execute() throws CommandException {
		checkValid();
		
		Result result = new Result();
		
		result.languages = SMap.empty();
		Locale defaultLocale = Locale.getDefault();
		for (Locale locale : Locale.getAvailableLocales()) {
			System.out.println("Adding locale: " + locale);
			SMap<String, String> displayNames = SMap.empty();
			displayNames = displayNames.add(null, locale.getDisplayName(defaultLocale));
			for (Locale displayLocale : Locale.getAvailableLocales()) {
				String displayLocaleName = displayLocale.toString();
				String displayName = locale.getDisplayName(displayLocale);
				System.out.println("  language: " + displayLocaleName + " display: " + displayName);
				displayNames = displayNames.add(displayLocaleName, displayName);
			}
			result.languages = result.languages.add(locale.toString(), displayNames);
		}
	
		return result;
	}
}

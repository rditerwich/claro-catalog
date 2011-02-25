package claro.catalog.manager.client.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.util.Tuple;

public class LanguagesWidget extends SelectionWidget<String> {
	
	enum Styles implements Style { mouseOverStyle, languageStyle, languageName, languageAddNoSelection, languageAdd, languageList }
	
	private PopupPanel addLanguagePanel;

	public LanguagesWidget(boolean multiSelect) {
		super(false, multiSelect);
	}
	

	public String displayName(String language) {
		if (language == null) {
			return messages.defaultLanguageName();
		}
		return LanguageUtil.displayName(language);
	}

	@Override
	protected String getAddToSelectionLabel() {
		return isMultiSelect() ? messages.addLanguageLink() : messages.setLanguageLink();
	}
	

	@Override
	protected void addSelectionClicked(Widget addWidget) {
		if (addLanguagePanel == null) {
			addLanguagePanel = new PopupPanel(true) {{
				StyleUtil.addStyle(this, Styles.languageList);
				setWidget(new ScrollPanel(new Label(messages.loading())) {{
					setPixelSize(200, 400);
				}});
			}};
		}
		
		// Update languages.
		String[] availableLocaleNames = LocaleInfo.getAvailableLocaleNames();
		Widget panelWidget = createLanguageSelection(availableLocaleNames);
		if (panelWidget == null) {
			panelWidget = new Label(messages.noLanguagesAvailable());
		}
		((ScrollPanel)addLanguagePanel.getWidget()).setWidget(panelWidget);
		
		addLanguagePanel.showRelativeTo(addWidget);
	}


	private Widget createLanguageSelection(final String[] allLanguages) {
		if (allLanguages == null || allLanguages.length == 0) {
			return null;
		}
		
		final List<Tuple<String,String>> newLanguages = new ArrayList<Tuple<String,String>>();
		for (int i = 0; i < allLanguages.length; i++) {
			if (!getSelection().contains(allLanguages[i]) && !allLanguages[i].equals("default")) {
				// DisplayName first to be able to sort.
				newLanguages.add(Tuple.create(displayName(allLanguages[i]), allLanguages[i]));
			}
		}
		
		Collections.sort(newLanguages);
		
		return new Grid(newLanguages.size(), 1) {{
			// Fill it
			for (int i = 0; i < newLanguages.size(); i++) {
				System.out.println("Lang: " + newLanguages.get(i));
				setWidget(i, 0,  new Label(newLanguages.get(i).getFirst()));
			}
			
			// Selections:
			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					Cell cell = getCellForEvent(event);
					if (cell != null) {
						Tuple<String, String> selectedLanguage = newLanguages.get(cell.getRowIndex());
						if (!getSelection().contains(selectedLanguage.getSecond())) {
							addSelectedObject(selectedLanguage.getSecond());
							addLanguagePanel.hide();
						}
					}
				}
			});
		}};
	}
}

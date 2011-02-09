package claro.catalog.manager.client.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import claro.catalog.manager.client.Globals;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.util.Tuple;

public class LanguagesWidget extends Composite implements Globals {
	
	enum Styles implements Style { mouseOverStyle, languageStyle, languageName, languageAdd, languageList }
	
	private FlowPanel mainPanel;
	private PopupPanel addCategoryPanel;
	

	private final boolean canSelect;
	private Set<String> languages = new TreeSet<String>();
	private final boolean allowMultiple;
	

	public LanguagesWidget() {
		this(true, true);
	}
	
	public LanguagesWidget(boolean canSelect, boolean allowMultiple) {
		this.canSelect = canSelect;
		this.allowMultiple = allowMultiple;
		initWidget(mainPanel = new FlowPanel() {{
			StyleUtil.addStyle(this, Styles.languageStyle);
		}});
	}
	
	public void setData(List<String> languages) {
		
		if (languages != null) {
			this.languages.addAll(languages);
		} else {
			 this.languages.clear();
		}
		
		render();
	}
	
	public Set<String> getLanguages() {
		return languages;
	}
	
	private void render() {
		mainPanel.clear();
		int i = 0;
		for (String language : languages) {
			final boolean lastCategory = i++ == languages.size() - 1;
			final String lang = language;
			final String languageName = LanguageUtil.displayName(language);
			mainPanel.add(new Grid(1, lastCategory && allowMultiple ? 3 : 2) {{
				setWidget(0, 0, new Anchor(languageName) {{
					StyleUtil.addStyle(this, Styles.languageName);
					setTitle(getLanguageTooltip(languageName));
					if (canSelect) {
						addHoverStyles(this);
						addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								languageClicked(lang);
							}
						});
					}
				}});
				setWidget(0, 1, new Anchor("X") {{
					addHoverStyles(this);
					setTitle(getRemoveLanguageTooltip(languageName));
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							removeLanguage(lang);
							languagesChanged();
						}
					});
				}});
				if (lastCategory && allowMultiple) {
					setWidget(0, 2, createAddAnchor("+"));
				}
			}});
		}
		if (languages.isEmpty()) {
			mainPanel.add(createAddAnchor(getAddLanguageLabel()));
		}
	}

	protected String getAddLanguageLabel() {
		return allowMultiple ? messages.addLanguageLink() : messages.setLanguageLink();
	}

	private Anchor createAddAnchor(String addCategoryText) {
		return new Anchor(addCategoryText) {{ // TODO Use image instead?
			StyleUtil.addStyle(this, Styles.languageAdd);
			setTitle(getAddLanguageTooltip());
			addHoverStyles(this);
			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					if (addCategoryPanel == null) {
						addCategoryPanel = new PopupPanel(true) {{
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
					((ScrollPanel)addCategoryPanel.getWidget()).setWidget(panelWidget);
					
					addCategoryPanel.showRelativeTo((Widget) event.getSource());
				}
			});
			
			// TODO add show on mouse over.
		}};
	}
	
	protected String getLanguageTooltip(String language) {
		return "";
	}
	
	protected String getRemoveLanguageTooltip(String language) {
		return "";
	}
	
	protected String getAddLanguageTooltip() {
		return "";
	}
	
	protected void languageClicked(String language) {
		
	}
	
	protected void addLanguage(String language) {
		languages.add(language);
		render();
	}

	protected void removeLanguage(String language) {
		languages.remove(language);
		render();
	}

	protected void languagesChanged() {
	}

	
	private Widget createLanguageSelection(final String[] allLanguages) {
		if (allLanguages == null || allLanguages.length == 0) {
			return null;
		}
		
		final List<Tuple<String,String>> newLanguages = new ArrayList<Tuple<String,String>>();
		for (int i = 0; i < allLanguages.length; i++) {
			if (!languages.contains(allLanguages[i]) && !allLanguages[i].equals("default")) {
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
						if (!languages.contains(selectedLanguage.getSecond())) {
							languagesChanged();
							addLanguage(selectedLanguage.getSecond());
							addCategoryPanel.hide();
						}
					}
				}
			});
		}};
	}

	private String displayName(final String language) {
		if (language == null) {
			return messages.defaultLanguageName();
		}
		return LanguageUtil.displayName(language);
	}
	
	private void addHoverStyles(final Anchor anchor) {
		anchor.addMouseOverHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				StyleUtil.addStyle(anchor, Styles.mouseOverStyle);
			}
		});
		anchor.addMouseOutHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				StyleUtil.remove(anchor, Styles.mouseOverStyle);
			}
		});
	}
}

package claro.catalog.manager.client.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import claro.catalog.command.GetLanguagesByShop;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.command.StatusCallback;
import claro.jpa.shop.Shop;

import com.google.common.base.Objects;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.util.SMap;
import easyenterprise.lib.util.Tuple;

public class LanguageAndShopSelector extends Composite implements Globals {
	private static final String INDENT = "&nbsp;&nbsp;&nbsp;";
	private Shop selectedShop;
	private String selectedLanguage;
	List<Tuple<Shop, String>> shopsAndLanguages = new ArrayList<Tuple<Shop,String>>();
	
	private ListBox listBox;

	public LanguageAndShopSelector() {
		initWidget(listBox = new ListBox() {{
			addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					updateSelection(listBox.getSelectedIndex());
				}
			});
		}});
		
		refreshData();
	}

	public void refreshData() {
		GetLanguagesByShop cmd = new GetLanguagesByShop();
		cmd.catalogId = CatalogManager.getCurrentCatalogId();
		
		GwtCommandFacade.executeCached(cmd, 1000 * 60 * 60 , new StatusCallback<GetLanguagesByShop.Result>(messages.loadingWebShopsAction()) {
			public void onSuccess(GetLanguagesByShop.Result result) {
				super.onSuccess(result);
				updateLanguages(result.languages);
			}
		});
	}
	
	public String getSelectedLanguage() {
		return selectedLanguage;
	}
	
	public Shop getSelectedShop() {
		return selectedShop;
	}
	
	protected void selectionChanged() {
		
	}
	
	private void updateSelection(int selectedIndex) {
		Shop oldShop = selectedShop;
		String oldLanguage = selectedLanguage;
		if (selectedIndex >= 0) {
			selectedShop = shopsAndLanguages.get(selectedIndex).getFirst();
			selectedLanguage = shopsAndLanguages.get(selectedIndex).getSecond();
		} else {
			selectedShop = null;
			selectedLanguage = null;
		}
		
		if (listBox.getSelectedIndex() != selectedIndex) {
			listBox.setSelectedIndex(selectedIndex);
		}
		
		if (!Objects.equal(oldShop, selectedShop) || !Objects.equal(oldLanguage, selectedLanguage)) {
			selectionChanged();
		}
	}
	
	private void updateLanguages(SMap<Shop, String> languagesByShop) {
		listBox.clear();
		shopsAndLanguages.clear();
		for (Entry<Shop, String> shopLanguage : languagesByShop) {
			shopsAndLanguages.add(Tuple.create(shopLanguage.getKey(), shopLanguage.getValue()));
		}
		
		int i = 0;
		int selectedIndex = -1;
		StringBuilder innerHtml = new StringBuilder();
		for (Tuple<Shop, String> shopLanguage : shopsAndLanguages) {
			innerHtml.append("<option>");
			addDisplayName(shopLanguage, innerHtml);
			innerHtml.append("</option>");
			
			if (Objects.equal(shopLanguage.getFirst(), selectedShop) && Objects.equal(shopLanguage.getSecond(), selectedLanguage)) {
				selectedIndex = i;
			}
			i++;
		}

		listBox.getElement().setInnerHTML(innerHtml.toString());
		
		// Update selection
		updateSelection(selectedIndex);
	}
	
	private void addDisplayName(Tuple<Shop, String> shopLanguage, StringBuilder result) {
		
		// Indentation
		if (shopLanguage.getFirst() != null && shopLanguage.getSecond() != null) {
			result.append(INDENT).append(INDENT);
		} else if (shopLanguage.getFirst() != null) {
			result.append(INDENT);
		}
		
		// displayname
		if (shopLanguage.getSecond() != null) {
			result.append(LanguageUtil.displayName(shopLanguage.getSecond()));
		} else if (shopLanguage.getFirst() != null) {
			result.append(shopLanguage.getFirst().getName());
		} else {
			// both null
			result.append(messages.defaultLanguageName());  
		}
	}

}


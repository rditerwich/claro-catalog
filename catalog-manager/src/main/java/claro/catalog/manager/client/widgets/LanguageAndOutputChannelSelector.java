package claro.catalog.manager.client.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import claro.catalog.command.GetLanguagesByOutputChannel;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.command.StatusCallback;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.shop.Shop;

import com.google.common.base.Objects;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.util.SMap;
import easyenterprise.lib.util.Tuple;

public class LanguageAndOutputChannelSelector extends Composite implements Globals {
	private static final String INDENT = "&nbsp;&nbsp;&nbsp;";
	private OutputChannel selectedOutputChannel;
	private String selectedLanguage;
	List<Tuple<OutputChannel, String>> channelsAndLanguages = new ArrayList<Tuple<OutputChannel,String>>();
	
	private ListBox listBox;

	public LanguageAndOutputChannelSelector() {
		initWidget(listBox = new ListBox() {{
			addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					updateSelection(listBox.getSelectedIndex());
				}
			});
			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					event.stopPropagation();  // Prevent detail screens from closing.  TODO Maybe somehow do this outside of this widget?
				}
			});
		}});
		
		refreshData();
	}

	public void refreshData() {
		GetLanguagesByOutputChannel cmd = new GetLanguagesByOutputChannel();
		cmd.catalogId = CatalogManager.getCurrentCatalogId();
		
		GwtCommandFacade.executeCached(cmd, 1000 * 60 * 60 , new StatusCallback<GetLanguagesByOutputChannel.Result>(messages.loadingOutputChannelsMessage()) {
			public void onSuccess(GetLanguagesByOutputChannel.Result result) {
				super.onSuccess(result);
				updateLanguages(result.languages);
			}
		});
	}
	
	public String getSelectedLanguage() {
		return selectedLanguage;
	}
	
	public OutputChannel getSelectedOutputChannel() {
		return selectedOutputChannel;
	}
	
	protected void selectionChanged() {
		
	}
	
	private void updateSelection(int selectedIndex) {
	  OutputChannel oldShop = selectedOutputChannel;
		String oldLanguage = selectedLanguage;
		if (selectedIndex >= 0) {
			selectedOutputChannel = channelsAndLanguages.get(selectedIndex).getFirst();
			selectedLanguage = channelsAndLanguages.get(selectedIndex).getSecond();
		} else {
			selectedOutputChannel = null;
			selectedLanguage = null;
		}
		
		if (listBox.getSelectedIndex() != selectedIndex) {
			listBox.setSelectedIndex(selectedIndex);
		}
		
		if (!Objects.equal(oldShop, selectedOutputChannel) || !Objects.equal(oldLanguage, selectedLanguage)) {
			selectionChanged();
		}
	}
	
	private void updateLanguages(SMap<OutputChannel, String> languagesByOutputChannel) {
		listBox.clear();
		channelsAndLanguages.clear();
		for (Entry<OutputChannel, String> shopLanguage : languagesByOutputChannel) {
			channelsAndLanguages.add(Tuple.create(shopLanguage.getKey(), shopLanguage.getValue()));
		}
		
		int i = 0;
		int selectedIndex = -1;
		StringBuilder innerHtml = new StringBuilder();
		for (Tuple<OutputChannel, String> shopLanguage : channelsAndLanguages) {
			innerHtml.append("<option>");
			addDisplayName(shopLanguage, innerHtml);
			innerHtml.append("</option>");
			
			if (Objects.equal(shopLanguage.getFirst(), selectedOutputChannel) && Objects.equal(shopLanguage.getSecond(), selectedLanguage)) {
				selectedIndex = i;
			}
			i++;
		}

		listBox.getElement().setInnerHTML(innerHtml.toString());
		
		// Update selection
		updateSelection(selectedIndex);
	}
	
	private void addDisplayName(Tuple<OutputChannel, String> channelAndLanguage, StringBuilder result) {
		
		// Indentation
		if (channelAndLanguage.getFirst() != null && channelAndLanguage.getSecond() != null) {
			result.append(INDENT).append(INDENT);
		} else if (channelAndLanguage.getFirst() != null) {
			result.append(INDENT);
		}
		
		// displayname
		if (channelAndLanguage.getSecond() != null) {
			result.append(LanguageUtil.displayName(channelAndLanguage.getSecond()));
		} else if (channelAndLanguage.getFirst() != null) {
			result.append(channelAndLanguage.getFirst().getName());
		} else {
			// both null
			result.append(messages.defaultLanguageName());  
		}
	}

}


package claro.catalog.manager.client.webshop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import claro.catalog.command.shop.StoreShop;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.ConfirmationDialog;
import claro.catalog.manager.client.widgets.FormTable;
import claro.catalog.manager.client.widgets.LanguagesWidget;
import claro.jpa.catalog.Language;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.gwt.client.widgets.Header;

public class WebshopDetail extends Composite implements Globals {
	
	private final ShopModel model;
	
	private Header header;
	private TextBox nameTextBox;
	private TextBox urlPrefixTextBox;
	private LanguagesWidget defaultLanguageWidget;
	private LanguagesWidget languagesWidget;

	private ConfirmationDialog removeWithConfirmation = new ConfirmationDialog(images.removeIcon()) {
		protected String getMessage() {
			if (model != null && model.getShop() != null) {
				return messages.removeShopConfirmationMessage(model.getShop().getName());
			} else {
				return "";
			}
		};
		protected void yesPressed() {
			StoreShop command = new StoreShop(model.getShop());
			command.removeShop = true;
			model.store(command);
			model.renderAll();
		}
	};
	


	public WebshopDetail(ShopModel model_) {
		this.model = model_;
		initWidget(new VerticalPanel() {{
			setStylePrimaryName("WebshopDetail");
			add(new Grid(1, 2) {{
				setWidget(0, 0, header = new Header(1, "") {{
				}});
				setWidget(0, 1, new Anchor(messages.removeShopLink()) {{
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							removeWithConfirmation.show();
						}
					});
				}});
			}});
			add(new FormTable() {{
				add(messages.nameLabel(), nameTextBox = new TextBox(), messages.shopNameHelp());
				add(messages.shopUrlPrefixLabel(), urlPrefixTextBox = new TextBox(), messages.urlPrefixHelp());
				add(messages.defaultlanguageLabel(), defaultLanguageWidget = new LanguagesWidget(false, false) {
					protected void addLanguage(String language) {
						super.addLanguage(language);
						doStoreShop();
					}
				}, messages.defaultLanguageHelp());
				add(messages.shopLanguagesLabel(), languagesWidget = new LanguagesWidget(false, true) {
					protected void addLanguage(String language) {
						super.addLanguage(language);
						doStoreShop();
					}
				}, messages.shopLanguagesHelp());
			}});		
		}});
		
		ValueChangeHandler<?> changeHandler = new ValueChangeHandler<Object>() {
			public void onValueChange(ValueChangeEvent<Object> event) {
				doStoreShop();
			}
		};
		nameTextBox.addValueChangeHandler((ValueChangeHandler<String>) changeHandler);
		urlPrefixTextBox.addValueChangeHandler((ValueChangeHandler<String>) changeHandler);
	}

	public void render() {
		removeWithConfirmation.hide();
		header.setText(model.getShop().getName());
		
		nameTextBox.setText(model.getShop().getName());
		urlPrefixTextBox.setText(model.getShop().getUrlPrefix());
		defaultLanguageWidget.setData(model.getShop().getDefaultLanguage() != null? Collections.singletonList(model.getShop().getDefaultLanguage()) : null); // TODO Actually set default language.
		setSelectedLanguages(model.getShop().getLanguages()); 
	}
	

	private void doStoreShop() {
		model.getShop().setName(nameTextBox.getText());
		model.getShop().setUrlPrefix(urlPrefixTextBox.getText());
		model.getShop().setDefaultLanguage(getSelectedDefaultLanguage());
		model.getShop().setLanguages(getSelectedLanguages());
		
		model.store(new StoreShop(model.getShop()));
	}
	
	private void setSelectedLanguages(List<Language> languages) {
		List<String> selectedLanguages = new ArrayList<String>();
		
		for (Language language : languages) {
			selectedLanguages.add(language.getName());
		}
		
		languagesWidget.setData(selectedLanguages);
	}
	
	private List<Language> getSelectedLanguages() {
		List<Language> result = new ArrayList<Language>();
		
		for (String language : languagesWidget.getLanguages()) {
			Language lang = new Language();
			lang.setName(language);
			result.add(lang);
		}
		
		return result;
	}

	private String getSelectedDefaultLanguage() {
		if (!defaultLanguageWidget.getLanguages().isEmpty()) {
			return defaultLanguageWidget.getLanguages().get(0);
		}
		return null;
	}
}


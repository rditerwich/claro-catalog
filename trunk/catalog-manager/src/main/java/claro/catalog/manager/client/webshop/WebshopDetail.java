package claro.catalog.manager.client.webshop;

import static easyenterprise.lib.util.CollectionUtil.firstOrNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import claro.catalog.command.shop.StoreShop;
import claro.catalog.data.ItemType;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.ConfirmationDialog;
import claro.catalog.manager.client.widgets.FormTable;
import claro.catalog.manager.client.widgets.ItemSelectionWidget;
import claro.catalog.manager.client.widgets.LanguagesWidget;
import claro.catalog.util.CatalogModelUtil;
import claro.jpa.catalog.Category;
import claro.jpa.shop.Navigation;

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
	private ItemSelectionWidget categoriesWidget;

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
	


	@SuppressWarnings("unchecked")
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
					@Override
					protected void languagesChanged() {
						doStoreShop();
					}
				}, messages.defaultLanguageHelp());
				add(messages.shopLanguagesLabel(), languagesWidget = new LanguagesWidget(false, true) {
					protected void addLanguage(String language) {
						super.addLanguage(language);
						doStoreShop();
					}
				}, messages.shopLanguagesHelp());
				add(messages.topLevelCategoriesLabel(), categoriesWidget = new ItemSelectionWidget(true, ItemType.category, true) {
					@Override
					protected void selectionChanged() {
						doStoreShop();
					}
				}, messages.topLevelCategoriesHelp());
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
		List<Category> categories = new ArrayList<Category>();
		for (Navigation nav : model.getShop().getNavigation()) {
			if (nav.getCategory() != null) {
				categories.add(nav.getCategory());
			}
		}
		categoriesWidget.setData(categories, CatalogManager.getUiLanguage());
	}
	

	private void doStoreShop() {
		model.getShop().setName(nameTextBox.getText());
		model.getShop().setUrlPrefix(urlPrefixTextBox.getText());
		model.getShop().setDefaultLanguage(getSelectedDefaultLanguage());
		model.getShop().setLanguages(CatalogModelUtil.mergeLanguages(languagesWidget.getLanguages()));
		
		StoreShop command = new StoreShop(model.getShop());
		command.topLevelCategoryIds = new ArrayList<Long>(categoriesWidget.getSelection());
		model.store(command);
	}
	
	private void setSelectedLanguages(String languages) {
		List<String> selectedLanguages = new ArrayList<String>();
		
		for (String language : CatalogModelUtil.splitLanguages(languages)) {
			selectedLanguages.add(language);
		}
		
		languagesWidget.setData(selectedLanguages);
	}
	
	private String getSelectedDefaultLanguage() {
		return firstOrNull(defaultLanguageWidget.getLanguages());
	}
}


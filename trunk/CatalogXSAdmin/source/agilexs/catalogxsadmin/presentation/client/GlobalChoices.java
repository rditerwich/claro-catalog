package agilexs.catalogxsadmin.presentation.client;

import java.util.Collections;
import java.util.List;

import agilexs.catalogxsadmin.presentation.client.binding.BindingConverter;
import agilexs.catalogxsadmin.presentation.client.binding.BindingEvent;
import agilexs.catalogxsadmin.presentation.client.binding.BindingListener;
import agilexs.catalogxsadmin.presentation.client.binding.ListBoxBinding;
import agilexs.catalogxsadmin.presentation.client.binding.ListPropertyBinding;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.Catalog;
import agilexs.catalogxsadmin.presentation.client.catalog.CatalogBinding;
import agilexs.catalogxsadmin.presentation.client.catalog.Language;
import agilexs.catalogxsadmin.presentation.client.catalog.LanguageBinding;
import agilexs.catalogxsadmin.presentation.client.catalog.OutputChannel;
import agilexs.catalogxsadmin.presentation.client.catalog.OutputChannelBinding;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Shop;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;


public class GlobalChoices extends Composite {
	private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);
	
	private ListPropertyBinding<Catalog> catalogsBinding = new ListPropertyBinding<Catalog>() {
		private List<Catalog> data = Collections.emptyList();

		protected List<Catalog> doGetData() {
			if (CatalogServiceAsync.findAllCatalogsIsValidCached(data)) {
				return data;
			} 
			
			CatalogServiceAsync.findAllCatalogsCached(new AsyncCallback<List<Catalog>>() {
				public void onSuccess(List<Catalog> result) {
					setData(result);
					if (!result.isEmpty()) {
						
						// Update current catalog:
						Catalog currentCatalog = currentCatalogBinding.getData();
						if (!result.contains(currentCatalog)) {
							currentCatalog = result.get(0);
							currentCatalogBinding.setData(currentCatalog);
						}

						// update current channel:
						if (!currentCatalog.getOutputChannels().isEmpty()) {
							OutputChannel currentChannel = currentChannelBinding.getData();
							if (!currentCatalog.getOutputChannels().contains(currentChannel)) {
								currentChannel = currentCatalog.getOutputChannels().get(0);
								currentChannelBinding.setData(currentChannel);
							}
						} else {
							currentChannelBinding.setData(null);
						}
					
						// update current language:
						if (!currentCatalog.getLanguages().isEmpty()) {
							Language currentLanguage = currentLanguageBinding.getData();
							if (!currentCatalog.getLanguages().contains(currentLanguage)) {
								currentLanguage = currentCatalog.getLanguages().get(0);
								currentLanguageBinding.setData(currentLanguage);
							}
						} else {
							currentLanguageBinding.setData(null);
						}
					} else {
						currentCatalogBinding.setData(null);
						// Create a default catalog:
						CatalogServiceAsync.getOrCreateCatalog("Catalog", new AsyncCallback<Catalog>() {
							public void onFailure(Throwable caught) {
							}

							public void onSuccess(Catalog result) {
								CatalogServiceAsync.findAllCatalogsFlushCache(false);
								doGetData(); // Reinitialize Global Decisions.
							}
						});
					}
				}
				
				public void onFailure(Throwable caught) {
					// TODO Error handling!
				}
			});
			return data;
		}
		
		protected void doSetData(List<Catalog> data) {
			this.data = data;
		}
	};
	
	private CatalogBinding<Catalog> currentCatalogBinding = new CatalogBinding<Catalog>() {
		{
			catalogsBinding.addBindingListener(new BindingListener() {
				public void onBindingChangeEvent(BindingEvent event) {
					System.out.println("hallo");
				}
			});
		}
		protected Catalog doGetData() {
			Catalog currentCatalog = CatalogCache.get().getCurrentCatalog();
			return currentCatalog;
		}
		protected void doSetData(Catalog data) {
			CatalogCache.get().setCurrentCatalog(data);
		}
	};
	
	private OutputChannelBinding<OutputChannel> currentChannelBinding = new OutputChannelBinding<OutputChannel>() {
		protected OutputChannel doGetData() {
			return CatalogCache.get().getCurrentShop();
		}
		protected void doSetData(OutputChannel data) {
			// TODO Shop vs outputchannel
			CatalogCache.get().setCurrentShop((Shop) data);
		}
	};
	
	private LanguageBinding<Language> currentLanguageBinding = new LanguageBinding<Language>() {
		protected Language doGetData() {
			return CatalogCache.get().getCurrentLanguage();
		}
		protected void doSetData(Language data) {
			CatalogCache.get().setCurrentLanguage(data);
		}
	};
	
	private static final BindingConverter<Catalog, String> catalogConverter = new BindingConverter<Catalog, String>() {
		public Catalog convertFrom(String data) {
			assert false : "not supported";
			return null;
		}
		public String convertTo(Catalog data) {
			return data.getName();
		}
	};
	
	private static final BindingConverter<OutputChannel, String> channelConverter = new BindingConverter<OutputChannel, String>() {
		public OutputChannel convertFrom(String data) {
			assert false : "not supported";
		return null;
		}
		public String convertTo(OutputChannel data) {
			return data.getName();
		}
	};
	
	private static final BindingConverter<Language, String> languageConverter = new BindingConverter<Language, String>() {
		public Language convertFrom(String data) {
			assert false : "not supported";
		return null;
		}
		public String convertTo(Language data) {
			return data.getDisplayName();
		}
	};
	
	public GlobalChoices() {
		// Widget tree
		VerticalPanel mainPanel = new VerticalPanel();
			Grid grid = new Grid(3, 2);
			grid.setWidget(0, 0, new Label(i18n.catalog()));
				ListBox catalogListbox = new ListBox();
			grid.setWidget(0, 1, catalogListbox);
			grid.setWidget(1, 0, new Label(i18n.shop()));
				ListBox shopListbox = new ListBox();
			grid.setWidget(1, 1, shopListbox);
			grid.setWidget(2, 0, new Label(i18n.language()));
				ListBox languageListbox = new ListBox();
			grid.setWidget(2, 1, languageListbox);
		mainPanel.add(grid);
		mainPanel.addStyleName(Styles.choices.toString());
		
		initWidget(mainPanel);
		
		ListBoxBinding.bind(catalogListbox, catalogsBinding, currentCatalogBinding, catalogConverter);
		ListBoxBinding.bind(shopListbox, currentCatalogBinding.outputChannels(), currentChannelBinding, channelConverter);
		ListBoxBinding.bind(languageListbox, currentCatalogBinding.languages(), currentLanguageBinding, languageConverter);
		
		// TODO Is there a better way?
		catalogsBinding.setData(Collections.<Catalog>emptyList());
	}
}

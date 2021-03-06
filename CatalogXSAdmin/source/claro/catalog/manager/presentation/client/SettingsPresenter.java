package claro.catalog.manager.presentation.client;

import java.util.List;

import claro.catalog.manager.presentation.client.catalog.Catalog;
import claro.catalog.manager.presentation.client.catalog.Language;
import claro.catalog.manager.presentation.client.services.CatalogServiceAsync;

import claro.catalog.manager.presentation.client.Util.DeleteHandler;
import claro.catalog.manager.presentation.client.cache.CatalogCache;
import claro.catalog.manager.presentation.client.i18n.I18NCatalogXS;
import claro.catalog.manager.presentation.client.page.Presenter;
import claro.catalog.manager.presentation.client.widget.StatusMessage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SettingsPresenter implements Presenter<SettingsView> {

	private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

	private final SettingsView view;
	private final Catalog oldCatalog = new Catalog();
	private final Catalog catalog = new Catalog();

	private final ItemParentsView languageView;
	private List<Language> allLanguages;
	private final AsyncCallback<Catalog> saveCallback = new AsyncCallback<Catalog>() {
		@Override
		public void onFailure(Throwable caught) {
			// StatusMessage.get().show(caught.getMessage());
			// TODO Auto-generated method stub
		}

		@Override
		public void onSuccess(Catalog result) {
			StatusMessage.get().show(i18n.languagesSaved());
			if (result != null) {
				CatalogCache.get().getCurrentCatalog().setLanguages(
						((Catalog) result).getLanguages());
			}
		}
	};

	public SettingsPresenter() {
		languageView = new ItemParentsView();
		// catalog.setLanguages(new ArrayList<Language>());
		view = new SettingsView(languageView);
		languageView.buttonAddHasClickHandlers().addClickHandler(
				new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						final String newName = languageView
								.getAllParentsListBox().getValue(
										languageView.getSelectedNewParent());

						for (Language lang : allLanguages) {
							if (lang.getName().equals(newName)) {
								lang.setId(null);
								for (Language lng : CatalogCache.get()
										.getCurrentCatalog().getLanguages()) {
									if (lng.getName().equals(newName)) {
										lang.setId(lng.getId());
										break;
									}
								}
								catalog.getLanguages().add(lang);
								break;
							}
						}
						show();
					}
				});
		languageView.setDeleteHandler(new DeleteHandler<Integer>() {
			@Override
			public void onDelete(Integer index) {
				catalog.getLanguages().remove(index.intValue());

				show();
			}
		});
		view.buttonSaveHasClickHandlers().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				oldCatalog.setId(catalog.getId());
				oldCatalog.setLanguages(CatalogCache.get().getCurrentCatalog()
						.getLanguages());
				CatalogServiceAsync.updateCatalog(oldCatalog, catalog,
						saveCallback);
			}
		});
	}

	@Override
	public SettingsView getView() {
		return view;
	}

	public void show() {
		catalog.setId(CatalogCache.get().getCurrentCatalog().getId());
		if (catalog.getLanguages().isEmpty()) {
			catalog.getLanguages().addAll(
					CatalogCache.get().getCurrentCatalog().getLanguages());
		}
		languageView.clearParentTable();
		for (Language lang : catalog.getLanguages()) {
			languageView.addParentToList(lang.getDisplayName());
		}
		if (allLanguages == null) {
			view.showLoading(true);
			// load initially.
			CatalogServiceAsync
					.getAllLanguages(new AsyncCallback<List<Language>>() {
						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
						}

						@Override
						public void onSuccess(List<Language> result) {
							if (result != null) {
								allLanguages = result;
								languageView.getAllParentsListBox().clear();
								for (Language lang : result) {
									languageView.getAllParentsListBox()
											.addItem(lang.getDisplayName(),
													lang.getName());
								}
								show();
							}
						}
					});
		} else {
			view.showLoading(false);
		}
	}
}

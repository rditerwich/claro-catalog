package claro.catalog.manager.presentation.client;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import claro.catalog.manager.presentation.client.catalog.Category;

import claro.catalog.manager.presentation.client.Util.AddHandler;
import claro.catalog.manager.presentation.client.Util.DeleteHandler;
import claro.catalog.manager.presentation.client.cache.CatalogCache;
import claro.catalog.manager.presentation.client.page.Presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class ItemParentsPresenter implements Presenter<ItemParentsView> {

	private ItemParentsView view;
	private final Set<Long> currentParents = new LinkedHashSet<Long>();
	private final List<Map.Entry<Long, String>> allPossibleParents = new ArrayList<Map.Entry<Long, String>>();
	private String currentLang = "en";
	private DeleteHandler<Long> deleteHandler;
	private AddHandler<Long> addHandler;

	public ItemParentsPresenter(final ItemParentsView view) {
		this.view = view;
		view.buttonAddHasClickHandlers().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final Long newPG = Long.valueOf(view.getAllParentsListBox().getValue(view.getSelectedNewParent()));

				currentParents.add(newPG);
				allPossibleParents.remove(CatalogCache.get().getCategoryNameEntry(newPG, currentLang));
				show(currentLang);
				if (addHandler != null) {
					addHandler.onAdd(newPG);
				}
			}
		});
		view.setDeleteHandler(new DeleteHandler<Integer>() {
			@Override
			public void onDelete(Integer index) {
				final boolean removed = currentParents.remove(index.longValue());

				if (removed) {
					allPossibleParents.add(CatalogCache.get().getCategoryNameEntry(index.longValue(), currentLang));
				}
				show(currentLang);
				if (deleteHandler != null) {
					deleteHandler.onDelete(index.longValue());
				}
			}
		});
	}

	public void setAddHandler(AddHandler<Long> addHandler) {
		this.addHandler = addHandler;
	}

	public void setDeleteHandler(DeleteHandler<Long> deleteHandler) {
		this.deleteHandler = deleteHandler;
	}

	public Set<Long> getValues() {
		return currentParents;
	}

	public void show(Category productGroup,
			List<Map.Entry<Long, String>> parents, String lang,
			ArrayList<Map.Entry<Long, String>> allParents) {
		currentLang = lang;
		currentParents.clear();
		for (Entry<Long, String> pg : parents) {
			currentParents.add(pg.getKey());
		}
		allPossibleParents.clear();
		allPossibleParents.addAll(allParents);
		if (productGroup != null) {
			allPossibleParents.remove(CatalogCache.get().getCategoryNameEntry(productGroup.getId(), currentLang));
		}
		for (Long pg : currentParents) {
			allPossibleParents.remove(CatalogCache.get().getCategoryNameEntry(pg, lang));
		}
		show(lang);
	}

	private void show(String lang) {
		currentLang = lang;
		view.clearParentTable();
		for (Long pg : currentParents) {
			view.addParentToList(CatalogCache.get().getCategoryNameEntry(pg, lang).getValue());
		}
		view.getAllParentsListBox().clear();
		for (Map.Entry<Long, String> pp : allPossibleParents) {
			view.getAllParentsListBox()
					.addItem(pp.getValue(), pp.getKey() + "");
		}
	}

	@Override
	public ItemParentsView getView() {
		return view;
	}
}

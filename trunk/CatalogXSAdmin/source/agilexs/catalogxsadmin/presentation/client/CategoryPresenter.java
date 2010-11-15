package agilexs.catalogxsadmin.presentation.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agilexs.catalogxsadmin.presentation.client.Util.AddHandler;
import agilexs.catalogxsadmin.presentation.client.Util.DeleteHandler;
import agilexs.catalogxsadmin.presentation.client.cache.CatalogCache;
import agilexs.catalogxsadmin.presentation.client.catalog.Category;
import agilexs.catalogxsadmin.presentation.client.catalog.Item;
import agilexs.catalogxsadmin.presentation.client.catalog.Language;
import agilexs.catalogxsadmin.presentation.client.catalog.ParentChild;
import agilexs.catalogxsadmin.presentation.client.catalog.Property;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.Presenter;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Shop;
import agilexs.catalogxsadmin.presentation.client.widget.StatusMessage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Presenter for the Category page
 */
public class CategoryPresenter implements Presenter<CategoryView> {

	private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

	private final CategoryView view = new CategoryView();
	private ItemParentsPresenter parentsP = new ItemParentsPresenter(
			new ItemParentsView());
	private ItemPropertiesPresenter pgpp;
	private String currentLanguage = "en";
	private Category currentCategory;
	private Category originalCategory;
	private final ArrayList<ItemValuesPresenter> valuesPresenters = new ArrayList<ItemValuesPresenter>();

	public CategoryPresenter() {
		pgpp = new ItemPropertiesPresenter(currentLanguage);
		parentsP.setDeleteHandler(new DeleteHandler<Long>() {
			@Override
			public void onDelete(Long data) {
				for (ParentChild parent : currentCategory.getParents()) {
					if (data.equals(parent.getParent().getId())) {
						currentCategory.getParents().remove(parent);
						break;
					}
				}
			}
		});
		parentsP.setAddHandler(new AddHandler<Long>() {
			@Override
			public void onAdd(Long pid) {
				boolean present = false;

				for (ParentChild parent : currentCategory.getParents()) {
					if (pid.equals(parent.getParent().getId())) {
						present = true;
					}
				}
				if (!present) {
					// Create an empty product group, so only this group is send
					// to the
					// server which will then not be checked for changes.
					final Category pg = new Category();

					// TODO Parentchild changed, the following may not be correct.
					pg.setId(pid);
					ParentChild newParentChild = new ParentChild();
					newParentChild.setParent(pg);
					newParentChild.setChild(currentCategory);
					currentCategory.getParents().add(newParentChild);
				}
			}
		});
		view.containsProductsClickHandlers().addClickHandler(
				new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (currentCategory != null) {
							currentCategory.setContainsProducts(view
									.containsProducts().getValue());
						}
					}
				});
		view.saveButtonClickHandlers().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				save();
			}
		});
	}

	public Category setNewCategory(Shop shop, Category parent) {
		originalCategory = null;
		final Category newPG = new Category();
		newPG.setCatalog(shop.getCatalog());
		newPG.setPropertyValues(new ArrayList<PropertyValue>());
		newPG.setProperties(new ArrayList<Property>());
		newPG.setContainsProducts(Boolean.FALSE);
		
		addParent(newPG, parent);
		
		view.containsProducts().setValue(newPG.getContainsProducts());
		show(newPG);
		return newPG;
	}
	
	private void addParent(Item item, Item newParent) {
		if (item.getParents() == null && newParent != null) {
			item.setParents(new ArrayList<ParentChild>());
		}
		
		if (newParent != null) {
			ParentChild newParentChild = new ParentChild();
			newParentChild.setParent(newParent);
			newParentChild.setChild(item);
			newParentChild.setIndex(-1);
			// TODO, how to set the index?  par?

			item.getParents().add(newParentChild);
		}
	}

	@Override
	public CategoryView getView() {
		return view;
	}

	private void save() {
		view.setSaving(true);
		if (originalCategory != null) {
			originalCategory.getChildren().clear();
			originalCategory.setProperties(Util.filterEmpty(originalCategory.getProperties()));
			originalCategory.setPropertyValues(Util.filterEmpty(originalCategory.getPropertyValues()));
		}
		final Category saveCategory = currentCategory.clone(new HashMap());

		saveCategory.getChildren().clear();
		saveCategory.setProperties(Util.filterEmpty(currentCategory.getProperties()));
		for (Property p : saveCategory.getProperties()) {
			if (currentCategory.equals(p.getItem())) {
				p.setItem(saveCategory);
			}
		}
		saveCategory.getParents().clear();
		for (Long parent : parentsP.getValues()) {
			final Category p = new Category();

			
			p.setId(parent);
			addParent(saveCategory, p);
		}
		saveCategory.setPropertyValues(Util.filterEmpty(saveCategory.getPropertyValues()));
		CatalogServiceAsync.updateCategory(originalCategory, saveCategory, new AsyncCallback<Category>() {
			@Override
			public void onFailure(Throwable caught) {
				view.setSaving(false);
			}

			@Override
			public void onSuccess(Category result) {
				view.setSaving(false);
				if (result != null) {
					StatusMessage.get().show(i18n.productCategorySaved(Util.name(result, currentLanguage)));
					CatalogCache.get().put(result);
					currentCategory = null; // force redraw of product
											// group
					originalCategory = null;
					show(result);
					onSaved(result);
				} else {
					StatusMessage.get().show(i18n.productCategorySaved(""));
				}
			}
		});
	}

	protected void onSaved(Category savedCategory) {
	}

	public void switchLanguage(String lang) {
		currentLanguage = lang;
		show(currentCategory);
	}

	public void show(Category newCategory) {
		final List<Language> langs = CatalogCache.get().getCurrentCatalog().getLanguages();

		if (currentCategory != newCategory) {
			currentCategory = newCategory;
			if (currentCategory != null) {
				if ((originalCategory == null || originalCategory.getId() != currentCategory.getId()) && currentCategory.getId() != null) {
					originalCategory = currentCategory.clone(new HashMap());
				}
			}
		}
		if (currentCategory != null) {
			view.setCategoryName(Util.name(currentCategory, currentLanguage));
//			view.setDescription(Util.description(currentCategory, currentLanguage));
			view.containsProducts().setValue(currentCategory.getContainsProducts());
			// parents product groups
			final List<Map.Entry<Long, String>> curParents = new ArrayList<Map.Entry<Long, String>>();

			for (ParentChild cp : currentCategory.getParents()) {
				curParents.add(CatalogCache.get().getCategoryNameEntry(cp.getParent().getId(), currentLanguage));
			}
			parentsP.show(currentCategory, curParents, currentLanguage, CatalogCache.get().getCategoryNamesByLang(currentLanguage));
			// own properties with default values
			pgpp.show(langs, currentLanguage, currentCategory);
			// inherited properties from parents
			view.clear();
			valuesPresenters.clear();
			view.add(i18n.parents(), parentsP.getView());
			view.add(i18n.properties(), pgpp.getView());
			final List<Long> parents = Util.findParents(currentCategory);
			final Long nameGId = CatalogCache.get().getNameCategory().getId();

			for (Long pid : parents) {
				final Category parent = CatalogCache.get().getCategory(pid);
				final List<PropertyValue[]> pv = Util.getCategoryPropertyValues(langs, parent, currentCategory);

				if (!pv.isEmpty()) {
					final ItemValuesPresenter presenter = new ItemValuesPresenter();

					valuesPresenters.add(presenter);
					final PropertyValue pvName = Util.getPropertyValueByName(parent.getPropertyValues(), Util.NAME, currentLanguage);
					final PropertyValue pvDName = Util.getPropertyValueByName(parent.getPropertyValues(), Util.NAME, null);
					final String name = nameGId.equals(pid) ? "" : (pvName == null || Util.isEmpty(pvName) ? (pvDName == null ? "" : pvDName.getStringValue()) : pvName.getStringValue());

					view.addPropertyValues(name, presenter.getView());
					presenter.show(currentLanguage, pv);
				}
			}
		} else {

		}
	}
}

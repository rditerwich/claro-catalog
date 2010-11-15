package agilexs.catalogxsadmin.presentation.client.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import agilexs.catalogxsadmin.presentation.client.catalog.Catalog;
import agilexs.catalogxsadmin.presentation.client.catalog.Category;
import agilexs.catalogxsadmin.presentation.client.catalog.Item;
import agilexs.catalogxsadmin.presentation.client.catalog.Label;
import agilexs.catalogxsadmin.presentation.client.catalog.Language;
import agilexs.catalogxsadmin.presentation.client.catalog.OutputChannel;
import agilexs.catalogxsadmin.presentation.client.catalog.Product;
import agilexs.catalogxsadmin.presentation.client.catalog.Property;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.services.CatalogServiceAsync;
import agilexs.catalogxsadmin.presentation.client.services.ShopServiceAsync;
import agilexs.catalogxsadmin.presentation.client.shop.Promotion;
import agilexs.catalogxsadmin.presentation.client.shop.Shop;
import agilexs.catalogxsadmin.presentation.client.util.Entry;

import com.google.gwt.user.client.rpc.AsyncCallback;

//TODO cache related objects:  propertyvalues in productgroup
//Singleton
public class CatalogCache {

	private static CatalogCache instance = new CatalogCache();

	public static CatalogCache get() {
		return instance;
	}

	private final Map<Long, Map<String, String>> categoryNamesCache = new HashMap<Long, Map<String, String>>();

	private final Map<String, Shop> catalogShopCache = new HashMap<String, Shop>();
	private final Map<Long, Shop> shopCache = new HashMap<Long, Shop>();
	private final Map<Long, Category> categoryCache = new HashMap<Long, Category>();
	private final Map<Long, Product> productCache = new HashMap<Long, Product>();
	private final Map<Long, Property> propertyCache = new HashMap<Long, Property>();
	private final Map<Long, PropertyValue> propertyValueCache = new HashMap<Long, PropertyValue>();
	private final Map<Long, Promotion> promotionCache = new HashMap<Long, Promotion>();
	private final Map<Long, Label> labelCache = new HashMap<Long, Label>();
	private Category nameCategory;
	private Category categoryProduct;
	
	private Catalog currentCatalog;
	private Shop currentShop;
	private Language currentLanguage;
	private Set<ChangeListener<Catalog>> currentCatalogListeners = new HashSet<ChangeListener<Catalog>>(); 
	private Set<ChangeListener<Shop>> currentShopListeners = new HashSet<ChangeListener<Shop>>(); 
	private Set<ChangeListener<Language>> currentLanguageListeners = new HashSet<ChangeListener<Language>>(); 

	private CatalogCache() {
	}

	/**
	 * View: Basic, languages, shops
	 * @return
	 */
	public Catalog getCurrentCatalog() {
		return currentCatalog;
	}

	/**
	 * View: Basic, languages, shops
	 * @return
	 */
	public void listenToCurrentCatalog(ChangeListener<Catalog> listener) {
		currentCatalogListeners.add(listener);
		listener.changed(currentCatalog);
	}
	
	/**
	 * View: Basic, languages, shops
	 * @return
	 */
	public void setCurrentCatalog(Catalog currentCatalog) {
		this.currentCatalog = currentCatalog;
		for (ChangeListener<Catalog> listener : currentCatalogListeners) {
			listener.changed(currentCatalog);
		}
	}
	
	/**
	 * View: Basic.
	 * @return
	 */
	public void listenToCurrentShop(ChangeListener<Shop> listener) {
		currentShopListeners.add(listener);
		listener.changed(currentShop);
	}
	
	/**
	 * The currentshop
	 * View: Basic
	 * @return
	 */
	public Shop getCurrentShop() {
		return currentShop;
	}
	

	/**
	 * Set the current shop
	 * View: Basic.
	 * @param currentShop
	 */
	public void setCurrentShop(Shop currentShop) {
		this.currentShop = currentShop;
		for (ChangeListener<Shop> listener : currentShopListeners) {
			listener.changed(currentShop);
		}
	}

	/**
	 * View: Basic.
	 * @return
	 */
	public Language getCurrentLanguage() {
		return currentLanguage;
	}
	
	/**
	 * View: Basic.
	 * @return
	 */
	public void listenToCurrentLanguage(ChangeListener<Language> listener) {
		currentLanguageListeners.add(listener);
		listener.changed(currentLanguage);
	}
	
	/**
	 * View: Basic.
	 * @param currentLanguage
	 */
	public void setCurrentLanguage(Language currentLanguage) {
		this.currentLanguage = currentLanguage;
		for (ChangeListener<Language> listener : currentLanguageListeners) {
			listener.changed(currentLanguage);
		}
	}


	/**
	 * Returns the Category that contains the Property with the label 'Name'.
	 * 
	 * @return
	 */
	public Category getNameCategory() {
		return nameCategory;
	}

	/**
	 * Returns the Category that contains the general product properties, like
	 * article number, description, price and image. These properties are
	 * required for every product and these properties are used to display a
	 * product in a table, etc.
	 * 
	 * @return
	 */
	public Category getCategoryProduct() {
		return categoryProduct;
	}

	/**
	 * This is the Product Group that owns the Name property
	 * 
	 * @param nameCategory
	 */
	public void setNameCategory(Category nameCategory) {
		this.nameCategory = nameCategory;
	}

	public void setCategoryProduct(Category categoryProduct) {
		this.categoryProduct = categoryProduct;
	}

	public void loadCategoryNames(final AsyncCallback callback) {
		listenToCurrentCatalog(new ChangeListener<Catalog>() {
			public void changed(Catalog newValue) {
				if (newValue != null) {
					CatalogServiceAsync.findAllCategoryNames(newValue,
							new AsyncCallback<List<PropertyValue>>() {
						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}
						
						@Override
						public void onSuccess(List<PropertyValue> result) {
							for (PropertyValue pv : result) {
								final String lang = pv.getLanguage() == null ? ""
										: pv.getLanguage();
								
								updateCategoryName(pv.getItem().getId(), lang,
										pv.getStringValue());
							}
							callback.onSuccess("loaded");
						}
					});
				}
			}
		});
	}

	public Map.Entry<Long, String> getCategoryNameEntry(Category pg, String lang) {
		return getCategoryNameEntry(pg.getId(), lang);
	}

	public Map.Entry<Long, String> getCategoryNameEntry(Long pid, String lang) {
		final Map.Entry<Long, String> tp = new Entry<Long, String>(pid);

		String categoryName = getCategoryName(pid, lang);
		tp.setValue(categoryName);
		
		return tp;
	}
	
	public String getCategoryName(final Long pid, final String lang) {
		final String cachedCategoryName[] = new String[1];
		cachedCategoryName[0] = getCachedCategoryName(pid, lang);
		if (cachedCategoryName[0] == null) {
			loadCategoryNames(new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					throw new RuntimeException(caught);
				}

				@Override
				public void onSuccess(String result) {
					cachedCategoryName[0] = getCachedCategoryName(pid, lang);
				}
			});
		}
		
		return cachedCategoryName[0];
	}

	private String getCachedCategoryName(Long pid, String lang) {
		String categoryName = null;
		Map<String, String> categoryNames = categoryNamesCache.get(pid);
		if (categoryNames != null) {
			categoryName = categoryNames.get(lang);
			if (categoryName == null) {
				// fall back on default language
				categoryName = categoryNames.get("");
			}
		}
		return categoryName;
	}

	// TODO make lazy + async???
	public ArrayList<Map.Entry<Long, String>> getCategoryNamesByLang(String lang) {
		final ArrayList<Map.Entry<Long, String>> list = new ArrayList<Map.Entry<Long, String>>();

		for (Long pid : categoryNamesCache.keySet()) {
			list.add(getCategoryNameEntry(pid, lang));
		}
		return list;
	}

	public void updateCategoryName(Long pid, String lang, String name) {
		Map<String, String> categoryNames = categoryNamesCache.get(pid);
		if (categoryNames == null) {
			categoryNames = new HashMap<String, String>();
			categoryNamesCache.put(pid, categoryNames);
		}
		categoryNames.put(lang, name);
	}

	public void getCatalog(String name, final AsyncCallback callback) {
		if (catalogShopCache.containsKey(name)) {
			callback.onSuccess(catalogShopCache.get(name));
		} else {
			CatalogServiceAsync.getOrCreateCatalog(name,
				new AsyncCallback<Catalog>() {
					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(Catalog result) {
						put(result);
						callback.onSuccess(result);
					}
				});
		}
	}

	public Collection<Category> getAllCategorys() {
		return categoryCache.values();
	}

	public Shop getShop(Long id) {
		return shopCache.get(id);
	}

	public void getShop(Long id, final AsyncCallback callback) {
		if (shopCache.containsKey(id)) {
			callback.onSuccess(getShop(id));
		} else {
			ShopServiceAsync.findShopById(id, new AsyncCallback<Shop>() {
				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				@Override
				public void onSuccess(Shop result) {
					put(result);
					put(result.getCatalog());
					getCurrentCatalog().getOutputChannels().add(result);
					callback.onSuccess(result);
				}
			});
		}
	}

	public Category getCategory(Long id) {
		return categoryCache.get(id);
	}

	public void getproductGroupCache(Long id, final AsyncCallback callback) {
		if (categoryCache.containsKey(id)) {
			callback.onSuccess(getCategory(id));
		} else {
			CatalogServiceAsync.findCategoryById(id,
					new AsyncCallback<Category>() {
						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

						@Override
						public void onSuccess(Category result) {
							put(result);
							callback.onSuccess(result);
						}
					});
		}
	}

	public Product getProduct(Long id) {
		return productCache.get(id);
	}

	public void getProduct(Long id, final AsyncCallback<Product> callback) {
		if (productCache.containsKey(id)) {
			callback.onSuccess(productCache.get(id));
		} else {
			CatalogServiceAsync.findProductById(id,
					new AsyncCallback<Product>() {
						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

						@Override
						public void onSuccess(Product result) {
							put(result);
							callback.onSuccess(result);
						}
					});
		}
	}

	public Property getProperty(Long id) {
		return propertyCache.get(id);
	}

	public void getProperty(Long id, final AsyncCallback<Property> callback) {
		if (propertyCache.containsKey(id)) {
			callback.onSuccess(propertyCache.get(id));
		} else {
			CatalogServiceAsync.findPropertyById(id,
					new AsyncCallback<Property>() {
						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

						@Override
						public void onSuccess(Property result) {
							put(result);
							callback.onSuccess(result);
						}
					});
		}
	}

	public PropertyValue getPropertyValue(Long id) {
		return propertyValueCache.get(id);
	}

	public void getPropertyValue(Long id,
			final AsyncCallback<PropertyValue> callback) {
		if (propertyValueCache.containsKey(id)) {
			callback.onSuccess(propertyValueCache.get(id));
		} else {
			CatalogServiceAsync.findPropertyValueById(id,
					new AsyncCallback<PropertyValue>() {
						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

						@Override
						public void onSuccess(PropertyValue result) {
							put(result);
							callback.onSuccess(result);
						}
					});
		}
	}

	public Promotion getPromotion(Long id) {
		return promotionCache.get(id);
	}

	public Label getLabel(Long id) {
		return labelCache.get(id);
	}

	public void getLabel(Long id, final AsyncCallback<Label> callback) {
		if (labelCache.containsKey(id)) {
			callback.onSuccess(labelCache.get(id));
		} else {
			CatalogServiceAsync.findLabelById(id, new AsyncCallback<Label>() {
				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				@Override
				public void onSuccess(Label result) {
					put(result);
					callback.onSuccess(result);
				}
			});
		}
	}

	public void put(Catalog catalog) {
		currentCatalog = catalog;
		for (OutputChannel channel : catalog.getOutputChannels()) {
			put((Shop)channel);
		}
		for (Item item : catalog.getItems()) {
			put(item);
		}
	}

	public void put(Shop shop) {
		if (shop != null) {
			shopCache.put(shop.getId(), shop);
		}
	}

	public void put(Category pg) {
		if (pg != null) {
			categoryCache.put(pg.getId(), pg);
		}
	}

	public void put(Product p) {
		if (p != null) {
			productCache.put(p.getId(), p);
		}
	}

	public void put(Item item) {
		if (item instanceof Product) {
			put((Product) item);
		} else if (item instanceof Category) {
			put((Category) item);
		}
	}

	public void put(Property p) {
		if (p != null) {
			propertyCache.put(p.getId(), p);
		}
	}

	public void put(Promotion p) {
		if (p != null) {
			promotionCache.put(p.getId(), p);
		}
	}

	public void put(PropertyValue pv) {
		if (pv != null) {
			propertyValueCache.put(pv.getId(), pv);
		}
	}

	public void put(Label l) {
		if (l != null) {
			labelCache.put(l.getId(), l);
		}
	}
	
	public interface ChangeListener<T> {
		void changed(T newValue);
	}
}

package claro.catalog.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.persistence.EntityManager;

import claro.catalog.CatalogDao;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.RootProperties;
import claro.catalog.util.CatalogModelUtil;
import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.Label;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Product;
import claro.jpa.catalog.PropertyGroup;
import claro.jpa.catalog.PropertyType;
import claro.jpa.catalog.StagingArea;
import claro.jpa.media.Media;
import claro.jpa.media.MediaContent;
import claro.jpa.shop.Shop;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

import easyenterprise.lib.util.SMap;
import easyenterprise.lib.util.SecurityUtil;

public class CatalogModel {

	public final CatalogDao dao;
	public final Catalog catalog;
	public final ItemModel root;
	public final PropertyGroup generalPropertyGroup;
	public final PropertyModel nameProperty;
	public final PropertyModel variantProperty;
	public final PropertyModel articleNumberProperty;
	public final PropertyModel descriptionProperty;
	public final PropertyModel imageProperty;
	public final PropertyModel smallImageProperty;
	public final PropertyModel priceProperty;
	public final PropertyModel supplierProperty;
	public final PropertyModel supplierArticleNumberProperty;
	public final PropertyModel visibleProperty;
	final Map<Long, ItemModel> items = new HashMap<Long, ItemModel>();
	final Map<Long, PropertyGroupInfo> propertyGroupInfos = new HashMap<Long, PropertyGroupInfo>();
	final ConcurrentLinkedQueue<ItemToInvalidate> itemsToInvalidate = new ConcurrentLinkedQueue<ItemToInvalidate>();
	private PropertyGroup imagesPropertyGroup;
	private PropertyGroup documentsPropertyGroup;
	private PropertyModel manualProperty;
	private LinkedHashSet<String> allLanguages;

	public CatalogModel(Long id, CatalogDao dao) {
		this.dao = dao;
		this.catalog = findOrCreateCatalog(id);
		this.root = findOrCreateRootCategory();
		this.generalPropertyGroup = findOrCreatePropertyGroup(RootProperties.GENERALGROUP, null);
		this.imagesPropertyGroup = findOrCreatePropertyGroup(RootProperties.IMAGES, null);
		this.documentsPropertyGroup = findOrCreatePropertyGroup(RootProperties.DOCUMENTS, null);
		this.nameProperty = root.findOrCreateProperty(RootProperties.NAME, null, PropertyType.String, generalPropertyGroup, RootProperties.ROOTCATEGORY_NAME);
		this.variantProperty = root.findOrCreateProperty(RootProperties.VARIANT, null, PropertyType.String, generalPropertyGroup);
		this.articleNumberProperty = root.findOrCreateProperty(RootProperties.ARTICLENUMBER, null, PropertyType.String, generalPropertyGroup);
		this.descriptionProperty = root.findOrCreateProperty(RootProperties.DESCRIPTION, null, PropertyType.String, generalPropertyGroup);
		this.imageProperty = root.findOrCreateProperty(RootProperties.IMAGE, null, PropertyType.Media, imagesPropertyGroup);
		this.smallImageProperty = root.findOrCreateProperty(RootProperties.SMALLIMAGE, null, PropertyType.Media, imagesPropertyGroup);
		this.priceProperty = root.findOrCreateProperty(RootProperties.PRICE, null, PropertyType.Money, generalPropertyGroup);
		this.supplierProperty = root.findOrCreateProperty(RootProperties.SUPPLIER, null, PropertyType.String, generalPropertyGroup);
		this.supplierArticleNumberProperty = root.findOrCreateProperty(RootProperties.SUPPLIER_ARTICLENUMBER, null, PropertyType.String, generalPropertyGroup);
		this.manualProperty = root.findOrCreateProperty(RootProperties.MANUAL, null, PropertyType.Media, documentsPropertyGroup);
		this.visibleProperty = root.findOrCreateProperty(RootProperties.VISIBLE, null, PropertyType.Boolean, generalPropertyGroup, true);
  }
	
	public Catalog getCatalog() {
		return catalog;
	}
	
	public ItemModel getRootItem() throws ItemNotFoundException {
		return getItem(catalog.getRoot().getId());
	}
	
	public synchronized ItemModel getItem(Long id) throws ItemNotFoundException {
		ItemModel itemData = items.get(id);
		if (itemData == null) {
			Item item = dao.getItem(id);
			if (item != null) {
				itemData = new ItemModel(this, id);
				items.put(id, itemData);
			} else {
				throw new ItemNotFoundException(id);
			}
		}
		return itemData;
	}
	
	public List<ItemModel> getItems(Collection<Long> ids) throws ItemNotFoundException {
		List<ItemModel> result = new ArrayList<ItemModel>();
		for (Long id : ids) {
			result.add(getItem(id));
		}
		return result;
	}
	
	public synchronized void removeItem(Long id) {
		ItemModel itemData = items.get(id);
		Item item;
		if (itemData != null) {
			item = itemData.getEntity();
			invalidateItems(itemData, true, true, true);
			root.flush();
			items.remove(id);
		} else {
			item = dao.getItem(id);
		}
		
		if (item == null) {
			throw new ItemNotFoundException(id);
		}
		
		dao.removeItem(item); // TODO It is more efficient in the context of a transaction rollback to move this to the beginning
	}
	
	public synchronized PropertyGroupInfo findPropertyGroupInfo(Long id) {
		PropertyGroupInfo result = propertyGroupInfos.get(id);
		if (result == null) {
			PropertyGroup group = dao.getEntityManager().find(PropertyGroup.class, id);
			if (group != null) {
				result = findOrCreatePropertyGroupInfo(group);
			}
		}
		
		return result;
	}

	public synchronized PropertyGroupInfo findOrCreatePropertyGroupInfo(PropertyGroup group) {
		PropertyGroupInfo result = propertyGroupInfos.get(group.getId());
		if (result == null) {
			result = new PropertyGroupInfo();
			result.propertyGroupId = group.getId();
			for (Label label : group.getLabels()) {
				result.labels = result.labels.add(label.getLanguage(), label.getLabel());
			}
			propertyGroupInfos.put(group.getId(), result);
		}
		
		return result;
	}
	
	/**
	 * Retrieves all languages currently in use by this catalog.  Note that the null language is also included.
	 * @return
	 */
	public synchronized Set<String> getAllLanguages() {
		if (allLanguages == null) {
			allLanguages = new LinkedHashSet<String>();
			
			allLanguages.add(null);
			
			allLanguages.addAll(CatalogModelUtil.splitLanguages(getCatalog().getLanguages()));
			for (OutputChannel channel : getCatalog().getOutputChannels()) {
				if (channel instanceof Shop) {
					Shop shop = (Shop) channel;
					
					allLanguages.addAll(CatalogModelUtil.splitLanguages(shop.getLanguages()));
				}
			}
		}
		return allLanguages;
	}

	public MediaContent getOrCreateMediaContent(String mimeType, byte[] data) {
		 String hash = SecurityUtil.computeSHA1(data);
		 for (MediaContent mediaContent : dao.findMediaContent(mimeType, hash)) {
			 if (mimeType.equals(mediaContent.getMimeType()) && Arrays.equals(mediaContent.getData(), data)) {
				 return mediaContent;
			 }
		 }
		 MediaContent mediaContent = new MediaContent();
		 mediaContent.setMimeType(mimeType);
		 mediaContent.setData(data);
		 mediaContent.setHash(hash);
		 dao.getEntityManager().persist(mediaContent);
		 return mediaContent;
	}
	
	public Media getOrCreateMedia(String mimeType, byte[] content, String nameHint) {
		MediaContent mediaContent = getOrCreateMediaContent(mimeType, content);
		Media result = null;
		for (Media media : dao.findMediaByContent(mediaContent)) {
			if (result == null || Objects.equal(nameHint, media.getName())) {
				result = media;
			}
		}
		if (result == null) {
			result = new Media();
			result.setName(nameHint);
			result.setContent(mediaContent);
			dao.getEntityManager().persist(result);
		}
		return result;
	}
	
	private Catalog findOrCreateCatalog(Long id) {
		EntityManager em = dao.getEntityManager();
		Catalog catalog = em.find(Catalog.class, id);
		if (catalog == null) {
			catalog = new Catalog();
			catalog.setId(id);
			em.persist(catalog);
			em.flush(); // Force id generation, because there is an interdependency between catalog and the root category.
		}
		if (Strings.isNullOrEmpty(catalog.getName())) {
			catalog.setName("Catalog");
		}
		return catalog;
  }

	private ItemModel findOrCreateRootCategory() {
	  ItemModel root;
	  if (catalog.getRoot() == null) {
		  root = createCategory();
		  catalog.setRoot((Category) root.getEntity());
	  } else {
		  root = getItem(catalog.getRoot().getId());
	  }
	  return root;
  }	
	
	private void findOrCreateStagingArea(String name) {
		StagingArea staging = dao.findStagingAreaByName(name);
		if (staging == null) {
			staging = new StagingArea();
			staging.setName(name);
			dao.getEntityManager().persist(staging);
		}
	}
	
	private PropertyGroup findOrCreatePropertyGroup(String propertyGroupLabel, String language) {
		for (PropertyGroup group : catalog.getPropertyGroups()) {
			if (CatalogModelUtil.find(group.getLabels(), propertyGroupLabel, language) != null) {
				return group;
			}
		}
		// Not found.  Create it instead
		return createPropertyGroup(SMap.create(language, propertyGroupLabel));
	}
	
	private PropertyGroup createPropertyGroup(SMap<String, String> initialLabels) {
		synchronized (catalog) {
			PropertyGroup propertyGroup = new PropertyGroup();
			for (String lanuage : initialLabels.getKeys()) {
				Label label = new Label();
				label.setLanguage(lanuage);
				label.setLabel(initialLabels.get(lanuage));
				label.setPropertyGroup(propertyGroup);
				propertyGroup.getLabels().add(label);
			}
			dao.getEntityManager().persist(propertyGroup);
			assert propertyGroup.getId() != null;
			catalog.getPropertyGroups().add(propertyGroup);
			propertyGroup.setCatalog(catalog);
//				catalog.invalidate(this);
//				catalog.invalidate(childExtent);
//				return PropertyModel.createRoot(propertyGroup.getId(), false, this);
			return propertyGroup;
		}
	}

	
	public ItemModel createProduct() {
		Product product = new Product();
		product.setCatalog(catalog);
		catalog.getItems().add(product);
		dao.getEntityManager().persist(product);
		return getItem(product.getId());
	}
	
	public ItemModel createCategory() {
		Category category = new Category();
		category.setCatalog(catalog);
		catalog.getItems().add(category);
		dao.getEntityManager().persist(category);
		return getItem(category.getId());
	}
	
	public void invalidateItem(ItemModel item) {
		invalidateItems(item, true, false, false);
	}
	
	public void invalidateItemAndChildExtent(ItemModel item) {
		invalidateItems(item, true, true, false);
	}
	
	public void invalidateItems(ItemModel item, boolean self, boolean childExtent, boolean parentExtent) {
		itemsToInvalidate.add(new ItemToInvalidate(item.itemId, self, childExtent, parentExtent));
	}
	
	public void invalidateAllItems() {
		for (ItemModel item : items.values()) {
			invalidateItem(item);
		}
	}
	
	public void invalidateAll() {
		invalidateAllItems();
		// invalidate more
	}
	
	void invalidateItems(Iterable<ItemModel> items) {
		if (items != null) {
			for (ItemModel item : items) {
				invalidateItem(item);
			}
		}
	}
 
	public void flush() {
		// collect items to invalidate
		Map<Long, ItemModel> items = null;
		while (true) {
			ItemToInvalidate itemToInvalidate = itemsToInvalidate.poll();
			if (itemToInvalidate == null) break;
			if (items == null) {
				items = new TreeMap<Long, ItemModel>();
			}
			ItemModel model = this.items.get(itemToInvalidate.itemId);
			if (model == null) continue;
			if (itemToInvalidate.self) items.put(itemToInvalidate.itemId, model);
			if (itemToInvalidate.childExtent) {
				for (ItemModel child : model.getChildExtent()) items.put(child.itemId, child);
			}
			if (itemToInvalidate.parentExtent) {
				for (ItemModel parent : model.getParentExtent()) items.put(parent.itemId, parent);
			}
			// now invalidate them all
			for (ItemModel item : items.values()) {
				item.flush();
			}
		}
	}
}

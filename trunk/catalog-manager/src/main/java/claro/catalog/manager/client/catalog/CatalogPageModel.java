package claro.catalog.manager.client.catalog;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.data.RootProperties;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.items.ItemPageModel;
import claro.catalog.manager.client.webshop.ShopModel;
import claro.jpa.catalog.OutputChannel;

import com.google.common.base.Objects;

import easyenterprise.lib.util.SMap;

public abstract class CatalogPageModel extends ItemPageModel implements Globals {

	private SMap<Long, SMap<PropertyInfo, SMap<String, Object>>> products = SMap.empty();
	
	private Long rootCategory;
	private PropertyInfo nameProperty;
	private PropertyInfo variantProperty;
	private PropertyInfo descriptionProperty;
	private PropertyInfo priceProperty;
	private PropertyInfo artNoProperty;
	private PropertyInfo imageProperty;
	private PropertyInfo smallImageProperty;
	
	private String filterString;
	private SMap<Long, SMap<String, String>> filterCategories = SMap.<Long, SMap<String, String>>empty();
	private Set<Long> changedProducts = Collections.emptySet();
	private SMap<String, PropertyInfo> rootProperties;

	private Long selectedProductId;
	private SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> selectedProductPropertyValues;
	private SMap<Long, SMap<String, String>> selectedProductCategories;
	private SMap<OutputChannel, SMap<Long, SMap<String, String>>> selectedProductPromotions;
	private SMap<Long, SMap<String, String>> selectedParentExtentWithSelf;
	
	public Long getSelectedProductId() {
		return selectedProductId;
	}

	public void setSelectedProductId(Long productId) {
		this.selectedProductId = productId;
	}

	public SMap<Long, SMap<String, String>> getSelectedProductCategories() {
		return selectedProductCategories;
	}
	
	public SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> getSelectedProductPropertyValues() {
		return selectedProductPropertyValues;
	}
	
	public SMap<OutputChannel, SMap<Long, SMap<String, String>>> getSelectedProductPromotions() {
		return selectedProductPromotions;
	}

	public SMap<Long, SMap<String, String>> getSelectedParentExtentWithSelf() {
		return selectedParentExtentWithSelf;
	}

	public void setProductData(Long productId, SMap<PropertyInfo, SMap<String, Object>> masterValues, SMap<Long, SMap<String, String>> parentExtentWithSelf, SMap<Long, SMap<String, String>> categories, SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> propertyValues, SMap<OutputChannel, SMap<Long, SMap<String, String>>> promotions) {
		if (Objects.equal(selectedProductId, productId)) {
			this.selectedProductCategories = categories;
			this.selectedProductPropertyValues = propertyValues;
			this.selectedProductPromotions = promotions;
			this.selectedParentExtentWithSelf = parentExtentWithSelf;
		}
		if (products.get(productId) != null) {
			products = products.set(productId, masterValues);
		}
	}
	
	public void removeProduct(Long productId) {
		products = products.removeKey(productId);
	}
	
	public SMap<Long, SMap<PropertyInfo, SMap<String, Object>>> getProducts() {
		return products;
	}
	
	public void setProducts(SMap<Long, SMap<PropertyInfo, SMap<String, Object>>> products) {
		this.products = products;
	}

	public Long getRootCategory() {
		return rootCategory;
	}
	
	public void setRootCategory(Long rootCategory) {
		this.rootCategory = rootCategory;
	}
	
	public SMap<String, PropertyInfo> getRootProperties() {
		return rootProperties;
	}
	
	public void setRootProperties(SMap<String, PropertyInfo> rootProperties) {
		this.rootProperties = rootProperties;
		this.nameProperty = rootProperties.get(RootProperties.NAME);
		this.variantProperty = rootProperties.get(RootProperties.VARIANT);
		this.descriptionProperty = rootProperties.get(RootProperties.DESCRIPTION);
		this.priceProperty = rootProperties.get(RootProperties.PRICE);
		this.artNoProperty = rootProperties.get(RootProperties.ARTICLENUMBER);
		this.imageProperty = rootProperties.get(RootProperties.IMAGE);
		this.smallImageProperty = rootProperties.get(RootProperties.SMALLIMAGE);
	}
	
	public PropertyInfo getNameProperty() {
		return nameProperty;
	}
	
	public PropertyInfo getVariantProperty() {
		return variantProperty;
	}
	
	public PropertyInfo getArtNoProperty() {
		return artNoProperty;	
	}
	
	public PropertyInfo getPriceProperty() {
		return priceProperty;
	}
	
	public PropertyInfo getImageProperty() {
		return imageProperty;
	}
	
	public PropertyInfo getSmallImageProperty() {
		return smallImageProperty;
	}
	
	public PropertyInfo getDescriptionProperty() {
		return descriptionProperty;
	}
	
	public String getFilterString() {
		return filterString;
	}
	
	public void setFilterString(String text) {
		this.filterString = text;
	}
	
	public SMap<Long, SMap<String, String>> getFilterCategories() {
		return filterCategories;
	}
	
	public void setFilterCategories(SMap<Long, SMap<String, String>> filterCategories) {
		this.filterCategories = filterCategories;
	}
	
	public int indexOfProduct(Long productId) {
		return products.indexOf(productId);
	}

	public boolean isChanged(Long productId) {
		return changedProducts.contains(productId);
	}
	
	public List<Long> getOrderByIds() {
		if (getNameProperty() != null) {
			return Collections.singletonList(getNameProperty().propertyId);
		}
		return Collections.emptyList();
	}
	
	public abstract ShopModel getShopModel();
}

package claro.catalog.manager.client.taxonomy;

import claro.catalog.manager.client.items.ItemPageModel;
import easyenterprise.lib.util.SMap;

public class TaxonomyModel extends ItemPageModel {

	private Long selectedCategoryId;
	private Long rootCategoryId;
	private SMap<Long, SMap<String, String>> categories;
	private SMap<Long, Long> childrenByCategory;
	public void setSelectedCategoryId(Long selectedCategoryId) {
		this.selectedCategoryId = selectedCategoryId;
	}
	public Long getSelectedCategoryId() {
		return selectedCategoryId;
	}

	
	public void setCategoriesTree(Long rootCategoryId, SMap<Long, Long> children, SMap<Long, SMap<String, String>> categories) {
		this.rootCategoryId = rootCategoryId;
		this.categories = categories;
		this.childrenByCategory = children;
	}
	
	
	public Long getRootCategoryId() {
		return rootCategoryId;
	}
	
	public SMap<Long, SMap<String, String>> getCategories() {
		return categories;
	}
	
	public SMap<Long, Long> getChildrenByCategory() {
		return childrenByCategory;
	}
	public void updateCategory(Long categoryId, SMap<String, String> categoryLabels) {
		categories = categories.set(categoryId, categoryLabels);
	}

}

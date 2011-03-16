package claro.catalog.manager.client.taxonomy;

import java.util.List;
import java.util.Map;

import claro.catalog.manager.client.items.ItemPageModel;
import easyenterprise.lib.util.SMap;

public class TaxonomyModel extends ItemPageModel {

	private Long selectedCategoryId;
	private Long rootCategoryId;
	private SMap<Long, SMap<String, String>> categories;
	private Map<Long, List<Long>> childrenByCategory;
	public void setSelectedCategoryId(Long selectedCategoryId) {
		this.selectedCategoryId = selectedCategoryId;
	}
	public Long getSelectedCategoryId() {
		return selectedCategoryId;
	}

	
	public void setCategoriesTree(Long rootCategoryId, Map<Long, List<Long>> children, SMap<Long, SMap<String, String>> categories) {
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
	
	public Map<Long, List<Long>> getChildrenByCategory() {
		return childrenByCategory;
	}
	public void updateCategory(Long categoryId, SMap<String, String> categoryLabels) {
		categories = categories.set(categoryId, categoryLabels);
	}

}

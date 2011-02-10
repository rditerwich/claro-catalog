package claro.catalog.manager.client.taxonomy;

import claro.catalog.manager.client.items.ItemPageModel;

public class TaxonomyModel extends ItemPageModel {

	private Long selectedCategoryId;
	public void setSelectedCategoryId(Long selectedCategoryId) {
		this.selectedCategoryId = selectedCategoryId;
	}
	public Long getSelectedCategoryId() {
		return selectedCategoryId;
	}

}

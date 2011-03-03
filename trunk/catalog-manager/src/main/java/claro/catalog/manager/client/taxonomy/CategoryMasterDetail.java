package claro.catalog.manager.client.taxonomy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import claro.catalog.command.items.StoreItemDetails;
import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.data.RootProperties;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.CatalogManagerMasterDetail;
import claro.catalog.manager.client.widgets.ItemSelectionWidget;
import claro.catalog.manager.client.widgets.LanguageAndShopSelector;

import com.google.common.base.Objects;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.gwt.client.widgets.EEButton;
import easyenterprise.lib.gwt.client.widgets.Table;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.SMap;

abstract public class CategoryMasterDetail extends CatalogManagerMasterDetail implements Globals {
	enum Styles implements Style { categoryMasterDetail, categoryprice, categoryname, category, categoryTD, categorypanel, categoryNewSubCategory }

	private String language;
	private String filterString;
	

	private PropertyInfo nameProperty;
	private PropertyInfo variantProperty;
	private PropertyInfo descriptionProperty;
	private PropertyInfo priceProperty;
	private PropertyInfo artNoProperty;
	private PropertyInfo imageProperty;
	private PropertyInfo smallImageProperty;
	
	
	// Widgets
	private ItemSelectionWidget filterCategories;
	private LanguageAndShopSelector languageSelection;
	private List<RowWidgets> tableWidgets = new ArrayList<CategoryMasterDetail.RowWidgets>();

	protected HTML filterLabel;
	private CategoryDetails details;
	
	private List<claro.catalog.manager.client.taxonomy.CategoryMasterDetail.CategoryRow> categoryRows = Collections.emptyList();
	protected Label pleaseWaitLabel;
	private final TaxonomyModel model;


	public CategoryMasterDetail(TaxonomyModel model) {
		super(126);
		this.model = model;
		StyleUtil.addStyle(this, Styles.categoryMasterDetail);
		createMasterPanel();
		createDetailPanel();
	}
	
	public void setRootProperties(SMap<String, PropertyInfo> rootProperties, PropertyGroupInfo generalGroup, Long rootCategory, SMap<String, String> rootCategoryLabels) {
		this.nameProperty = rootProperties.get(RootProperties.NAME);
		this.variantProperty = rootProperties.get(RootProperties.VARIANT);
		this.descriptionProperty = rootProperties.get(RootProperties.DESCRIPTION);
		this.priceProperty = rootProperties.get(RootProperties.PRICE);
		this.artNoProperty = rootProperties.get(RootProperties.ARTICLENUMBER);
		this.imageProperty = rootProperties.get(RootProperties.IMAGE);
		this.smallImageProperty = rootProperties.get(RootProperties.SMALLIMAGE);
		if (details != null) {
			details.setRootProperties(rootProperties);
		}
		render();
	}
	
	public void setCategoriesTree(Long rootCategoryId, SMap<Long, Long> children, SMap<Long, SMap<String, String>> categories) {
		this.model.setCategoriesTree(rootCategoryId, children, categories);
	
		this.categoryRows = createRows();
		render();
		
		// Did we have a selection?  
		if (model.getSelectedCategoryId() != null) {
			
			// Try to find it back
			int row = CategoryRow.findRow(categoryRows, model.getSelectedCategoryId());
			
			// Reselect it.
			rowSelected(row);
		}

	}
	
	public List<Long> getFilterCategories() {
		if (filterCategories != null) {
			return filterCategories.getSelection();
		}
		return null;
	}
	
	public String getFilter() {
		// TODO add drop down filter options:
		return filterString;
	}


	public void refreshLanguages() {
		if (languageSelection != null) {
			languageSelection.refreshData();
		}
	}
	

//	@Override
//	protected int getExtraTableWidth() {
//		return 24; // Compensation for the rounded panels
//	}
//	
//	@Override
//	protected int getExtraTableHeight() {
//		return 48; // Compensation for the rounded panels
//	}


	private void createMasterPanel() {
		Table productTable = new Table();
		setMaster(productTable);

		productTable.resizeColumns(1);
		
		// search panel
		setHeader(new VerticalPanel() {{
				add(new Grid(2, 3) {{
					StyleUtil.addStyle(this, CatalogManager.Styles.filterpanel);
					setWidget(0, 0, languageSelection = new LanguageAndShopSelector() {
						protected void selectionChanged() {
							model.setSelectedLanguage(getSelectedLanguage());
							model.setSelectedShop(getSelectedShop());
							updateCategories();
						}
					});
					setWidget(1, 0, new EEButton(messages.newCategory()) {{
						addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								createNewCategory(model.getRootCategoryId());
							}
						});
					}});
					setWidget(1, 1, new Anchor(messages.refresh()) {{
						addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								updateCategories();
							}
						});
					}});
				}});
				add(new FlowPanel() {{
					add(filterLabel = new HTML() {{
						setVisible(false); 
					}});
				}});
				add(pleaseWaitLabel = new Label(messages.pleaseWait()) {{
					setVisible(true);
				}});
				
		}});
	}
	
	private void createDetailPanel() {
		setDetail(new LayoutPanel() {{
			add(details = new CategoryDetails(model, nameProperty, variantProperty, priceProperty, imageProperty) {
				protected void storeItem(StoreItemDetails cmd) {
					CategoryMasterDetail.this.storeItem(cmd);
				}
			});
		}});
	}
	
	
	public void updateCategory(Long previousCategoryId, Long categoryId, Long parentId, SMap<String, String> categoryLabels, SMap<Long, SMap<String, String>> groups, SMap<Long, SMap<String, String>> parentExtentWithSelf, SMap<Long, SMap<String, String>> parents, SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> propertyValues) {
		
		// No category id means a remove.
		if (categoryId != null) {
			model.updateCategory(categoryId, categoryLabels);
		}
		
		// Find row to update
		int itemRow = CategoryRow.findRow(categoryRows, previousCategoryId);
		if (categoryId == null) {
			categoryRows.remove(itemRow);
			render();
			closeDetail();
		} else if (itemRow != -1) {
			// Update row
			categoryRows.get(itemRow).categoryId = categoryId;
			categoryRows.get(itemRow).isChanged = true;

			// Rerender row
			render(itemRow, categoryRows.get(itemRow));

//			masterChanged();
		} else {
			int parentIndex = -1;
			if (parentId != null) {
				// Find parent:
				parentIndex = CategoryRow.findRow(categoryRows, parentId);
			}
			
			// Add new category, and set itemRow accordingly
			if (parentIndex != -1) {
				itemRow = parentIndex + 1;
				categoryRows.add(itemRow, new CategoryRow(categoryId, model.getChildrenByCategory().getAll(categoryId).isEmpty(), categoryRows.get(parentIndex).indentation + 1, true));
			} else {
				itemRow = categoryRows.size();
				categoryRows.add(new CategoryRow(categoryId, model.getChildrenByCategory().getAll(categoryId).isEmpty(), 1, true));
			}
			
			// Rerender master to add row.
			render();
		}

		if (getCurrentRow() == itemRow) {
			openDetail(itemRow);  // Redraw selection if necessary.
			details.setItemData(categoryId, groups, parentExtentWithSelf, parents, propertyValues);
		}
	}

	private void render() {
		
		// Make sure we have the root properties:
		pleaseWaitLabel.setVisible(nameProperty == null);
		if (nameProperty == null) {
			return;
		}
		
		
		Table categoryTable = getMasterTable();
		
		updateFilterLabel();


		// Delete/Create widgets as necessary:
		int oldRowCount = categoryTable.getRowCount();
		
		// Delete old rows:
		// TODO What if selected row is deleted?  maybe close detail?
		
		categoryTable.resizeRows(categoryRows.size());
		for (int i = tableWidgets.size() - 1; i >= categoryRows.size(); i--) {
			tableWidgets.remove(i);
		}
		
		// Create new Rows
		for (int i = oldRowCount; i < categoryRows.size(); i++) {
			final RowWidgets rowWidgets = new RowWidgets();
			tableWidgets.add(rowWidgets);
			final int row = i;
			
			categoryTable.setWidget(i, 0, new HorizontalPanel() {{
				StyleUtil.addStyle(this, Styles.category);

				// Indent label
				add(rowWidgets.indentLabel = new InlineLabel());
				
				// Image
				add(rowWidgets.hasChildrenImage = new Image(images.nonLeafImage()) {{
					setWidth("10px");
				}});
				
				// Anchor
				add(rowWidgets.categoryName = new Anchor() {{
					StyleUtil.addStyle(this, Styles.categoryname);
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							rowSelected(row);
						}
					});
				}});
				
				add(new Anchor(messages.newChildCategory()) {{
					StyleUtil.addStyle(this, Styles.categoryNewSubCategory);
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							createNewCategory(categoryRows.get(row).categoryId);
						}
					});
				}});
			}});
		}
		

		// (Re) bind widgets:
		
		int i = 0;
		for (CategoryRow categoryRow : categoryRows) {

			render(i, categoryRow);
			
			i++;
		}

//		masterChanged();
	}

	private void render(int i, CategoryRow categoryRow) {
		RowWidgets rowWidgets = tableWidgets.get(i);

		// Indent
		StringBuilder indent = new StringBuilder();
		for (int j = 0; j < 8 * categoryRow.indentation; j++) {
			indent.append("&nbsp;");
		}
		rowWidgets.indentLabel.getElement().setInnerHTML(indent.toString());
		
		// nonLeaf image
		rowWidgets.hasChildrenImage.setVisible(!categoryRow.isLeaf);
		
		// category label
		String label = model.getCategories().getOrEmpty(categoryRow.categoryId).tryGet(language, null);
		if (label == null) {
			label = "<" + messages.noCategoryNameSet() + ">";
		}
		rowWidgets.categoryName.setText(label);
		
		if (categoryRow.isChanged) {
			StyleUtil.add(getMasterTable().getRowFormatter(), i, CatalogManager.Styles.itemRowChanged);
		} else {
			StyleUtil.remove(getMasterTable().getRowFormatter(), i, CatalogManager.Styles.itemRowChanged);
		}
	}
	
	private void closeDetail() {
		model.setSelectedCategoryId(null);
		closeDetail(true);
	}
	
	private void rowSelected(int row) {
		if (row < 0) {
			closeDetail();
		} else {
			CategoryRow selectedCategory = categoryRows.get(row);
			categorySelected(selectedCategory.categoryId);
		}
	}
	

	
	protected void setSelectedCategory(Long categoryId, SMap<Long, SMap<String, String>> groups, SMap<Long, SMap<String, String>> parentExtentWithSelf, SMap<Long, SMap<String, String>> parents, SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> properties) {
		model.setSelectedCategoryId(categoryId);
		int row = CategoryRow.findRow(categoryRows, categoryId);

		int oldRow = getCurrentRow();
		
		setCurrentRow(row);

		details.setItemData(categoryId, groups, parentExtentWithSelf, parents, properties);
		
		if (oldRow != row) {
//			details.resetTabState();
		}
	}
	
	abstract protected void categorySelected(Long productId);
	
	abstract protected void createNewCategory(long parentId);
	
	abstract protected void updateCategories();
	
	abstract protected void storeItem(StoreItemDetails cmd);

	private void updateFilterLabel() {
		StringBuilder filterText = new StringBuilder();
		String actualFilter = getFilter();
		boolean filterSet = actualFilter != null && !actualFilter.trim().equals("");
		if (filterSet) {
			filterText.append(actualFilter);
		}
		String sep = "";
		if (filterSet) {
			sep = " and ";
		}
//		for (Entry<Long, SMap<String, String>> category : filterCategories.getCategories()) {
//			filterText.append(sep); sep = ", ";
//			filterText.append(category.getValue().tryGet(language, null));
//		}
		if (filterText.length() > 0) {
			filterLabel.setHTML(messages.filterMessage(filterText.toString())); 
			filterLabel.setVisible(true);
		} else {
			filterLabel.setVisible(false);
		}
	}
	
	protected void categoryCreated(Long storedItemId, Long parentId, SMap<String, String> categoryLabels, SMap<Long, SMap<String, String>> groups, SMap<Long, SMap<String, String>> parentExtentWithSelf, SMap<Long, SMap<String, String>> parents, SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> properties) {
		// insert row
		updateCategory(null, storedItemId, parentId, categoryLabels, groups, parentExtentWithSelf, parents, properties);
		
		setSelectedCategory(storedItemId, groups, parentExtentWithSelf, parents, properties); 
	}

	private List<CategoryRow> createRows() {
		List<CategoryRow> result = new ArrayList<CategoryRow>();
		
		convertToRows(model.getRootCategoryId(), result, 0);
		
		return result;
	}
	
	private void convertToRows(Long currentCategory, List<CategoryRow> result, int indentation) {
		List<Long> children = model.getChildrenByCategory().getAll(currentCategory);
		boolean isLeaf = children == null || children.size() == 0;

		// Add current:
		result.add(new CategoryRow(currentCategory, isLeaf, indentation, false));
		
		// Sort children:
		List<Long> sortedChildren = sortChildren(CollectionUtil.notNull(children));
		
		// iterate over children
		for (Long child : sortedChildren) {
			convertToRows(child, result, indentation + 1);
		}
	}

	private List<Long> sortChildren(Collection<Long> children) {
		List<Long> result = new ArrayList<Long>(children);
		
		Collections.sort(result, new Comparator<Long>() {
			public int compare(Long o1, Long o2) {
				String category1Label = model.getCategories().getOrEmpty(o1).tryGet(language, null);
				String category2Label = model.getCategories().getOrEmpty(o2).tryGet(language, null);
				
				// TODO what if labels are not available?
				if (category1Label == null) {
					return 1;
				}
				if (category2Label == null) {
					return -1;
				}
				return category1Label.compareTo(category2Label);
			}
		});
		
		return result;
	}


	private static class RowWidgets {
		public InlineLabel indentLabel;
		public Image hasChildrenImage;
		public Anchor categoryName;
	}
	
	private static class CategoryRow {
		public boolean isLeaf;
		public Long categoryId;
		public int indentation;
		public boolean isChanged;
		
		public CategoryRow(Long categoryId) {
			this(categoryId, false, -1, false);
		}
		
		public CategoryRow(Long categoryId, boolean isLeaf, int indentation, boolean isChanged) {
			this.categoryId = categoryId;
			this.isLeaf = isLeaf;
			this.indentation = indentation;
			this.isChanged = isChanged;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CategoryRow) {
				CategoryRow other = (CategoryRow) obj;
				
				return Objects.equal(this.categoryId, other.categoryId);
			}
			
			return false;
		}
		
		@Override
		public int hashCode() {
			return Objects.hashCode(categoryId);
		}

		private static int findRow(List<CategoryRow> rows, Long categoryId) {
			return rows.indexOf(new CategoryRow(categoryId));
		}
	}

}

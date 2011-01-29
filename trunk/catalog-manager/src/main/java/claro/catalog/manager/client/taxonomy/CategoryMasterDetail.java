package claro.catalog.manager.client.taxonomy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.cobogw.gwt.user.client.ui.RoundedPanel;

import claro.catalog.command.items.StoreProduct;
import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.data.RootProperties;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.GlobalStyles;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.ActionImage;
import claro.catalog.manager.client.widgets.CategoriesWidget;
import claro.jpa.catalog.OutputChannel;

import com.google.common.base.Objects;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.gwt.client.widgets.MasterDetail;
import easyenterprise.lib.gwt.client.widgets.Table;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.SMap;

abstract public class CategoryMasterDetail extends MasterDetail implements Globals {
	enum Styles implements Style { productMasterDetail, productprice, productname, product, productTD, productpanel }
	private static final int IMAGE_COL = 0;
	private static final int PRODUCT_COL = 1;
	private static final int PRICE_COL = 2;

	private static final int NR_COLS = 3;

	
	
	private List<Long> productKeys = new ArrayList<Long>();
	private SMap<Long, SMap<PropertyInfo, SMap<String, Object>>> products = SMap.empty();
	private String language;
	private String filterString;
	

	private OutputChannel outputChannel;



	private PropertyInfo nameProperty;
	private PropertyInfo variantProperty;
	private PropertyInfo descriptionProperty;
	private PropertyInfo priceProperty;
	private PropertyInfo artNoProperty;
	private PropertyInfo imageProperty;
	private PropertyInfo smallImageProperty;
	
	
	// Widgets
	private CategoriesWidget filterCategories;
	private List<RowWidgets> tableWidgets = new ArrayList<CategoryMasterDetail.RowWidgets>();

	protected HTML filterLabel;
	private CategoryDetails details;
	
	private PropertyGroupInfo generalGroup;
	private Long rootCategory;
	private SMap<String, String> rootCategoryLabels;
	
	private SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> newCategoryProperties;
	private SMap<Long, SMap<String, String>> newCategoryParents;
	private RoundedPanel masterRoundedPanel;
	private Long rootCategoryId;
	private SMap<Long, SMap<String, String>> categories;
	private SMap<Long, Long> childrenByCategory;
	private List<claro.catalog.manager.client.taxonomy.CategoryMasterDetail.CategoryRow> categoryRows = Collections.emptyList();


	public CategoryMasterDetail() {
		super(126, 0);
		StyleUtil.add(this, Styles.productMasterDetail);
		
	}
	
	public void setRootProperties(SMap<String, PropertyInfo> rootProperties, PropertyGroupInfo generalGroup, Long rootCategory, SMap<String, String> rootCategoryLabels) {
		this.generalGroup = generalGroup;
		this.rootCategory = rootCategory;
		this.rootCategoryLabels = rootCategoryLabels;
		this.nameProperty = rootProperties.get(RootProperties.NAME);
		this.variantProperty = rootProperties.get(RootProperties.VARIANT);
		this.descriptionProperty = rootProperties.get(RootProperties.DESCRIPTION);
		this.priceProperty = rootProperties.get(RootProperties.PRICE);
		this.artNoProperty = rootProperties.get(RootProperties.ARTICLENUMBER);
		this.imageProperty = rootProperties.get(RootProperties.IMAGE);
		this.smallImageProperty = rootProperties.get(RootProperties.SMALLIMAGE);
		
		render();
	}
	
	public void setCategoriesTree(Long rootCategoryId, SMap<Long, Long> children, SMap<Long, SMap<String, String>> categories) {
		this.rootCategoryId = rootCategoryId;
		this.categories = categories;
		this.childrenByCategory = children;
	
		this.categoryRows = createRows();
		
		render();
	}
	
	public void setProducts(SMap<Long, SMap<PropertyInfo, SMap<String, Object>>> products) {
		this.products = products;
		this.productKeys.clear();
		this.productKeys.addAll(products.getKeys());
		
		render();
	}
	
	public void setLanguage(String newLanguage) {
		language = newLanguage;
		
		render(); 
	}
	
	public String getLanguage() {
		return language;
	}
	
	public OutputChannel getOutputChannel() {
		return outputChannel;
	}
	
	
	public SMap<Long, SMap<String, String>> getFilterCategories() {
		if (filterCategories != null) {
			return filterCategories.getCategories();
		}
		return SMap.empty();
	}
	
	public String getFilter() {
		// TODO add drop down filter options:
		return filterString;
	}
	

	
	public void updateCategory(Long previousCategoryId, Long categoryId, SMap<PropertyInfo, SMap<String, Object>> masterValues, SMap<Long, SMap<String, String>> categories, SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> propertyValues, boolean setChangedStyle) {
		// Update product list:
		
//		
//		products = products.set(categoryId, masterValues);
//		
//		// Determine row to update
//		int itemRow = productKeys.indexOf(previousCategoryId);
//		if (itemRow != -1) {
//			// update row
//			productKeys.set(itemRow, categoryId);
//			
//			Table productTable = getMasterTable();
//			if (setChangedStyle) {
//				StyleUtil.add(productTable.getRowFormatter(), itemRow, CatalogManager.Styles.itemRowChanged);
//			}
//			
//			render(itemRow, categoryId);
//			
//			// Update details:
//			if (getCurrentRow() == itemRow) {
//				details.setItemData(categoryId, categories, propertyValues);
//			}
//			openDetail(itemRow);  // Redraw selection if necessary.
//			
//		} else {
//			productKeys.add(categoryId);
//		}
	}
	
	@Override
	protected final Widget tableCreated(Table table) {
		table.setStylePrimaryName(GlobalStyles.mainTable.toString());
		masterRoundedPanel = new RoundedPanel(table, RoundedPanel.ALL, 4) {{
			setBorderColor("white");
		}};
		return masterRoundedPanel;
	}


	@Override
	protected void masterPanelCreated(DockLayoutPanel masterPanel2) {
		Table productTable = getMasterTable();

		productTable.resizeColumns(NR_COLS);
		
		// search panel
		getMasterHeader().add(new RoundedPanel( RoundedPanel.ALL, 4) {{
			setBorderColor("white");
			
			add(new VerticalPanel() {{
				add(new Grid(2, 3) {{
					StyleUtil.add(this, CatalogManager.Styles.filterpanel);
					setWidget(0, 0, new ListBox() {{
						DOM.setInnerHTML(getElement(), "<option>Default</option><option>English</option><option>French</option><option>&nbsp;&nbsp;Shop</option><option>&nbsp;&nbsp;&nbsp;&nbsp;English</option><option>&nbsp;&nbsp;&nbsp;&nbsp;French</option>");
					}});
//					setWidget(0, 1, new TextBox() {{
//						addChangeHandler(new ChangeHandler() {
//							public void onChange(ChangeEvent event) {
//								filterString = getText();
//								updateFilterLabel();
//								updateCategories();
//							}
//						});
//					}});
//					setWidget(0, 2, filterCategories = new CategoriesWidget(false) {{
//							setData(SMap.<Long, SMap<String, String>>empty(), language);
//						}
//						protected String getAddCategoryTooltip() {
//							return messages.addCategoryFilter();
//						}
//						protected String getRemoveCategoryTooltip(String categoryName) {
//							return messages.removeCategoryFilterTooltip(categoryName);
//						}
//						protected void removeCategory(Long categoryId) {
//							super.removeCategory(categoryId);
//							updateCategories();
//						}
//						protected void addCategory(Long categoryId, SMap<String, String> labels) {
//							super.addCategory(categoryId, labels);
//							updateCategories();
//						}
//					});
					setWidget(1, 0, new Anchor(messages.newCategory()) {{
						addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								createNewCategory();
							}
						});
					}});
					setWidget(1, 1, new Anchor(messages.refresh()) {{
						addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								// TODO Invalidate cache.
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
			}}); 
		}});
	}
	
	@Override
	protected void detailPanelCreated(LayoutPanel detailPanel) {
		detailPanel.add(new DockLayoutPanel(Unit.PX) {{
			addNorth(new ActionImage(images.closeBlue(), new ClickHandler() {
					public void onClick(ClickEvent event) {
						closeDetail(true);
					}
				}) {{
					setStylePrimaryName(GlobalStyles.detailPanelCloseButton.toString());
				}}, 40);
			add(details = new CategoryDetails(language, outputChannel, nameProperty, variantProperty, priceProperty, imageProperty) {
				protected void storeItem(StoreProduct cmd) {
					CategoryMasterDetail.this.storeItem(cmd);
				}
			});
		}});
	}

	private void render() {
		
		// Make sure we have the root properties:
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
				// Indent label
				add(rowWidgets.indentLabel = new InlineLabel());
				
				// Image
				add(rowWidgets.hasChildrenImage = new Image(images.nonLeafImage()));
				
				// Anchor
				add(rowWidgets.categoryName = new Anchor() {{
					StyleUtil.add(this, Styles.productname);
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							rowSelected(row);
						}
					});
				}});
			}});
			
//			categoryTable.getWidget(i, PRODUCT_COL).getElement().getParentElement().addClassName(Styles.productTD.toString());
		}
		

		// (Re) bind widgets:
		
		
		int i = 0;
		for (CategoryRow categoryRow : categoryRows) {
			StyleUtil.remove(categoryTable.getRowFormatter(), i, CatalogManager.Styles.itemRowChanged);

			render(i, categoryRow);
			
			i++;
		}
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
		String label = categories.getOrEmpty(categoryRow.categoryId).tryGet(language, null);
		rowWidgets.categoryName.setText(label);
	}
	
	
	private void rowSelected(int row) {

		CategoryRow selectedCategory = categoryRows.get(row);
		if (selectedCategory.categoryId == null) {
			// New category selected, only update locally.
			setSelectedCategory(null, null, null, newCategoryParents, newCategoryProperties); // TODO What groups/parentextent to supply?
		} else {
			categorySelected(selectedCategory.categoryId);
		}
	}
	
	abstract protected void categorySelected(Long productId);

	
	public void setSelectedCategory(Long categoryId, SMap<Long, SMap<String, String>> groups, SMap<Long, SMap<String, String>> parentExtentWithSelf, SMap<Long, SMap<String, String>> parents, SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> properties) {
		
		int row = categoryRows.indexOf(new CategoryRow(categoryId, false, 0));

		openDetail(row);

		details.setItemData(categoryId, groups, parentExtentWithSelf, parents, properties);
	}
	
	abstract protected void updateCategories();
	
	abstract protected void storeItem(StoreProduct cmd);

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


	@SuppressWarnings("serial")
	private void createNewCategory() {
		// TODO Maybe replace with server roundtrip???

		newCategoryParents = SMap.empty();
		
		final Object newProductText = messages.newProduct();
		
		SMap<PropertyInfo, PropertyData> propertyMap = SMap.empty();
		propertyMap = propertyMap.add(nameProperty, new PropertyData() {{
			values = SMap.create(outputChannel, SMap.create(language, newProductText));
		}});
		propertyMap = propertyMap.add(variantProperty, new PropertyData());
		propertyMap = propertyMap.add(descriptionProperty, new PropertyData());
		propertyMap = propertyMap.add(artNoProperty, new PropertyData());
		propertyMap = propertyMap.add(imageProperty, new PropertyData());
		propertyMap = propertyMap.add(smallImageProperty, new PropertyData());
		
		if (!productKeys.contains(null)) {
			// Only add new product once...
			productKeys.add(0, null);
		}
		products = products.set(null, SMap.create(nameProperty, SMap.create(language, newProductText)));
		
		render();

		newCategoryProperties = SMap.create(generalGroup, propertyMap);
		setSelectedCategory(null, null, null, newCategoryParents, newCategoryProperties); // TODO What group/parentextent??
	}

	private List<CategoryRow> createRows() {
		List<CategoryRow> result = new ArrayList<CategoryRow>();
		
		convertToRows(rootCategoryId, result, 0);
		
		return result;
	}
	
	private void convertToRows(Long currentCategory, List<CategoryRow> result, int indentation) {
		List<Long> children = childrenByCategory.getAll(currentCategory);
		boolean isLeaf = children == null || children.size() == 0;

		// Add current:
		result.add(new CategoryRow(currentCategory, isLeaf, indentation));
		
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
				String category1Label = categories.getOrEmpty(o1).tryGet(language, null);
				String category2Label = categories.getOrEmpty(o2).tryGet(language, null);
				
				// TODO what if labels are not available?
				
				return category1Label.compareTo(category2Label);
			}
		});
		
		return result;
	}


	private class RowWidgets {
		public InlineLabel indentLabel;
		public Image hasChildrenImage;
		public Anchor categoryName;
	}
	
	private class CategoryRow {
		public boolean isLeaf;
		public Long categoryId;
		public int indentation;
		
		public CategoryRow(Long categoryId, boolean isLeaf, int indentation) {
			this.categoryId = categoryId;
			this.isLeaf = isLeaf;
			this.indentation = indentation;
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
	}

}

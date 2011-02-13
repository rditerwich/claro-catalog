package claro.catalog.manager.client.catalog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import claro.catalog.command.items.PerformStaging;
import claro.catalog.command.items.StoreItemDetails;
import claro.catalog.data.MediaValue;
import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.data.RootProperties;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.command.StatusCallback;
import claro.catalog.manager.client.widgets.CatalogManagerMasterDetail;
import claro.catalog.manager.client.widgets.CategoriesWidget;
import claro.catalog.manager.client.widgets.ConfirmationDialog;
import claro.catalog.manager.client.widgets.LanguageAndShopSelector;
import claro.catalog.manager.client.widgets.MediaWidget;

import com.google.common.base.Objects;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.gwt.client.widgets.EEButton;
import easyenterprise.lib.gwt.client.widgets.MoneyFormatUtil;
import easyenterprise.lib.gwt.client.widgets.Table;
import easyenterprise.lib.util.Money;
import easyenterprise.lib.util.SMap;

abstract public class ProductMasterDetail extends CatalogManagerMasterDetail implements Globals {
	enum Styles implements Style { productMasterDetail, productprice, productname, product, productTD, productpanel }
	private static final int IMAGE_COL = 0;
	private static final int PRODUCT_COL = 1;
	private static final int PRICE_COL = 2;

	private static final int NR_COLS = 3;

	private List<ProductRow> productRows = new ArrayList<ProductRow>();
	private SMap<Long, SMap<PropertyInfo, SMap<String, Object>>> products = SMap.empty();
	private String filterString;
	
	private PropertyInfo nameProperty;
	private PropertyInfo variantProperty;
	private PropertyInfo descriptionProperty;
	private PropertyInfo priceProperty;
	private PropertyInfo artNoProperty;
	private PropertyInfo imageProperty;
	private PropertyInfo smallImageProperty;
	
	
	// Widgets
	private CategoriesWidget filterCategories;
	private LanguageAndShopSelector languageSelection;
	private List<RowWidgets> tableWidgets = new ArrayList<ProductMasterDetail.RowWidgets>();

	private Label noProductsFoundLabel;
	protected HTML filterLabel;
	private ProductDetails details;
	
	private Long rootCategory;
	private Table productTable;
	private Label pleaseWaitLabel;
	private final CatalogPageModel model;
	
	public ProductMasterDetail(CatalogPageModel model) {
		super(150);
		this.model = model;
		StyleUtil.addStyle(this, Styles.productMasterDetail);
		createMasterPanel();
		createDetailPanel();
	}
	
	public void setRootProperties(SMap<String, PropertyInfo> rootProperties, PropertyGroupInfo generalGroup, Long rootCategory, SMap<String, String> rootCategoryLabels) {
		this.rootCategory = rootCategory;
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
	
	public void setProducts(SMap<Long, SMap<PropertyInfo, SMap<String, Object>>> products) {
		this.products = products;
		this.productRows = convertProductKeys(products.getKeys());
		
		render();
		
		// Did we have a selection?  
		if (model.getSelectedProductId() != null) {
			
			// Try to find it back
			int row = ProductRow.findRow(productRows, model.getSelectedProductId());
			
			// Reselect it.
			rowSelected(row);
		}
	}
	
	public SMap<Long, SMap<String, String>> getFilterCategories() {
		if (filterCategories != null) {
			return filterCategories.getCategories();
		}
		return SMap.empty();
	}
	

	public void refreshLanguages() {
		if (languageSelection != null) {
			languageSelection.refreshData();
		}
	}
	
	public String getFilter() {
		// TODO add drop down filter options:
		return filterString;
	}
	
	public List<Long> getOrderByIds() {
		if (nameProperty != null) {
			return Collections.singletonList(nameProperty.propertyId);
		}
		return Collections.emptyList();
	}

	
//	@Override
//	protected int getExtraTableWidth() {
//		return 24; // Compensation for the rounded panel
//	}
//	
//	@Override
//	protected int getExtraTableHeight() {
//		return 48; // Compensation for the rounded panel
//	}
	protected void createMasterPanel() {
		productTable = new Table();

		productTable.resizeColumns(NR_COLS);
		productTable.setHeaderText(0, 1, messages.product());
		productTable.setHeaderText(0, 2, messages.price());
		setMaster(productTable);
		
		// search panel
		setHeader(new HorizontalPanel() {{
			add(new VerticalPanel() {{
				add(new Grid(2, 3) {{
					StyleUtil.addStyle(this, CatalogManager.Styles.filterpanel);
					setWidget(0, 0, languageSelection = new LanguageAndShopSelector() {
						protected void selectionChanged() {
							model.setSelectedLanguage(getSelectedLanguage());
							model.setSelectedShop(getSelectedShop());
							updateProductList();
						}
					});
					setWidget(0, 1, new TextBox() {{
						addChangeHandler(new ChangeHandler() {
							public void onChange(ChangeEvent event) {
								filterString = getText();
								updateFilterLabel();
								updateProductList();
							}
						});
					}});
					setWidget(0, 2, filterCategories = new CategoriesWidget(false) {{
							setData(SMap.<Long, SMap<String, String>>empty(), model.getSelectedLanguage());
						}
						protected String getAddCategoryLabel() {
							return messages.addCategoriesLink();
						};
						protected String getAddCategoryTooltip() {
							return messages.addCategoryFilter();
						}
						protected String getRemoveCategoryTooltip(String categoryName) {
							return messages.removeCategoryFilterTooltip(categoryName);
						}
						protected void removeCategory(Long categoryId) {
							super.removeCategory(categoryId);
							updateProductList();
						}
						protected void addCategory(Long categoryId, SMap<String, String> labels) {
							super.addCategory(categoryId, labels);
							updateProductList();
						}
					});
					setWidget(1, 0, new EEButton(messages.newProduct()) {{
						addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								createNewProduct(rootCategory);
							}
						});
					}});
					setWidget(1, 1, new Anchor(messages.refresh()) {{
						addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								languageSelection.refreshData();
								updateProductList();
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
			add(new VerticalPanel() {{
				add(new Anchor(messages.publishToPreviewButton()) {{
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							new ConfirmationDialog(messages.publishToPreviewConfirmation(), images.warning()) {
								@Override
								protected void yesPressed() {
									PerformStaging command = new PerformStaging();
									command.catalogId = CatalogManager.getCurrentCatalogId();
									command.toStagingName = PerformStaging.STAGING_AREA_PREVIEW;
									GwtCommandFacade.execute(command, new StatusCallback<PerformStaging.Result>() {});
								}
							}.show();
						}
					});
				}});
				add(new Anchor(messages.publishPreviewToProductionButton()) {{
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							new ConfirmationDialog(messages.publishPreviewToProductionConfirmation(), images.warning()) {
								@Override
								protected void yesPressed() {
									PerformStaging command = new PerformStaging();
									command.catalogId = CatalogManager.getCurrentCatalogId();
									command.fromStagingName = PerformStaging.STAGING_AREA_PREVIEW;
									command.toStagingName = PerformStaging.STAGING_AREA_PUBLISHED;
									GwtCommandFacade.execute(command, new StatusCallback<PerformStaging.Result>() {});
								}
							}.show();
						}
					});
				}});
			}});
		}});

		noProductsFoundLabel = new Label(messages.noProductsFound()) {{
			setVisible(false);
		}};
	}
	
	protected void createDetailPanel() {
		setDetail(new LayoutPanel() {{
			add(details = new ProductDetails(nameProperty, variantProperty, priceProperty, imageProperty) {
				protected void storeItem(StoreItemDetails cmd) {
					ProductMasterDetail.this.storeItem(cmd);
				}
			});
		}});
		details.setModel(model);
	}
	
	
	protected void updateProduct(Long previousProductId, Long productId, SMap<PropertyInfo, SMap<String, Object>> masterValues, SMap<Long, SMap<String, String>> categories, SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> propertyValues) {
		
		// If we did not remove,
		if (productId != null) {
			// Update product list:
			products = products.set(productId, masterValues);
		}
		
		// Determine row to update
		int itemRow = productRows.indexOf(new ProductRow(previousProductId));
		if (productId == null) {
			// Remove row
			productRows.remove(itemRow);
			render();
			closeDetail();
		} else if (itemRow != -1) {
			// update row
			ProductRow productRow = productRows.get(itemRow);
			productRow.productId = productId;
			productRow.isChanged = true;
			
			render(itemRow, productRow);
			
//			masterChanged();
		} else {
			itemRow = 0;
			productRows.add(itemRow, new ProductRow(productId, true));
			
			render();
		}
		
		// Update details:
		if (getCurrentRow() == itemRow) {
			setCurrentRow(itemRow);  // Redraw selection if necessary.
			details.setItemData(productId, categories, propertyValues);
		}
	}
	
	
	private void render() {
		
		// Make sure we have the root properties:
		pleaseWaitLabel.setVisible(nameProperty == null);
		if (nameProperty == null) {
			return;
		}
		
		if (productRows.isEmpty()) {
			setMainWidget(noProductsFoundLabel);
		} else {
			setMaster(productTable);
		}
		
		noProductsFoundLabel.setVisible(productRows.isEmpty());
		updateFilterLabel();


		// Delete/Create widgets as necessary:
		int oldRowCount = productTable.getRowCount();
		
		// Delete old rows:
		// TODO What if selected row is deleted?  maybe close detail?
		
		productTable.resizeRows(productRows.size());
		for (int i = tableWidgets.size() - 1; i >= productRows.size(); i--) {
			tableWidgets.remove(i);
		}
		
		// Create new Rows
		for (int i = oldRowCount; i < productRows.size(); i++) {
			final RowWidgets rowWidgets = new RowWidgets();
			tableWidgets.add(rowWidgets);
			final int row = i;
			
			// Image
			productTable.setWidget(i, IMAGE_COL, rowWidgets.imageWidget = new MediaWidget(false, false) {{
				addRowSelectionListener(this, row);
			}});
			
			// Product
			productTable.setWidget(i, PRODUCT_COL, new VerticalPanel() {{
				StyleUtil.addStyle(this, Styles.product);
				// .title -> name
				add(rowWidgets.productNameLabel = new Anchor() {{
					StyleUtil.addStyle(this, Styles.productname);
					addRowSelectionListener(this, row);
				}});
				// .subtitle -> variant
				add(rowWidgets.productVariantLabel = new InlineLabel() {{
					StyleUtil.addStyle(this, CatalogManager.Styles.productvariant);
					addRowSelectionListener(this, row);
				}});
				
				// .body -> artnr
				add(rowWidgets.productNrLabel = new InlineLabel() {{
					addRowSelectionListener(this, row);
				}});
				// .body -> description
				add(rowWidgets.productDescriptionLabel = new InlineLabel() {{
					addRowSelectionListener(this, row);
				}});
			}});
			productTable.getWidget(i, PRODUCT_COL).getElement().getParentElement().addClassName(Styles.productTD.toString());
			// Price
			productTable.setWidget(i, PRICE_COL, rowWidgets.priceLabel = new Label() {{
				StyleUtil.addStyle(this, Styles.productprice);
				addRowSelectionListener(this, row);
			}});
		}
		

		// (Re) bind widgets:
		
		
		int i = 0;
		for (ProductRow productRow : productRows) {

			render(i, productRow);
			
			i++;
		}
		
//		masterChanged();
	}

	private void render(int i, ProductRow productRow) {
		RowWidgets rowWidgets = tableWidgets.get(i);
		
		SMap<PropertyInfo, SMap<String, Object>> properties = products.getOrEmpty(productRow.productId);
		
		// image
		Object image = properties.getOrEmpty(smallImageProperty).tryGet(model.getSelectedLanguage(), null);
		if (MediaValue.mediaIsNull(image)) {
			image = properties.getOrEmpty(imageProperty).tryGet(model.getSelectedLanguage(), null);
		}
		if (image instanceof MediaValue) {
			final MediaValue value = (MediaValue)image;
			rowWidgets.imageWidget.setData(value.propertyValueId, value.mimeType, value.filename);
		} else {
			rowWidgets.imageWidget.setData(null, null, null);  // Clear any previous images.
		}
		
		// product
		Object productName = properties.getOrEmpty(nameProperty).tryGet(model.getSelectedLanguage(), null);
		rowWidgets.productNameLabel.setText(productName instanceof String ? productName.toString() : "<" + messages.noProductNameSet() + ">");
		
		final Object productVariant = properties.getOrEmpty(variantProperty).tryGet(model.getSelectedLanguage(), null);
		rowWidgets.productVariantLabel.setText(productVariant instanceof String ? productVariant.toString() : "");
		
		final Object productNr = properties.getOrEmpty(artNoProperty).tryGet(model.getSelectedLanguage(), null);
		rowWidgets.productNrLabel.setText(productNr instanceof String ?  "Art. nr. " + productNr.toString() : "");
		
		final Object productDescription = properties.getOrEmpty(descriptionProperty).tryGet(model.getSelectedLanguage(), null);
		rowWidgets.productDescriptionLabel.setText(productDescription instanceof String ? productDescription.toString() : "");
		
		// price
		final Object price = properties.getOrEmpty(priceProperty).tryGet(model.getSelectedLanguage(), null);
		if (price != null) {
			// TODO Use locale in the following format??
			rowWidgets.priceLabel.setText(MoneyFormatUtil.full((Money) price));
		} else {
			rowWidgets.priceLabel.setText("");
		}

		if (productRow.isChanged) {
			StyleUtil.add(productTable.getRowFormatter(), i, CatalogManager.Styles.itemRowChanged);
		} else {
			StyleUtil.remove(productTable.getRowFormatter(), i, CatalogManager.Styles.itemRowChanged);
		}
	}
	

	public void productCreated(Long productId, SMap<PropertyInfo, SMap<String, Object>> masterValues, SMap<Long, SMap<String, String>> categories, SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> propertyValues) {
		updateProduct(null, productId, masterValues, categories, propertyValues);
		setSelectedProduct(productId, categories, propertyValues);
	}
	
	public void setSelectedProduct(Long productId, SMap<Long, SMap<String, String>> categories, SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> propertyValues) {
		int row = productRows.indexOf(new ProductRow(productId));
		
		model.setSelectedProductId(productId);

		int oldRow = getCurrentRow();
		
		setCurrentRow(row);

		details.setItemData(productId, categories, propertyValues);
		
		if (oldRow != row) {
			details.resetTabState();
		}
	}
	

	private void closeDetail() {
		model.setSelectedProductId(null);
		closeDetail(true);
	}

	
	abstract protected void createNewProduct(Long parentId);
	
	abstract protected void updateProductList();
	
	abstract protected void productSelected(Long productId);
	
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
		for (Entry<Long, SMap<String, String>> category : filterCategories.getCategories()) {
			filterText.append(sep); sep = ", ";
			filterText.append(category.getValue().tryGet(model.getSelectedLanguage(), null));
		}
		if (filterText.length() > 0) {
			filterLabel.setHTML(messages.filterMessage(filterText.toString())); 
			filterLabel.setVisible(true);
		} else {
			filterLabel.setVisible(false);
		}
	}

	private void rowSelected(int row) {
		if (row < 0) {
			closeDetail();
		} else {
			ProductRow product = productRows.get(row);
			productSelected(product.productId);
		}
	}
	
	private void addRowSelectionListener(HasClickHandlers widget, final int row) {
		widget.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				rowSelected(row);
				openDetail(row);
				// prevent detail to close immediately
				event.stopPropagation();
			}
		});
	}
	
	
	private static List<ProductRow> convertProductKeys(List<Long> keys) {
		List<ProductRow> result = new ArrayList<ProductRow>();
		for (Long key : keys) {
			result.add(new ProductRow(key, false));
		}
		return result;
	}
	
	
	private static class ProductRow {
		public Long productId;
		public boolean isChanged;
		
		public static int findRow(List<ProductRow> rows, Long productId) {
			return rows.indexOf(new ProductRow(productId));
		}
		public ProductRow(Long productId) {
			this(productId, false);
		}
		public ProductRow(Long productId, boolean isChanged) {
			this.productId = productId;
			this.isChanged = isChanged;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ProductRow) {
				ProductRow other = (ProductRow) obj;
				return Objects.equal(this.productId, other.productId);
			}
			return super.equals(obj);
		}
		
		@Override
		public int hashCode() {
			return Objects.hashCode(productId);
		}
	}

	private class RowWidgets {
		public Label priceLabel;
		public MediaWidget imageWidget;
		public Anchor productNameLabel;
		public Label productVariantLabel;
		public Label productNrLabel;
		public Label productDescriptionLabel;
		
	}

}

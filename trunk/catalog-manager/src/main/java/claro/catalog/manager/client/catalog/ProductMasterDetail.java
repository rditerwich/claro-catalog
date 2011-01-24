package claro.catalog.manager.client.catalog;

import static claro.catalog.manager.client.CatalogManager.propertyStringConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.cobogw.gwt.user.client.ui.RoundedPanel;

import claro.catalog.command.items.StoreProduct;
import claro.catalog.data.MediaValue;
import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.data.RootProperties;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.GlobalStyles;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.CatalogManager.Styles;
import claro.catalog.manager.client.widgets.CategoriesWidget;
import claro.catalog.manager.client.widgets.MediaWidget;
import claro.catalog.manager.client.widgets.StatusMessage;
import claro.jpa.catalog.OutputChannel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.gwt.client.widgets.MasterDetail;
import easyenterprise.lib.gwt.client.widgets.MoneyFormatUtil;
import easyenterprise.lib.gwt.client.widgets.Table;
import easyenterprise.lib.util.Money;
import easyenterprise.lib.util.SMap;

abstract public class ProductMasterDetail extends MasterDetail implements Globals {
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
	private List<RowWidgets> tableWidgets = new ArrayList<ProductMasterDetail.RowWidgets>();

	private Label noProductsFoundLabel;
	protected HTML filterLabel;
	private ProductDetails details;
	
	private PropertyGroupInfo generalGroup;
	private Long rootCategory;
	private SMap<String, String> rootCategoryLabels;
	
	private SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> newProductPropertyValues;
	private SMap<Long, SMap<String, String>> newProductCategories;
	private RoundedPanel masterRoundedPanel;


	public ProductMasterDetail() {
		super(80, 0);
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
	

	
	public void updateProduct(Long previousProductId, Long productId, SMap<PropertyInfo, SMap<String, Object>> masterValues, SMap<Long, SMap<String, String>> categories, SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> propertyValues, boolean setChangedStyle) {
		// Update product list:
		products = products.set(productId, masterValues);
		
		// Determine row to update
		int itemRow = productKeys.indexOf(previousProductId);
		if (itemRow != -1) {
			// update row
			productKeys.set(itemRow, productId);
			
			Table productTable = getMasterTable();
			if (setChangedStyle) {
				StyleUtil.add(productTable.getRowFormatter(), itemRow, CatalogManager.Styles.itemRowChanged);
			}
			
			rebind(itemRow, productId);
			
			// Update details:
			if (getCurrentRow() == itemRow) {
				details.setItemData(productId, categories, propertyValues);
			}
			openDetail(itemRow);  // Redraw selection if necessary.
			
		} else {
			productKeys.add(productId);
		}
	}
	
	public void render() {
		
		// Make sure we have the root properties:
		if (nameProperty == null) {
			return;
		}
		
		
		Table productTable = getMasterTable();
		
		if (productKeys.isEmpty()) {
			masterRoundedPanel.setWidget(noProductsFoundLabel);
		} else {
			masterRoundedPanel.setWidget(getMasterTable());
		}
		
		noProductsFoundLabel.setVisible(productKeys.isEmpty());
		updateFilterLabel();


		// Delete/Create widgets as necessary:
		int oldRowCount = productTable.getRowCount();
		
		// Delete old rows:
		// TODO What if selected row is deleted?  maybe close detail?
		
		productTable.resizeRows(productKeys.size());
		for (int i = tableWidgets.size() - 1; i >= productKeys.size(); i--) {
			tableWidgets.remove(i);
		}
		
		// Create new Rows
		for (int i = oldRowCount; i < productKeys.size(); i++) {
			final RowWidgets rowWidgets = new RowWidgets();
			tableWidgets.add(rowWidgets);
			final int row = i;
			
			// Image
			productTable.setWidget(i, IMAGE_COL, rowWidgets.imageWidget = new MediaWidget(false, false) {{
				addRowSelectionListener(this, row);
			}});
			
			// Product
			productTable.setWidget(i, PRODUCT_COL, new VerticalPanel() {{
				StyleUtil.add(this, Styles.product);
				// .title -> name
				add(rowWidgets.productNameLabel = new Anchor() {{
					StyleUtil.add(this, Styles.productname);
					addRowSelectionListener(this, row);
				}});
				// .subtitle -> variant
				add(rowWidgets.productVariantLabel = new InlineLabel() {{
					StyleUtil.add(this, CatalogManager.Styles.productvariant);
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
				StyleUtil.add(this, Styles.productprice);
				addRowSelectionListener(this, row);
			}});
		}
		

		// (Re) bind widgets:
		
		
		int i = 0;
		for (Long productId : productKeys) {
			StyleUtil.remove(productTable.getRowFormatter(), i, CatalogManager.Styles.itemRowChanged);

			rebind(i, productId);
			
			i++;
		}
	}

	private void rebind(int i, Long productId) {
		RowWidgets rowWidgets = tableWidgets.get(i);
		
		SMap<PropertyInfo, SMap<String, Object>> properties = products.tryGet(productId);
		
		// image
		Object image = properties.getOrEmpty(smallImageProperty).tryGet(language, null);
		if (image == null) {
			image = properties.getOrEmpty(imageProperty).tryGet(language, null);
		}
		if (image instanceof MediaValue) {
			final MediaValue value = (MediaValue)image;
			rowWidgets.imageWidget.setData(value.propertyValueId, value.mimeType, value.filename);
			
		} else {
			rowWidgets.imageWidget.setData(null, null, null);  // Clear any previous images.
		}
		
		// product
		final Object productName = properties.getOrEmpty(nameProperty).tryGet(language, null);
		rowWidgets.productNameLabel.setText(productName instanceof String ? productName.toString() : "");
		
		final Object productVariant = properties.getOrEmpty(variantProperty).tryGet(language, null);
		rowWidgets.productVariantLabel.setText(productVariant instanceof String ? productVariant.toString() : "");
		
		final Object productNr = properties.getOrEmpty(artNoProperty).tryGet(language, null);
		rowWidgets.productNrLabel.setText(productNr instanceof String ?  "Art. nr. " + productNr.toString() : "");
		
		final Object productDescription = properties.getOrEmpty(descriptionProperty).tryGet(language, null);
		rowWidgets.productDescriptionLabel.setText(productDescription instanceof String ? productDescription.toString() : "");
		
		// price
		final Object price = properties.getOrEmpty(priceProperty).tryGet(language, null);
		if (price != null) {
			// TODO Use locale in the following format??
			rowWidgets.priceLabel.setText(MoneyFormatUtil.full((Money) price));
		} else {
			rowWidgets.priceLabel.setText("");
		}
	}
	
	public void setSelectedProduct(Long productId, SMap<Long, SMap<String, String>> categories, SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> propertyValues) {
		int row = productKeys.indexOf(productId);

		openDetail(row);

		details.setItemData(productId, categories, propertyValues);
		
	}
	
	abstract protected void updateProductList();
	
	abstract protected void productSelected(Long productId);
	
	
	@Override
	protected void masterPanelCreated(DockLayoutPanel masterPanel2) {
		Table productTable = getMasterTable();

		productTable.resizeColumns(NR_COLS);
		productTable.setHeaderText(0, 1, messages.product());
		productTable.setHeaderText(0, 2, messages.price());
		
		// search panel
		getMasterHeader().add(new RoundedPanel( RoundedPanel.ALL, 4) {{
			setBorderColor("white");
			
			add(new VerticalPanel() {{
				add(new Grid(2, 3) {{
					StyleUtil.add(this, CatalogManager.Styles.filterpanel);
					setWidget(0, 0, new ListBox() {{
						DOM.setInnerHTML(getElement(), "<option>Default</option><option>English</option><option>French</option><option>&nbsp;&nbsp;Shop</option><option>&nbsp;&nbsp;&nbsp;&nbsp;English</option><option>&nbsp;&nbsp;&nbsp;&nbsp;French</option>");
					}});
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
							setData(SMap.<Long, SMap<String, String>>empty(), language);
						}
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
					setWidget(1, 0, new Anchor(messages.newProduct()) {{
						addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								createNewProduct();
							}
						});
					}});
					setWidget(1, 1, new Anchor(messages.refresh()) {{
						addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
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
			}}); 
		}});
		noProductsFoundLabel = new Label(messages.noProductsFound()) {{
			setVisible(false);
		}};
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
	protected void detailPanelCreated(LayoutPanel detailPanel) {
		detailPanel.add(new DockLayoutPanel(Unit.PX) {{
			addNorth(new Anchor("Close") {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						closeDetail(true);
					}
				});
			}}, 40);
			add(details = new ProductDetails(language, outputChannel, nameProperty, variantProperty, priceProperty, imageProperty) {
				protected void storeItem(StoreProduct cmd) {
					ProductMasterDetail.this.storeItem(cmd);
				}
			});
		}});
	}
	
	abstract protected void storeItem(StoreProduct cmd);

	private void updateFilterLabel() {
		String actualFilter = getFilter();
		if (actualFilter != null && !actualFilter.trim().equals("")) {
			filterLabel.setHTML(messages.filterMessage(actualFilter)); 
			filterLabel.setVisible(true);
		} else {
			filterLabel.setVisible(false);
		}
	}


	@SuppressWarnings("serial")
	private void createNewProduct() {
		// TODO Maybe replace with server roundtrip???

		newProductCategories = SMap.empty();
		
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

		newProductPropertyValues = SMap.create(generalGroup, propertyMap);
		setSelectedProduct(null, newProductCategories, newProductPropertyValues);
	}
	
	private void rowSelected(int row) {

		Long productId = productKeys.get(row);
		if (productId == null) {
			// New product selected, only update locally.
			setSelectedProduct(null, newProductCategories, newProductPropertyValues);
		} else {
			productSelected(productId);
		}
	}
	
	private void addRowSelectionListener(HasClickHandlers widget, final int row) {
		widget.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				rowSelected(row);
			}
		});
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

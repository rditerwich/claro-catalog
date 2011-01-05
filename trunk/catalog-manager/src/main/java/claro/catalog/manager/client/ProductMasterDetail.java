package claro.catalog.manager.client;

import static claro.catalog.manager.client.CatalogManager.propertyStringConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import claro.catalog.data.MediaValue;
import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.data.RootProperties;
import claro.catalog.manager.client.widgets.MediaWidget;
import claro.catalog.manager.client.widgets.StatusMessage;
import claro.jpa.catalog.OutputChannel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.gwt.client.widgets.MasterDetail;
import easyenterprise.lib.gwt.client.widgets.Table;
import easyenterprise.lib.util.SMap;

abstract public class ProductMasterDetail extends MasterDetail implements Globals {
	enum Styles implements Style { productMasterDetail, productprice, productname, product, productpanel }
	private static final int IMAGE_COL = 0;
	private static final int PRODUCT_COL = 1;
	private static final int PRICE_COL = 2;

	private static final int NR_COLS = 3;

	
	
	private SMap<Long, SMap<PropertyInfo, SMap<String, Object>>> products = SMap.empty();
	private SMap<Long, SMap<String, String>> filterCategories = SMap.empty();
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
	private List<RowWidgets> tableWidgets = new ArrayList<ProductMasterDetail.RowWidgets>();

	private Label noProductsFoundLabel;
	protected HTML filterLabel;
	private ProductDetails details;


	public ProductMasterDetail() {
		super(80, 0);
		StyleUtil.add(this, Styles.productMasterDetail);
		
	}
	
	public void setRootProperties(SMap<String, PropertyInfo> rootProperties) {
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
		
		render();
	}
	
	public void setLanguage(String newLanguage) {
		language = newLanguage;
		
		render(); 
	}
	
	
	public SMap<Long, SMap<String, String>> getFilterCategories() {
		return filterCategories;
	}
	
	public String getFilter() {
		// TODO add drop down filter options:
		return filterString;
	}
	

	
	public void updateProduct(Long itemId, SMap<PropertyInfo, SMap<String, Object>> newValues, boolean setChangedStyle) {
		// Update product list:
		products = products.add(itemId, newValues);
		int itemRow = products.getKeys().indexOf(itemId);
		
		Table productTable = getMasterTable();
		if (setChangedStyle) {
			StyleUtil.remove(productTable.getRowFormatter(), itemRow, CatalogManager.Styles.itemRowChanged);
		}
		
		rebind(itemRow, itemId);
	}
	
	public void render() {
		
		// Make sure we have the root properties:
		if (nameProperty == null) {
			return;
		}
		
		
		Table productTable = getMasterTable();
		
		noProductsFoundLabel.setVisible(products == null || products.isEmpty());
		updateFilterLabel();


		
		List<Long> productKeys = products != null ? products.getKeys() : Collections.<Long>emptyList();

		
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
			productTable.setWidget(i, IMAGE_COL, rowWidgets.imageWidget = new MediaWidget(false) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						rowSelected(row);
					}
				});
			}});
			
			// Product
			productTable.setWidget(i, PRODUCT_COL, new VerticalPanel() {{
				StyleUtil.add(this, Styles.product);
				// .title -> name
				add(rowWidgets.productNameLabel = new InlineLabel() {{
					StyleUtil.add(this, Styles.productname);
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							rowSelected(row);
						}
					});
				}});
				// .subtitle -> variant
				add(rowWidgets.productVariantLabel = new InlineLabel() {{
					StyleUtil.add(this, CatalogManager.Styles.productvariant);
				}});
				
				// .body -> artnr
				add(rowWidgets.productNrLabel = new InlineLabel());
				// .body -> description
				add(rowWidgets.productDescriptionLabel = new InlineLabel());
			}});
			
			// Price
			productTable.setWidget(i, PRICE_COL, rowWidgets.priceLabel = new Label() {{
				StyleUtil.add(this, Styles.productprice);
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
			rowWidgets.priceLabel.setText(propertyStringConverter.toString(priceProperty.type, price));
		} else {
			rowWidgets.priceLabel.setText("");
		}
	}
	
	public void setSelectedProduct(Long productId, SMap<Long, SMap<String, String>> categories, SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> propertyValues) {
		int row = products.getKeys().indexOf(productId);

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
		getMasterHeader().add(new VerticalPanel() {{
			add(new Grid(2, 3) {{
				StyleUtil.add(this, CatalogManager.Styles.filterpanel);
				setWidget(0, 0, new ListBox() {{
					addItem("Default");
					addItem("English");
					addItem("French");
					addItem("\tShop");
					addItem("\t\tEnglish");
					addItem("\t\tFrench");
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
				setWidget(0, 2, new CategoriesWidget(false) {{
						setData(filterCategories, language);
					}
					protected String getAddCategoryTooltip() {
						return messages.addCategoryFilter();
					}
					protected String getRemoveCategoryTooltip(String categoryName) {
						return messages.removeCategoryFilterTooltip(categoryName);
					}
					// TODO add.
					protected void removeCategory(Long categoryId) {
						// TODO update filtercats.
					}
				});
				setWidget(1, 0, new Anchor(messages.newProduct()));
			}});
			add(new FlowPanel() {{
				add(filterLabel = new HTML() {{
					setVisible(false); 
				}});
				add(noProductsFoundLabel = new Label(messages.noProductsFound()) {{
					setVisible(false);
				}});
			}});
		}});
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
			}}, 50);
			add(details = new ProductDetails(language, outputChannel, nameProperty, variantProperty, priceProperty, imageProperty) {
				protected void propertyValueSet(Long itemId, PropertyInfo propertyInfo, String language, Object value) {
					// Call Server method
					// Queueing?
					// Use result of server command to update both product and list.
					// TODO Implement.
				}
				protected void propertyValueRemoved(Long itemId, PropertyInfo propertyInfo, String language) {
					// TODO Auto-generated method stub
					
				}
				protected void categoryRemoved(Long itemId, Long categoryId) {
					// TODO Auto-generated method stub
				}
			});
		}});
	}

	private void updateFilterLabel() {
		String actualFilter = getFilter();
		if (actualFilter != null && !actualFilter.trim().equals("")) {
			filterLabel.setHTML(messages.filterMessage(actualFilter)); 
			filterLabel.setVisible(true);
		} else {
			filterLabel.setVisible(false);
		}
	}


	

	private void rowSelected(int row) {
		StatusMessage.get().show(messages.loadingProductDetails());

		productSelected(products.getKeys().get(row));
	}
	
	private class RowWidgets {
		public Label priceLabel;
		public MediaWidget imageWidget;
		public Label productNameLabel;
		public Label productVariantLabel;
		public Label productNrLabel;
		public Label productDescriptionLabel;
		
	}
}

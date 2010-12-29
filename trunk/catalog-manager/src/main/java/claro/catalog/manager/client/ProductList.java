package claro.catalog.manager.client;

import static claro.catalog.manager.client.CatalogManager.propertyStringConverter;

import java.util.ArrayList;
import java.util.List;

import claro.catalog.data.MediaValue;
import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.catalog.data.RootProperties;
import claro.catalog.manager.client.widgets.MediaWidget;
import claro.catalog.manager.client.widgets.StatusMessage;
import claro.jpa.catalog.OutputChannel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.gwt.client.widgets.MasterDetail;
import easyenterprise.lib.gwt.client.widgets.Table;
import easyenterprise.lib.util.SMap;

abstract public class ProductList extends MasterDetail implements Globals {
	private static final int IMAGE_COL = 0;
	private static final int PRODUCT_COL = 1;
	private static final int PRICE_COL = 2;

	private static final int NR_COLS = 3;

	
	private boolean initialized;

	private SMap<Long, SMap<PropertyInfo, SMap<String, Object>>> products = SMap.empty();
	private List<RowWidgets> tableWidgets = new ArrayList<ProductList.RowWidgets>();

	private String language;

	private PropertyInfo nameProperty;
	private PropertyInfo variantProperty;
	private PropertyInfo descriptionProperty;
	private PropertyInfo priceProperty;
	private PropertyInfo artNoProperty;
	private PropertyInfo imageProperty;
	private PropertyInfo smallImageProperty;
	private ProductDetails details;


	public ProductList(int headerSize, int footerSize) {
		super(headerSize, footerSize);
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
	
	public void switchLanguage(String newLanguage) {
		language = newLanguage;
		
		setHeader(getMasterTable(), language);
		
		render(); 
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
	
	private void initializeMainPanel() {
		if (initialized) {
			return;
		}
		
		initialized = true;

		getMasterTable().resizeColumns(NR_COLS);
		ProductList.this.setHeader(getMasterTable(), language);
	}
	
	private void setHeader(Table productTable, String language) {
		productTable.setHeaderText(0, 1, messages.product());
		productTable.setHeaderText(0, 2, messages.price());
	}
	
	public void render() {
		initializeMainPanel();
		
		// Make sure we have the root properties:
		if (nameProperty == null) {
			return;
		}
		
		List<Long> productKeys = products.getKeys();

		Table productTable = getMasterTable();
		
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
				StyleUtil.add(this, CatalogManager.Styles.product);
				// .title -> name
				add(rowWidgets.productNameLabel = new InlineLabel() {{
					StyleUtil.add(this, CatalogManager.Styles.productname);
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
				StyleUtil.add(this, CatalogManager.Styles.productprice);
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

		if (details == null) {
			LayoutPanel detailPanel = getDetail();
			detailPanel.clear();
			
			detailPanel.add(new Anchor("Close") {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						closeDetail();
					}
				});
			}});
			String uiLanguage = null; // TODO.
			OutputChannel outputChannel = null; // TODO
			details = new ProductDetails(language, uiLanguage, outputChannel, nameProperty, variantProperty, priceProperty, imageProperty) {
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
			};
			detailPanel.add(details);
		}

		details.setItemData(productId, categories, propertyValues);
		openDetail(row);
	}
	
	abstract protected void productSelected(Long productId);
	
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

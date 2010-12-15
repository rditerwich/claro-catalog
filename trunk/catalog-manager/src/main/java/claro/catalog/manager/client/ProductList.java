package claro.catalog.manager.client;

import java.util.List;

import claro.catalog.data.MediaValue;
import claro.catalog.data.MoneyValue;
import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyInfo;
import claro.catalog.data.RootProperties;
import claro.catalog.manager.client.widgets.MediaWidget;
import claro.catalog.manager.client.widgets.StatusMessage;
import claro.catalog.manager.client.widgets.Table;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.util.SMap;

abstract public class ProductList extends MasterDetail {
	private static final int IMAGE_COL = 0;
	private static final int PRODUCT_COL = 1;
	private static final int PRICE_COL = 2;

	private static final int NR_COLS = 3;

	
	private boolean initialized;

	private SMap<Long, SMap<PropertyInfo, SMap<String, Object>>> products = SMap.empty();

	private String language;

	private PropertyInfo nameProperty;
	private PropertyInfo variantProperty;
	private PropertyInfo descriptionProperty;
	private PropertyInfo priceProperty;
	private PropertyInfo artNoProperty;
	private PropertyInfo imageProperty;
	private PropertyInfo smallImageProperty;


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
		
		setHeader(getMaster(), language);
		
		render(); // TODO More delicate rerender?
	}
	
	public void updateProduct(Long itemId, SMap<PropertyInfo, SMap<String, Object>> newValues) {
		// TODO  Do we need this?
	}
	
	private void initializeMainPanel() {
		if (initialized) {
			return;
		}
		
		initialized = true;

		getMaster().resizeColumns(NR_COLS);
		ProductList.this.setHeader(getMaster(), language);
	}
	
	private void setHeader(Table productTable, String language) {
		productTable.setHeaderText(0, 1, Util.i18n.product());
		productTable.setHeaderText(0, 2, Util.i18n.price());
	}
	
	public void render() {
		initializeMainPanel();
		
		// Make sure we have the root properties:
		if (nameProperty == null) {
			return;
		}
		
		Table productTable = getMaster();
		
		productTable.clear();  // TODO Do more efficiently
		List<Long> productKeys = products.getKeys();
		productTable.resizeRows(1 + productKeys.size());
		int i = 0;
		for (Long productId : productKeys) {
			final int row = i;
			SMap<PropertyInfo, SMap<String, Object>> properties = products.tryGet(productId);
			
			// image
			Object image = properties.getOrEmpty(smallImageProperty).tryGet(language, null);
			if (image == null) {
				image = properties.getOrEmpty(imageProperty).tryGet(language, null);
			}
			if (image instanceof MediaValue) {
				final MediaValue value = (MediaValue)image;
				
				productTable.setWidget(i, IMAGE_COL, new MediaWidget(false) {{
					setData(value.propertyValueId, value.mimeType, value.filename);
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							rowSelected(row);
						}
					});
				}});
			}
			
			// product
			final Object productName = properties.getOrEmpty(nameProperty).tryGet(language, null);
			final Object productVariant = properties.getOrEmpty(variantProperty).tryGet(language, null);
			final Object productNr = properties.getOrEmpty(artNoProperty).tryGet(language, null);
			final Object productDescription = properties.getOrEmpty(descriptionProperty).tryGet(language, null);
			productTable.setWidget(i, PRODUCT_COL, new VerticalPanel() {{
				Styles.add(this, Styles.product);
				// .title -> name
				add(new InlineLabel(productName instanceof String ? productName.toString() : "") {{
					Util.add(this, Styles.productname);
				}});
				// .subtitle -> variant
				add(new InlineLabel(productVariant instanceof String ? productVariant.toString() : "") {{
					Util.add(this, Styles.productvariant);
				}});
				
				// .body -> artnr
				add(new InlineLabel(productNr instanceof String ?  "Art. nr. " + productNr.toString() : ""));
				// .body -> description
				add(new InlineLabel(productDescription instanceof String ? productDescription.toString() : ""));
			}});
			
			// price
			final Object price = properties.getOrEmpty(priceProperty).tryGet(language, null);
			// TODO Use locale in the following format??
			if (price instanceof MoneyValue) {
				MoneyValue priceMoney = (MoneyValue) price;
				productTable.setWidget(i, PRICE_COL, new Label(NumberFormat.getCurrencyFormat(priceMoney.currency).format(priceMoney.value)) {{
					Styles.add(this, Styles.productprice);
				}});
			}
			
			i++;
		}
	}
	
	public void setSelectedProduct(Long productId, SMap<PropertyInfo, PropertyData> propertyValues) {
		int row = products.getKeys().indexOf(productId);

		LayoutPanel detailPanel = getDetail();
		detailPanel.clear();
		
		detailPanel.add(new Anchor("Close") {{
			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					closeDetail();
				}
			});
		}});
		
		ItemDetails details = new ItemDetails();
		details.setItemData(productId, propertyValues);
		detailPanel.add(details);
		
		openDetail(row);
	}
	
	abstract protected void productSelected(Long productId);
	
	private void rowSelected(int row) {
		StatusMessage.get().show(Util.i18n.loadingProductDetails());

		productSelected(products.getKeys().get(row));
	}
}

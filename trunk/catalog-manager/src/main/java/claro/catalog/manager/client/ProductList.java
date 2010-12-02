package claro.catalog.manager.client;

import java.util.List;

import claro.catalog.RootProperties;
import claro.catalog.data.MediaValue;
import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyInfo;
import claro.catalog.manager.client.widgets.MediaWidget;
import claro.catalog.manager.client.widgets.Table;
import claro.jpa.catalog.Item;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.util.SMap;

public class ProductList extends Composite {
	private static final int IMAGE_COL = 0;
	private static final int PRODUCT_COL = 1;
	private static final int PRICE_COL = 2;

	private static final int NR_COLS = 0;

	
	private boolean initialized;
	private FlowPanel mainPanel;
	private Table productTable;

	private SMap<Long, SMap<PropertyInfo, SMap<String, Object>>> products = SMap.empty();

	private String language;

	private PropertyInfo nameProperty;
	private PropertyInfo variantProperty;
	private PropertyInfo descriptionProperty;
	private PropertyInfo priceProperty;
	private PropertyInfo artNoProperty;
	private PropertyInfo imageProperty;
	private PropertyInfo smallImageProperty;


	public ProductList() {
		mainPanel = new FlowPanel();
		
		initWidget(mainPanel);
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
		
		setHeader(productTable, language);
		
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
		
		mainPanel.add(new DockLayoutPanel(Unit.PX) {{
			 add(new ScrollPanel(){{
				 Styles.add(this, Styles.productpanel);
				 setWidget(productTable = new Table(0, NR_COLS) {{
					 ProductList.this.setHeader(this, language);
				 }});
			 }});
		}});
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
		
		productTable.clear();  // TODO Do more efficiently
		List<Long> productKeys = products.getKeys();
		productTable.resizeRows(1 + productKeys.size());
		int i = 1;
		for (Long productId : productKeys) {
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
			productTable.setWidget(i, PRICE_COL, new Label(price instanceof Double? String.format("%d.2", price) : "") {{
				Styles.add(this, Styles.productprice);
			}});
			
			i++;
		}
	}
}

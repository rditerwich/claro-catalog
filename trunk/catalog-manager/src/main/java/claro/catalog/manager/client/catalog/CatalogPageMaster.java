package claro.catalog.manager.client.catalog;

import java.util.Map.Entry;

import claro.catalog.data.MediaValue;
import claro.catalog.data.PropertyInfo;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.MediaWidget;

import com.google.common.base.Strings;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.gwt.client.widgets.MasterDetail.IsHTMLTable;
import easyenterprise.lib.gwt.client.widgets.MoneyFormatUtil;
import easyenterprise.lib.gwt.client.widgets.TableWithObjects;
import easyenterprise.lib.util.Money;
import easyenterprise.lib.util.SMap;

public class CatalogPageMaster extends VerticalPanel implements Globals, IsHTMLTable {

	private static final int IMAGE_COL = 0;
	private static final int PRODUCT_COL = 1;
	private static final int PRICE_COL = 2;
	private static final int NR_COLS = 3;

	private final CatalogPage page;
	private final CatalogPageModel model;

	protected HTML label;
	private TableWithObjects<RowWidgets> table;

	public CatalogPageMaster(CatalogPage page, CatalogPageModel model) {
		this.page = page;
		this.model = model;
		add(label = new HTML(DEBUG_ID_PREFIX));
		add(table = new TableWithObjects<CatalogPageMaster.RowWidgets>() {{
			resizeColumns(NR_COLS);
			getColumnFormatter().getElement(PRODUCT_COL).getStyle().setProperty("textAlign", "left");
			setHeaderText(0, 1, messages.product());
			setHeaderText(0, 2, messages.price());
		}});
	}
	
	@Override
	public HTMLTable asHTMLTable() {
		return table;
	}
	
	public void render() {

		updateLabel();
		
		int fromRows = table.getRowCount();
		table.resizeRows(model.getProducts().size());
		
		// Create new Rows
		for (int row = fromRows; row < model.getProducts().size(); row++) {
			
			
			final RowWidgets rowWidgets = new RowWidgets();
			table.setObject(row, rowWidgets);
			
			// Image
			table.setWidget(row, IMAGE_COL, rowWidgets.imageWidget = new MediaWidget(false, false));
			
			// Product
			table.setWidget(row, PRODUCT_COL, new VerticalPanel() {{
				StyleUtil.addStyle(this, CatalogPage.Styles.product);
				// .title -> name
				add(rowWidgets.productNameLabel = new Anchor() {{
					StyleUtil.addStyle(this, CatalogPage.Styles.productname);
				}});
				// .subtitle -> variant
				add(rowWidgets.productVariantLabel = new InlineLabel() {{
					StyleUtil.addStyle(this, CatalogManager.Styles.productvariant);
				}});
				
				// .body -> artnr
				add(rowWidgets.productNrLabel = new InlineLabel() {{
				}});
				// .body -> description
				add(rowWidgets.productDescriptionLabel = new InlineLabel() {{
				}});
			}});
			table.getWidget(row, PRODUCT_COL).getElement().getParentElement().addClassName(CatalogPage.Styles.productTD.toString());
			// Price
			table.setWidget(row, PRICE_COL, rowWidgets.priceLabel = new Label() {{
				StyleUtil.addStyle(this, CatalogPage.Styles.productprice);
			}});
		}
		

		// (Re) bind widgets:
		
		
		int i = 0;
		for (Entry<Long, SMap<PropertyInfo, SMap<String, Object>>> product : model.getProducts()) {

			render(i, product.getKey(), product.getValue());
			
			i++;
		}
		
//		masterChanged();
	}
	

	private void render(int row, Long productId, SMap<PropertyInfo, SMap<String, Object>> properties) {
		RowWidgets rowWidgets = table.getObject(row);
		
		// image
		Object image = properties.getOrEmpty(model.getSmallImageProperty()).tryGet(model.getSelectedLanguage(), null);
		if (MediaValue.mediaIsNull(image)) {
			image = properties.getOrEmpty(model.getImageProperty()).tryGet(model.getSelectedLanguage(), null);
		}
		if (image instanceof MediaValue) {
			final MediaValue value = (MediaValue)image;
			rowWidgets.imageWidget.setData(value.propertyValueId, value.mimeType, value.filename);
		} else {
			rowWidgets.imageWidget.setData(null, null, null);  // Clear any previous images.
		}
		
		// product
		Object productName = properties.getOrEmpty(model.getNameProperty()).tryGet(model.getSelectedLanguage(), null);
		rowWidgets.productNameLabel.setText(productName instanceof String ? productName.toString() : "<" + messages.noProductNameSet() + ">");
		
		final Object productVariant = properties.getOrEmpty(model.getVariantProperty()).tryGet(model.getSelectedLanguage(), null);
		rowWidgets.productVariantLabel.setText(productVariant instanceof String ? productVariant.toString() : "");
		
		final Object productNr = properties.getOrEmpty(model.getArtNoProperty()).tryGet(model.getSelectedLanguage(), null);
		rowWidgets.productNrLabel.setText(productNr instanceof String ?  "Art. nr. " + productNr.toString() : "");
		
		final Object productDescription = properties.getOrEmpty(model.getDescriptionProperty()).tryGet(model.getSelectedLanguage(), null);
		rowWidgets.productDescriptionLabel.setText(productDescription instanceof String ? productDescription.toString() : "");
		
		// price
		final Object price = properties.getOrEmpty(model.getPriceProperty()).tryGet(model.getSelectedLanguage(), null);
		if (price != null) {
			// TODO Use locale in the following format??
			rowWidgets.priceLabel.setText(MoneyFormatUtil.full((Money) price));
		} else {
			rowWidgets.priceLabel.setText("");
		}

		if (model.isChanged(productId)) {
			StyleUtil.add(table.getRowFormatter(), row, CatalogManager.Styles.itemRowChanged);
		} else {
			StyleUtil.remove(table.getRowFormatter(), row, CatalogManager.Styles.itemRowChanged);
		}
	}

	private void updateLabel() {
		boolean show = false;
		StringBuilder filterText = new StringBuilder();
		if (model.getProducts().size() == 0) {
			filterText.append("No products found");
			show = true;
		} else {
			show = false;
			filterText.append("Results for ");
			String filter = Strings.nullToEmpty(model.getFilterString()).trim();
			String sep = "";
			if (!filter.isEmpty()) {
				filterText.append(filter);
				sep = " and ";
				show = true;
			}
			for (Long categoryId : page.ribbon.filterCategories.getSelection()) {
				filterText.append(sep); 
				sep = ", ";
				filterText.append(page.ribbon.filterCategories.displayName(categoryId));
				show = true;
			}
		}
		label.setHTML(messages.filterMessage(filterText.toString())); 
		label.setVisible(show);
	}
	
	class RowWidgets {
		public Label priceLabel;
		public MediaWidget imageWidget;
		public Anchor productNameLabel;
		public Label productVariantLabel;
		public Label productNrLabel;
		public Label productDescriptionLabel;
		
	}

}

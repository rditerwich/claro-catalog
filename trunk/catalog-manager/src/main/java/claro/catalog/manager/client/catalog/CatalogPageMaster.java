package claro.catalog.manager.client.catalog;

import java.util.Map.Entry;

import claro.catalog.data.MediaValue;
import claro.catalog.data.PropertyInfo;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.widgets.MediaWidget;
import claro.catalog.manager.client.widgets.RoundedPanelAccess;

import com.google.common.base.Strings;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.gwt.client.widgets.EEHoverCorners;
import easyenterprise.lib.gwt.client.widgets.EEPagingButton;
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

	public CatalogPageMaster(CatalogPage page, final CatalogPageModel model) {
		this.page = page;
		this.model = model;
		add(label = new HTML(DEBUG_ID_PREFIX));
		
		add(new EEHoverCorners() {{
			setLeft(new EEPagingButton.PreviousNext(model.getProductData(), new Image(images.prevPage()), new Image(images.nextPage())));
			setRight(new EEPagingButton.PreviousNext(model.getProductData(), new Image(images.prevPage()), new Image(images.nextPage())));
			add(table = new TableWithObjects<CatalogPageMaster.RowWidgets>() {{
				resizeColumns(NR_COLS);
				getColumnFormatter().getElement(PRODUCT_COL).getStyle().setProperty("textAlign", "left");
				setHeaderText(0, 1, messages.product());
				setHeaderText(0, 2, messages.price());
			}});
		}});
	}
	
	@Override
	public HTMLTable asHTMLTable() {
		return table;
	}
	
	public void render() {

		updateLabel();
		
		int fromRows = table.getRowCount();
//		table.resizeRows(model.getProducts().size());

		boolean fits = true;
		int bufferSize = model.getProductData().getBufferSize();
		for (int i = 0; i < bufferSize; i++) {
			int row = i;
			Entry<Long, SMap<PropertyInfo, SMap<String, Object>>> product = model.getProductData().getEntry(i);

			// Only new rows need to be created.
			if (i >= fromRows) {
				table.resizeRows(i + 1);
			
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
		
			render(i, product.getKey(), product.getValue());
			
			if (!rowFits(i)) {
				// Remove from i
				table.resizeRows(i);
				model.getProductData().setSize(i);
				fits = false;
				break;
			}
		}
		
		if (fits) {
			table.resizeRows(bufferSize);
			
			// Everything fits, so try to get more...
			
			model.getProductData().requestMore();
		}
	}
	

	private boolean rowFits(int i) {
		Widget tableParent = getParent().getParent();
		
//		System.out.println("  MasterParentParent bottom is " + tableParent.getElement().getAbsoluteBottom());
//		System.out.println("  MasterParent bottom is " + getParent().getElement().getAbsoluteBottom());
//		System.out.println("  MasterParent lastdiv bottom is " + ((RoundedPanelAccess)getParent()).getAbsoluteBottom());
//		System.out.println("  MasterTable bottom is " + table.getElement().getAbsoluteBottom());
		
		Element tr = table.getRowFormatter().getElement(i);
		int tableBottomSize = ((RoundedPanelAccess)getParent()).getAbsoluteBottom() - table.getElement().getAbsoluteBottom();
//		System.out.println("  row i: " + i  + " absBottom: " + tr.getAbsoluteBottom() + " bottom: " + tableBottomSize);
		if (tr.getAbsoluteBottom() > tableParent.getElement().getAbsoluteBottom() - tableBottomSize) {
			return false;
		}

		return true;
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
		if (model.getProductData().getBufferSize() == 0) {
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

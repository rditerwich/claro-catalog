package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.catalog.Product;
import agilexs.catalogxsadmin.presentation.client.catalog.PropertyValue;
import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;
import agilexs.catalogxsadmin.presentation.client.page.View;
import agilexs.catalogxsadmin.presentation.client.widget.PropertyValueWidget;
import agilexs.catalogxsadmin.presentation.client.widget.Table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProductView extends Composite implements View {

	private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

	public enum SHOW {
		NO_PRODUCTS, PRODUCTS, PRODUCT
	}

	private final DeckPanel deck = new DeckPanel();
	private final Button newProductButton = new Button(i18n.newProduct());
	private final Button saveButton = new Button(i18n.saveChanges());
	private final Anchor back = new Anchor();
	private final FlowPanel propertiesDeck = new FlowPanel();
	private final FlowPanel propertyValuesPanel = new FlowPanel();
	private final HTML pvHeader = new HTML(i18n.h3(i18n.propertyValues()));
	final ListBox productGroup = new ListBox();

	// private final FlowPanel labels = new FlowPanel();
	private final Table productTable = new Table();
	private HTML productsOverviewLabel;
	private HTML productDetailTitle;
	private Image productImage;
	private Label priceLabel;

	// private Label lastSelected;

	public ProductView(boolean inlineNewProductButton) {
		initWidget(deck);

		deck.add(new HTML(i18n.noProducts()));
		// Overview table
		DockLayoutPanel scrollableOverviewPanel = new DockLayoutPanel(Unit.PX);

			final FlowPanel overviewPanel = new FlowPanel();
	
				newProductButton.addStyleName(Styles.button4.toString());
				if (inlineNewProductButton) {
					final HorizontalPanel tophp = new HorizontalPanel();
					tophp.add(newProductButton);
					overviewPanel.add(tophp);
				}
		
				productsOverviewLabel = new HTML();
				scrollableOverviewPanel.addNorth(productsOverviewLabel, 40);
			overviewPanel.add(productTable);
			final ScrollPanel spo = new ScrollPanel(overviewPanel);
		scrollableOverviewPanel.add(spo);
		Util.add(spo, Styles.productpanel);
		deck.add(scrollableOverviewPanel);

		// Detail page
		final FlowPanel detailPanel = new FlowPanel();
		Util.add(detailPanel, Styles.itempanel);

			final VerticalPanel top = new VerticalPanel();
				back.getElement().getStyle().setCursor(Cursor.POINTER);
				back.setHTML(i18n.backToProductOverview());
			top.add(back);
				final HorizontalPanel titleAndSave = new HorizontalPanel();
					productDetailTitle = new HTML();
					saveButton.addStyleName(Styles.button4.toString());
				titleAndSave.add(productDetailTitle);
				titleAndSave.add(saveButton);
				titleAndSave.setCellVerticalAlignment(saveButton, HorizontalPanel.ALIGN_MIDDLE);
				// saveButton.getElement().getStyle().setMarginLeft(200, Unit.PX);
				
				HorizontalPanel imageAndPrice = new HorizontalPanel();
					productImage = new Image();
					productImage.setSize("150px", "150px");
					priceLabel = new Label();
					Util.add(priceLabel, Styles.productprice);
				imageAndPrice.add(productImage);
				imageAndPrice.add(priceLabel);
				imageAndPrice.setCellVerticalAlignment(priceLabel, HorizontalPanel.ALIGN_MIDDLE);
			top.add(titleAndSave);
			top.add(imageAndPrice);
		detailPanel.add(top);
			final FlowPanel productPanel = new FlowPanel();
		detailPanel.add(productPanel);
		DockLayoutPanel scrollableDetailPanel = new DockLayoutPanel(Unit.PX);
		final ScrollPanel detailScrollPanel = new ScrollPanel(detailPanel);
		scrollableDetailPanel.add(detailScrollPanel);

		deck.add(scrollableDetailPanel);
		final FlowPanel fpp = new FlowPanel();
		final FlowPanel fp = new FlowPanel();

		fp.add(new InlineLabel(i18n.explainOwnerCategory()));
		fp.add(productGroup);
		productGroup.getElement().getStyle().setMarginLeft(10, Unit.PX);
		add(fpp, i18n.category(), fp);
		productPanel.add(fpp);
		productPanel.add(propertiesDeck);
	}

	public void clear() {
		propertiesDeck.clear();
		productGroup.clear();
		propertyValuesPanel.clear();
		propertyValuesPanel.removeFromParent();
	}

	public void addPropertyValues(String name, Widget widget) {
		if (!propertyValuesPanel.isAttached()) {
			propertiesDeck.add(propertyValuesPanel);
			propertyValuesPanel.add(pvHeader);
		}
		add(propertyValuesPanel, name, widget);
	}

	private void add(FlowPanel parent, String name, Widget widget) {
		final HTML lbl = new HTML(i18n.h3(name));

		parent.getElement().getStyle().setPadding(8, Unit.PX);
		parent.getElement().getStyle().setMarginBottom(8, Unit.PX);
		Util.add(parent, Styles.properties);
		parent.add(lbl);
		parent.add(widget);
	}

	public HasClickHandlers backClickHandlers() {
		return back;
	}

	public Button getNewProductButton() {
		return newProductButton;
	}

	public HasClickHandlers newProductButtonClickHandlers() {
		return newProductButton;
	}

	public HasClickHandlers saveButtonClickHandlers() {
		return saveButton;
	}

	public ListBox getGroupListBox() {
		return productGroup;
	}

	/**
	 * Diplays button for status when saving in progress.
	 */
	public void setSaving(boolean saving) {
		if (saving) {
			saveButton.setText(i18n.saving());
			saveButton.setEnabled(false);
		} else {
			saveButton.setText(i18n.saveChanges());
			saveButton.setEnabled(true);
		}
	}

	public void setCategoryName(String name) {
		productsOverviewLabel.setHTML(i18n.h2(i18n.productsIn(name)));
	}

	public void setProductName(String name) {
		// pname.setHTML(i18n.h2(name));
		productDetailTitle.setHTML(i18n.h2(i18n.productDetailsOf(name)));
	}
	
	public void setImage(String url, String filename) {
		productImage.setUrl(url);
		productImage.setTitle(filename);
	}
	
	public void setPrice(String price) {
		priceLabel.setText(price);
	}

	public Table getProductTable() {
		return productTable;
	}

	public void setProductTableHeader(int column, String text) {
		if (column >= productTable.getColumnCount()) {
			productTable.resizeColumns(column + 1);
		}
		productTable.setHeaderHTML(0, column, text);
	}

	public void setProductTableCellImage(int row, int column, ImageResource image) {
		productTable.getCellFormatter().getElement(row, column).getStyle().setCursor(Cursor.POINTER);
		if (column >= productTable.getColumnCount()) {
			productTable.resizeColumns(column + 1);
		}
		productTable.setWidget(row, column, new Image(image));
	}

	public void setProductTableCell(int row, int column, PropertyValue pv) {
		if (column >= productTable.getColumnCount()) {
			productTable.resizeColumns(column + 1);
		}
		if (pv == null) {
			productTable.setHTML(row, column, "&nbsp;");
		} else {
			productTable.setWidget(row, column, new PropertyValueWidget(pv));
		}
	}

	public void setProductTablePrice(int row, int column, Product product) {
		if (column >= productTable.getColumnCount()) {
			productTable.resizeColumns(column + 1);
		}
		Label label = new Label(Util.price(product));
		Util.add(label, Styles.productprice);
		productTable.setWidget(row, column, label);
	}
	
	public void setProductTableProduct(int row, int column, Product product, String currentLanguage) {
		if (column >= productTable.getColumnCount()) {
			productTable.resizeColumns(column + 1);
		}
		
		VerticalPanel productPanel = new VerticalPanel();
		Util.add(productPanel, Styles.product);
		Label productLabel = new Label(Util.name(product, currentLanguage));
		Util.add(productLabel, Styles.productname);
		productPanel.add(productLabel);
		productPanel.add(new Label("Art. nr. " + Util.articleNumber(product, currentLanguage)));
		productPanel.add(new InlineLabel(Util.description(product, currentLanguage)));
		
		productTable.setWidget(row, column, productPanel);
	}
	

	@Override
	public Widget asWidget() {
		return this;
	}

	public void showPage(SHOW show) {
		setSaving(false); // reset save button
		int i = 0;
		switch (show) {
		case NO_PRODUCTS:
			i = 0;
			productTable.setVisible(false);
			break;
		case PRODUCTS:
			i = 1;
			productTable.setVisible(true);
			break;
		case PRODUCT:
			i = 2;
			break;
		}
		deck.showWidget(i);
	}
}

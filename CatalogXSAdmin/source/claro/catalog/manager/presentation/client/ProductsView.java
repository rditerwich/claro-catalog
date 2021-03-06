package claro.catalog.manager.presentation.client;


import claro.catalog.manager.presentation.client.i18n.I18NCatalogXS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

public class ProductsView extends CatalogView {

	private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);
	private LayoutPanel products = new LayoutPanel();
	private TextBox filter = new TextBox();

	public ProductsView() {
		super();
	}

	public void init(ProductView pv) {
//		topPanel.getElement().getStyle().setMarginTop(10, Unit.PX);
		topPanel.add(new InlineLabel(i18n.filter()));
		topPanel.add(filter);
		topPanel.add(pv.getNewProductButton());
		topPanel.add(languageList);
		topPanel.add(publishButton);
		detailPanel.add(products);
		products.add(pv.asWidget());
		pv.asWidget().getElement().getStyle().setPadding(10, Unit.PX);
	}

	public HasKeyUpHandlers productFilterHandlers() {
		return filter;
	}

	public String getFilterText() {
		return filter.getText();
	}
}

package claro.catalog.manager.presentation.client;


import claro.catalog.manager.presentation.client.i18n.I18NCatalogXS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;

public class TaxonomiesView extends CatalogView {

	private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

//	private final TabLayoutPanel tp = new TabLayoutPanel(40, Unit.PX);
	private final Button newCategoryButton = new Button(i18n.newCategory());

	public TaxonomiesView() {
		super();
		Util.add(newCategoryButton, Styles.button5);
		topPanel.add(newCategoryButton);
		topPanel.add(languageList);
		topPanel.add(publishButton);
//		detailPanel.add(tp);
	}

	public HasClickHandlers getNewCategoryButtonClickHandler() {
		return newCategoryButton;
	}

//	public void addTab(View view, String text) {
//		tp.add(view.asWidget(), text);
//	}
//
//	public void addTabSelectionHandler(SelectionHandler<Integer> selectionHandler) {
//		tp.addSelectionHandler(selectionHandler);
//	}
//
//	public int getSelectedTab() {
//		return tp.getSelectedIndex();
//	}
//
//	public void selectedTab(int i) {
//		tp.selectTab(i);
//	}
//
//	public void setTabVisible(int index, boolean visible) {
//		tp.getTabWidget(index).getParent().setVisible(visible);
//	}
}

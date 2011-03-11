package claro.catalog.manager.client.test;

import claro.catalog.manager.client.Page;

import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.gwt.client.widgets.EEButton;
import easyenterprise.lib.gwt.client.widgets.SExprEditor;

public class TearupTabsTestPage extends Page {

	private VerticalPanel mainPanel;
	protected SExprEditor expr2;

	public TearupTabsTestPage(PlaceController placeController) {
		super(placeController);
		initWidget(new HorizontalPanel() {{
			add(new EEButton("Import Now"));
			add(new EEButton("Import File"));
		}});
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

}

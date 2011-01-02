package claro.catalog.manager.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.gwt.client.widgets.TearUpTabs;

public class TearupTabsTestPage extends Page {

	private TearUpTabs mainPanel;

	public TearupTabsTestPage(PlaceController placeController) {
		super(placeController);
		mainPanel = new TearUpTabs(40, 5);
		mainPanel.addTab(new Label("Details"), 60, new VerticalPanel() {{
			add(new Label("Some content"));
		}});
		mainPanel.addTab(new Label("Log"), 40, new VerticalPanel() {{
			add(new Label("Some log"));
		}});
		initWidget(mainPanel);
	}

	@Override
	public Place getPlace() {
		return null;
	}

	@Override
	public void show() {
		mainPanel.onResize();
	}
}

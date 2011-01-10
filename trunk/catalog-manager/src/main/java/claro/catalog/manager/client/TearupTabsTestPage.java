package claro.catalog.manager.client;

import claro.catalog.manager.client.widgets.Help;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.gwt.client.widgets.SExprEditor;

public class TearupTabsTestPage extends Page {

	private VerticalPanel mainPanel;
	protected SExprEditor expr2;

	public TearupTabsTestPage(PlaceController placeController) {
		super(placeController);
		mainPanel = new VerticalPanel() {{
			add(new SExprEditor(){{
				addValueChangeHandler(new ValueChangeHandler<String>() {
					
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						// TODO Auto-generated method stub
					System.out.println("ChNAGED");	
					}
				});
			}});
			add(expr2 = new SExprEditor());
			add(new Help("HI THERE \"dfdfdf\""));
		}};
		initWidget(mainPanel);
	}

	@Override
	public Place getPlace() {
		return null;
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

}

package claro.catalog.manager.client;

import claro.catalog.manager.client.widgets.FormTable;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.gwt.client.widgets.SExprEditor;

public class TearupTabsTestPage extends Page {

	private VerticalPanel mainPanel;
	protected SExprEditor expr2;

	public TearupTabsTestPage(PlaceController placeController) {
		super(placeController);
		initWidget(new FormTable() {{
			add("Name", new TextBox(), "Enter the name of the object");
			add("Newsletter", new CheckBox(), "Will user receive a checkbox?");
			add("Newsletter", new CheckBox(), "Will user receive a checkbox?");
			add("Newsletter", new CheckBox(), "Will user receive a checkbox?");
			add("Newsletter", new TextBox(), "Name of the newletter that the user will get \n alskdfj laskdfj lawiejf a;lwefkj ;alweifj;alweifjlij sl;fdkjasdlfkja lwekjfawef\n akdfj akjfha lwkeufhalkuh ");
		}});
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

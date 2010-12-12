package claro.catalog.manager.client;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.LayoutPanel;

import easyenterprise.lib.gwt.ui.SExprEditor;
import easyenterprise.lib.sexpr.BuiltinFunctions;
import easyenterprise.lib.sexpr.DefaultContext;

public class ImportPage extends Page {

	private final LayoutPanel mainPanel;
	private boolean initialized;
	private DefaultContext context = new DefaultContext();

	public ImportPage(PlaceController placeController) {
		super(placeController);
		context.addFunctions(BuiltinFunctions.functions);
		mainPanel = new LayoutPanel();
		initWidget(mainPanel);
	}

	@Override
	public Place getPlace() {
		return null;
	}

	@Override
	public void show() {
		initializeMainPanel();
	}

	private void initializeMainPanel() {
		if (initialized) return;
		initialized = true;
		
		mainPanel.add(new SExprEditor());
	}

		
	
}


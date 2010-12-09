package claro.catalog.manager.client;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.LayoutPanel;

import easyenterprise.lib.gwt.ui.RichTextEditor;
import easyenterprise.lib.gwt.ui.SExprEditor;
import easyenterprise.lib.gwt.ui.SExprExprEditor;
import easyenterprise.lib.gwt.ui.SExprExprEditor.Item;
import easyenterprise.lib.sexpr.BuiltinFunctions;
import easyenterprise.lib.sexpr.DefaultContext;
import easyenterprise.lib.util.SortedList;

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
		
		final RichTextEditor rich = new RichTextEditor();
//		mainPanel.add(rich);
		rich.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				System.out.println(rich.getSelection());
				
			}
		});
//		SortedList<Item> items = SExprExprEditor.getItems(context);
//		mainPanel.add(new SExprExprEditor(items) {{
//			
//		}});
		mainPanel.add(new SExprEditor());
	}

		
	
}


package claro.catalog.manager.client;

import org.cobogw.gwt.user.client.ui.RoundedPanel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.DockLayoutPanel;

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.gwt.client.widgets.Header;

public class EmptyPage extends Page {
	enum Styles implements Style { emptyPage }

	public EmptyPage(PlaceController placeController, final String title) {
		super(placeController);
		initWidget(new RoundedPanel( RoundedPanel.ALL, 4) {{
			StyleUtil.addStyle(this, Styles.emptyPage);
			setBorderColor("white");
			setWidget(new Header(1, title));
		}});
	}

	@Override
	public void show() {
	}

}

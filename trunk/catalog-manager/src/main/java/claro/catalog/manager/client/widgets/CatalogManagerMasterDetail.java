package claro.catalog.manager.client.widgets;

import org.cobogw.gwt.user.client.ui.RoundedPanel;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;

import easyenterprise.lib.gwt.client.widgets.MasterDetail;

public class CatalogManagerMasterDetail extends MasterDetail {

	public CatalogManagerMasterDetail(int headerSize) {
		super(headerSize, 0, 24);
	}

	@Override
	protected HasWidgets createHeaderWrapper(LayoutPanel parent) {
		Panel result = new RoundedPanel(RoundedPanel.ALL, 4) {{
			setBorderColor("white");
		}};
		parent.add(result);
		return result;
	}
	
	@Override
	protected HasWidgets createMasterWrapper(LayoutPanel parent) {
		Panel result = new RoundedPanel(RoundedPanel.ALL, 4) {{
			setBorderColor("white");
		}};
		parent.add(result);
		return result;
	}
}

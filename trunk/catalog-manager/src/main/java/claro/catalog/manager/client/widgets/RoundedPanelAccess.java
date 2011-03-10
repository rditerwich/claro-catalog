package claro.catalog.manager.client.widgets;

import org.cobogw.gwt.user.client.ui.RoundedPanel;

import com.google.gwt.dom.client.DivElement;

public class RoundedPanelAccess extends RoundedPanel {
	public RoundedPanelAccess(int all, int i) {
		super(all, i);
	}

	public int getAbsoluteBottom() {
		int result = getContainerElement().getAbsoluteBottom();
		for (DivElement div : divb) {
			if (div.getAbsoluteBottom() > result) {
				result = div.getAbsoluteBottom();
			}
		}
		
		return result;
	}
}

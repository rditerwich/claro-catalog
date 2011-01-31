package claro.catalog.manager.client.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;

public class LanguageAndChannelSelector extends Composite {
	private ListBox listBox;

	public LanguageAndChannelSelector() {
		initWidget(listBox = new ListBox() {{
		}});
		
		refreshData();
	}

	public void refreshData() {
		
	}
}

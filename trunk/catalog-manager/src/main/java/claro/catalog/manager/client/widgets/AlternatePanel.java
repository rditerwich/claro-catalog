package claro.catalog.manager.client.widgets;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class AlternatePanel extends FlowPanel {

	private Widget currentWidget;
	
	public AlternatePanel(Widget... children) {
		for (Widget child : children) {
			child.setVisible(false);
			add(child);
		}
		show(0);
	}
	
	public void show(int index) {
		show(getChildren().get(index));
	}
	
	public void show(Widget widget) {
		if (currentWidget != widget) {
			if (currentWidget != null) {
				currentWidget.setVisible(false);
			}
			if (widget != null) {
				widget.setVisible(true);
			}
			currentWidget = widget;
		}
	}
	
	public void showSecond(boolean second) {
		show(second ? 1 : 0);
	}
	
	public void hide() {
		show(null);
	}
}

package claro.catalog.manager.client.importing;

import claro.catalog.manager.client.Globals;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ImportLogPanel extends VerticalPanel implements Globals {

	private Label label;
	private Label logText;
	private final ImportSoureModel model;

	public ImportLogPanel(ImportSoureModel model) {
		this.model = model;
		setStylePrimaryName("ImportLogPanel");
		add(label = new Label());
		add(logText = new Label());
		logText.setStylePrimaryName("LogText");
	}

	void render() {
		if (model.getJobResult() != null) {
			label.setText(model.getJobResult().getUrl() + " at " + model.getJobResult().getEndTime().toString());
			logText.setText(model.getJobResult().getLog());
			logText.setVisible(true);
		} else {
			label.setText(messages.pleaseWait());
			logText.setVisible(false);
		}
	}
}

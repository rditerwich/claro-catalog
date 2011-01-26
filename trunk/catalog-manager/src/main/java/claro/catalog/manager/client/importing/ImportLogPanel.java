package claro.catalog.manager.client.importing;

import claro.catalog.manager.client.Globals;
import claro.jpa.importing.ImportJobResult;
import claro.jpa.importing.ImportSource;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ImportLogPanel extends VerticalPanel implements Globals {

	private ImportJobResult jobResult;
	private ImportSource importSource;
	private Label label;
	private Label logText;

	public ImportLogPanel() {
		setStylePrimaryName("ImportLogPanel");
		add(label = new Label());
		add(logText = new Label());
		logText.setStylePrimaryName("LogText");
	}
	
	public void setImportSource(ImportSource importSource) {
		if (this.importSource != importSource) {
			this.importSource = importSource;
			reset();
		}
	}
	
	private void reset() {
		label.setText(messages.pleaseWait());
		logText.setVisible(false);
	}

	public void setJobResult(ImportJobResult jobResult) {
		if (this.jobResult != jobResult) {
			this.jobResult = jobResult;
			render();
		}
	}

	private void render() {
		label.setText(jobResult.getUrl() + " at " + jobResult.getEndTime().toString());
		logText.setText(jobResult.getLog());
		logText.setVisible(true);
	}
}

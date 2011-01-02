package claro.catalog.manager.client.importing;

import claro.catalog.manager.client.Globals;
import claro.jpa.importing.ImportSource;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.gwt.client.widgets.SExprEditor;
import easyenterprise.lib.util.ObjectUtil;

public abstract class ImportSourceMainPanel extends Composite implements Globals {

	private VerticalPanel panel;
	private Label idLabel;
	private TextBox nameTextBox;
	private ImportSource importSource;
	private Label lastStatusLabel;
	private SExprEditor importUrlEditor;
	
	public ImportSourceMainPanel() {
		initWidget(panel = new VerticalPanel() {{
			add(idLabel = new Label());
			add(new Grid(3, 2) {{
				setWidget(0, 0, new Label(messages.importSource()));
				setWidget(0, 1, nameTextBox = new TextBox());
				setWidget(1, 0, new Label(messages.lastStatus()));
				setWidget(1, 1, lastStatusLabel = new Label());
				setWidget(1, 0, new Label(messages.importUrl()));
				setWidget(1, 1, importUrlEditor = new SExprEditor());
			}});
		}});
		
		ValueChangeHandler<String> changeHandler = new ValueChangeHandler<String>() {
			public void onValueChange(ValueChangeEvent<String> event) {
				importSource.setName(nameTextBox.getText());
				importSource.setImportUrlExpression(importUrlEditor.getExpression());
				importSourceChanged();
			}
		};
		nameTextBox.addValueChangeHandler(changeHandler);
		importUrlEditor.addValueChangeHandler(changeHandler);
	}
	
	public void setImportSource(ImportSource importSource) {
		this.importSource = importSource;
		idLabel.setText(ObjectUtil.orElse(importSource.getId(), -1).toString());
		nameTextBox.setText(importSource.getName());
		Boolean lastSuccess = importSource.getJob().getLastSuccess();
		lastStatusLabel.setText(lastSuccess == null ? messages.notRun() : (lastSuccess ? messages.success() : messages.failed()));
		importUrlEditor.setExpression(ObjectUtil.orElse(importSource.getImportUrlExpression(), ""));
	}
	
	protected abstract void importSourceChanged();
	
}

package claro.catalog.manager.client.importing;

import static claro.catalog.model.CatalogModelUtil.propertyLabel;
import claro.catalog.manager.client.Globals;
import claro.catalog.model.CatalogModelUtil;
import claro.jpa.importing.ImportProperty;
import claro.jpa.importing.ImportSource;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.gwt.client.widgets.SExprEditor;

public abstract class ImportSourcePropertyMappingsPanel extends Composite implements Globals {

	private VerticalPanel panel;
	private Grid propertyGrid;
	private ImportSource importSource;
	
	public ImportSourcePropertyMappingsPanel() {
		initWidget(panel = new VerticalPanel() {{
			add(propertyGrid = new Grid(0, 2));
		}});
		
	}
	
	public void setImportSource(ImportSource importSource) {
		this.importSource = importSource;
		for (int i = propertyGrid.getRowCount(); i < importSource.getProperties().size(); i++) {
			propertyGrid.resizeRows(i + 1);
			propertyGrid.setWidget(i, 0, new TextBox() {{
				addChangeHandler(new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						
					}
				});
			}});
			propertyGrid.setWidget(i, 1, new SExprEditor());
		}
		int row = 0;
		for (ImportProperty property : importSource.getProperties()) {
			((TextBox) propertyGrid.getWidget(row, 0)).setText(propertyLabel(property.getProperty(), null, "" + property.getProperty().getId()));
			((SExprEditor) propertyGrid.getWidget(row, 1)).setExpression(property.getValueExpression());
			row++;
		}
	}
	
	protected abstract void importSourceChanged();
	
}

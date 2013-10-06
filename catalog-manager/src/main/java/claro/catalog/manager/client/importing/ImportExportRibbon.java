package claro.catalog.manager.client.importing;

import claro.catalog.manager.client.Globals;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;

import easyenterprise.lib.gwt.client.widgets.EERibbon;
import easyenterprise.lib.gwt.client.widgets.EERibbonPanel;
import easyenterprise.lib.gwt.client.widgets.Header;

public class ImportExportRibbon extends FlowPanel implements Globals {

	private final ImportSoureModel model;

	public ImportExportRibbon(final ImportSoureModel model) {
		this.model = model;
		add(new EERibbon() {{
  		add(new EERibbonPanel() {{
  		  add(new Header(1, "Actions"));
  		  add(new Button(messages.newImportSourceLink()) {{
  		    addClickHandler(new ClickHandler() {
  		      public void onClick(ClickEvent event) {
  		        model.createImportSource();
  		        event.stopPropagation();
  		      }
  		    });
  		  }});
  		}});
		}});
	}
}

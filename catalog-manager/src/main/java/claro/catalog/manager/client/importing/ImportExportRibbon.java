package claro.catalog.manager.client.importing;

import claro.catalog.manager.client.Globals;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;

public class ImportExportRibbon extends FlowPanel implements Globals {

	private final ImportSoureModel model;

	public ImportExportRibbon(final ImportSoureModel model) {
		this.model = model;
		add(new Anchor(messages.newImportSourceLink()) {{
			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					model.createImportSource();
					event.stopPropagation();
				}
			});
		}});
	}
}

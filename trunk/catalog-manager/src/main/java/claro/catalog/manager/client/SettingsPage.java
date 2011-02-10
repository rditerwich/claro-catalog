package claro.catalog.manager.client;

import org.cobogw.gwt.user.client.ui.RoundedPanel;

import claro.catalog.command.FlushCatalogModel;
import claro.catalog.manager.client.command.StatusCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.gwt.client.widgets.Header;

public class SettingsPage extends Page {
	enum Styles implements Style { emptyPage }

	public SettingsPage(PlaceController placeController, final String title) {
		super(placeController);
		initWidget(new RoundedPanel( RoundedPanel.ALL, 4) {{
			StyleUtil.addStyle(this, Styles.emptyPage);
			setBorderColor("white");
			setWidget(new VerticalPanel() {{
				add(new Header(1, title));
				add(new Anchor("flush model cache") {{
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							GwtCommandFacade.execute(new FlushCatalogModel(CatalogManager.getCurrentCatalogId()), new StatusCallback<FlushCatalogModel.Result>("Flushing Cache", false) {
							});
						}
					});
				}});
			}});
		}});
	}

	@Override
	public void show() {
	}

}

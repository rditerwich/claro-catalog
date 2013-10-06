package claro.catalog.manager.client.webshop;

import static easyenterprise.lib.util.ObjectUtil.orElse;
import claro.catalog.manager.client.Globals;
import claro.jpa.shop.Shop;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;

import easyenterprise.lib.gwt.client.widgets.TableWithObjects;

public class WebshopMaster extends TableWithObjects<Shop> implements Globals {

	private static final int NAME_COL = 0;
	private static final int URL_COL = 1;
	private static final int NR_COLS = 2;

	private final OutputChannelModel model;

	public WebshopMaster(OutputChannelModel model) {
		this.model = model;
		resizeColumns(NR_COLS);
		setHeaderText(0, NAME_COL, messages.shopNameLabel());
		setHeaderText(0, URL_COL, messages.shopUrlLabel());
	}
	
	public void render() {
		int fromRows = getRowCount();
		resizeRows(model.getShops().size());
		
		// Create new Rows
		for (int row = fromRows; row < model.getShops().size(); row++) {
			setObject(row, model.getShops().get(row));
			
			final ClickHandler selectRowClickHandler = new ClickHandler() {
				public void onClick(ClickEvent event) {
				}
			};
			
			setWidget(row, NAME_COL, new Anchor() {{
				addClickHandler(selectRowClickHandler);
			}});
			setWidget(row, URL_COL, new Label() {{
				addDomHandler(selectRowClickHandler, ClickEvent.getType());
			}});
		}
		
		// render all rows
		for (int row = 0; row < model.getShops().size(); row++) {
			renderRow(row);
		}
		
	}
	
	public void renderRow(int row) {
		Shop shop = model.getShops().get(row);
		setObject(row, shop);
		((HasText) getWidget(row, NAME_COL)).setText(shop.getName());
		((HasText) getWidget(row, URL_COL)).setText(orElse(shop.getUrlPrefix(), ""));
	}
}


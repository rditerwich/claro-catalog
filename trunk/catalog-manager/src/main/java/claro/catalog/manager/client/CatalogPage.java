package claro.catalog.manager.client;

import claro.catalog.command.ItemDetailsCommand;
import claro.catalog.command.ItemDetailsCommandResult;
import claro.jpa.catalog.Item;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;

public class CatalogPage extends Page {

	private FlowPanel mainPanel;
	private ItemDetails details;

	private Item selectedItem;
	
	public CatalogPage(PlaceController placeController) {
		super(placeController);
		
		mainPanel = new FlowPanel();
		initWidget(mainPanel);
	}

	@Override
	public Place getPlace() {
		// TODO implement
		return null;
	}

	public void show() {
		// Init widget tree
		details = new ItemDetails();
		mainPanel.add(details);

		// Lots more need here :)
		selectedItem = new Item();
		selectedItem.setId(0L);
		
		// retrieve data
		ItemDetailsCommand cmd = new ItemDetailsCommand();
		cmd.setCatalogId(-1L);
		cmd.setItem(selectedItem.getId());
		GwtCommandFacade.execute(cmd, new AsyncCallback<ItemDetailsCommandResult>() {
			public void onSuccess(ItemDetailsCommandResult result) {
				details.setItemData(selectedItem, result.propertyData);
				System.out.println("Gelukt");
			}
			public void onFailure(Throwable caught) {
				System.out.println("Niet gelukt: " + caught);
				caught.printStackTrace();
			}			
		});

		// TODO See whether it was cached 

	}
	
}

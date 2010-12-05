package claro.catalog.manager.client;

import claro.catalog.command.ItemDetailsCommand;
import claro.catalog.command.ItemDetailsCommandResult;
import claro.catalog.command.RootPropertiesCommand;
import claro.catalog.command.RootPropertiesCommandResult;
import claro.jpa.catalog.Item;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

import easyenterprise.lib.command.gwt.GwtCommandFacade;

public class CatalogPage extends Page {

	private FlowPanel mainPanel;
	ProductList filteredProductList;
	private ItemDetails details;

	private Item selectedItem;
	private boolean initialized;
	protected HTML filterLabel;
	
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
		initializeMainPanel();
		
		// Retrieve Products for filter:
		// TODO

		// Lots more need here :)
		selectedItem = new Item();
		selectedItem.setId(2L);
		
		// retrieve data
		ItemDetailsCommand cmd = new ItemDetailsCommand();
		cmd.setCatalogId(-1L)
		   .setItem(selectedItem.getId());
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
	
	private void initializeMainPanel() {
		if (initialized) {
			return;
		}

		initialized = true;
		
		// search panel
		mainPanel.add(new FlowPanel() {{
			add(new Grid(1, 5){{
				Styles.add(this, Styles.filterpanel);
				setWidget(0, 0, new TextBox() {{
					setText("articlenumber: 234444");
				}});
				setWidget(0, 1, new Label("Filter1")); // TODO i18n
				setWidget(0, 2, new ListBox() {{
					addItem("Option1");
					addItem("Option2");
					addItem("Option3");
				}});
				setWidget(0, 3, new Label("Filter2")); // TODO i18n
				setWidget(0, 4, new ListBox() {{
					addItem("Option4");
					addItem("Option5");
					addItem("Option6");
				}});
			}});
			add(new FlowPanel() {{
				Styles.add(this, Styles.catalogresultspanel);
				add(filterLabel = new HTML("Search results for <b>articlenumber: 234444</b>") {{
					setVisible(true); // TODO should be false..
				}});  // TODO i18n
				add(filteredProductList = new ProductList());
			}});
		}});
		
		// Read Root properties
		RootPropertiesCommand cmd = new RootPropertiesCommand();
		cmd.setCatalogId(-1L);
		GwtCommandFacade.execute(cmd, new AsyncCallback<RootPropertiesCommandResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Paniek!!
			}

			@Override
			public void onSuccess(RootPropertiesCommandResult result) {
				filteredProductList.setRootProperties(result.rootProperties);
			}
		});		
		
		// TODO Listen to language selection??
		
		details = new ItemDetails();
		mainPanel.add(details);
	}
}





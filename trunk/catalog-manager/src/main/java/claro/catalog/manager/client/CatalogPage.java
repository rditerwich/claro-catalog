package claro.catalog.manager.client;

import claro.catalog.command.ItemDetailsCommand;
import claro.catalog.command.ItemDetailsCommandResult;
import claro.catalog.command.ProductListCommand;
import claro.catalog.command.ProductListCommandResult;
import claro.catalog.command.RootPropertiesCommand;
import claro.catalog.command.RootPropertiesCommandResult;
import claro.catalog.manager.client.command.StatusCallback;
import claro.jpa.catalog.Item;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

import easyenterprise.lib.command.gwt.GwtCommandFacade;

public class CatalogPage extends Page {

	private LayoutPanel mainPanel;
	ProductList filteredProductList;
	private Label noProductsFoundLabel;

	private Item selectedItem;
	private boolean initialized;
	protected HTML filterLabel;
	
	private long currentCatalogId;
	private long currentOuputChannel;
	private String currentLanguage;
	private String filterString;

	
	
	public CatalogPage(PlaceController placeController) {
		super(placeController);
		
		currentCatalogId = -1L; // TODO Make dynamic.
		initWidget(mainPanel = new LayoutPanel());
	}

	@Override
	public Place getPlace() {
		// TODO implement
		return null;
	}

	public void show() {
		initializeMainPanel();
		
		// Retrieve Products for filter:
		updateProductList();

	}
	
	private void initializeMainPanel() {
		if (initialized) {
			return;
		}

		initialized = true;
		
		mainPanel.add(filteredProductList = new ProductList(50, 0) {{
			// search panel
			getMasterHeader().add(new Grid(2, 6){{
				Styles.add(this, Styles.filterpanel);
				setWidget(0, 0, new ListBox() {{
					addItem("Default");
					addItem("English");
					addItem("French");
					addItem("  Shop");
					addItem("    English");
					addItem("    French");
				}});
				setWidget(0, 1, new TextBox() {{
					addChangeHandler(new ChangeHandler() {
						public void onChange(ChangeEvent event) {
							filterString = getText();
							updateFilterLabel();
							updateProductList();
						}
					});
				}});
				setWidget(0, 2, new Label("Filter1")); // TODO i18n
				setWidget(0, 3, new ListBox() {{
					addItem("Option1");
					addItem("Option2");
					addItem("Option3");
				}});
				setWidget(0, 4, new Label("Filter2")); // TODO i18n
				setWidget(0, 5, new ListBox() {{
					addItem("Option4");
					addItem("Option5");
					addItem("Option6");
				}});
			}});
			getMasterHeader().add(filterLabel = new HTML() {{
				setVisible(false); 
			}}); 
			getMasterHeader().add(noProductsFoundLabel = new Label(Util.i18n.noProductsFound()) {{
				setVisible(false);
			}});
		}
		
			protected void productSelected(final Long productId) {
				// retrieve data
				updateProductSelection(productId);
			}
		});
		
		// Read Root properties
		updateProductListRootProperties();		
		
		// TODO Listen to language selection??
	}

	private void updateProductListRootProperties() {
		RootPropertiesCommand cmd = new RootPropertiesCommand();
		cmd.setCatalogId(-1L);
		GwtCommandFacade.executeWithRetry(cmd, 3, new StatusCallback<RootPropertiesCommandResult>() {
			public void onSuccess(RootPropertiesCommandResult result) {
				filteredProductList.setRootProperties(result.rootProperties);
			}
		});
	}
	
	private void updateProductList() {
		ProductListCommand cmd = new ProductListCommand();
		cmd .setCatalogId(currentCatalogId)
			.setOutputChannelId(currentOuputChannel)
			.setLanguage(currentLanguage)
			.setFilterString(constructFilterString());

		GwtCommandFacade.executeWithRetry(cmd, 3, new StatusCallback<ProductListCommandResult>(Util.i18n.loadingProducts()) {
			public void onSuccess(ProductListCommandResult result) {
				updateFilterLabel();
				noProductsFoundLabel.setVisible(result.products.isEmpty());
				filteredProductList.setProducts(result.products);
			}
		});
	}

	private String constructFilterString() {
		// TODO add drop down filter options:
		return filterString;
	}
	
	private void updateFilterLabel() {
		String actualFilter = constructFilterString();
		if (actualFilter != null && !actualFilter.trim().equals("")) {
			filterLabel.setHTML(Util.i18n.filterMessage(actualFilter)); 
			filterLabel.setVisible(true);
		} else {
			filterLabel.setVisible(false);
		}
	}

	private void updateProductSelection(final Long productId) {
		ItemDetailsCommand cmd = new ItemDetailsCommand();
		cmd .setCatalogId(currentCatalogId)
			.setItem(selectedItem.getId());
		GwtCommandFacade.executeWithRetry(cmd, 3, new StatusCallback<ItemDetailsCommandResult>() {
			public void onSuccess(ItemDetailsCommandResult result) {
				filteredProductList.setSelectedProduct(productId, result.propertyData);
			}
		});
		
		// TODO See whether it was cached 
	}
}

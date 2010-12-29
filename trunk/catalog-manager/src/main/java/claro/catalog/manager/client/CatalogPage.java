package claro.catalog.manager.client;


import claro.catalog.command.RootPropertiesCommand;
import claro.catalog.command.RootPropertiesCommandResult;
import claro.catalog.command.items.FindItems;
import claro.catalog.command.items.ItemDetailsCommand;
import claro.catalog.command.items.ItemDetailsCommandResult;
import claro.catalog.manager.client.command.StatusCallback;
import claro.jpa.catalog.Item;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.util.SMap;

public class CatalogPage extends Page {

	private LayoutPanel mainPanel;
	ProductList filteredProductList;
	private Label noProductsFoundLabel;

	private Item selectedItem;
	private SMap<Long, SMap<String, String>> filterCategories = SMap.empty();
	
	private boolean initialized;
	protected HTML filterLabel;
	
	private Long currentCatalogId;
	private Long currentOuputChannel;
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
		
		mainPanel.add(filteredProductList = new ProductList(90, 0) {{
			// search panel
			getMasterHeader().add(new DockLayoutPanel(Unit.PX) {{
				addSouth(new FlowPanel() {{
					add(filterLabel = new HTML() {{
						setVisible(false); 
					}});
					add(noProductsFoundLabel = new Label(messages.noProductsFound()) {{
						setVisible(false);
					}});
				}}, 30);
				add(new Grid(2, 3) {{
					StyleUtil.add(this, CatalogManager.Styles.filterpanel);
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
					setWidget(0, 2, new CategoriesWidget() {{
							setData(filterCategories, currentLanguage, true, true, false);
						}
						protected String getAddCategoryTooltip() {
							return messages.addCategoryFilter();
						}
						protected String getRemoveCategoryTooltip(String categoryName) {
							return messages.removeCategoryFilterTooltip(categoryName);
						}
						// TODO add.
						protected void removeCategory(Long categoryId) {
							// TODO update filtercats.
						}
					});
					setWidget(1, 0, new Button(messages.newProduct()));
				}});
			}});
		}
		
			protected void productSelected(final Long productId) {
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
		FindItems cmd = new FindItems();
		cmd.catalogId = currentCatalogId;
		cmd.outputChannelId = currentOuputChannel;
		cmd.language = currentLanguage;
		cmd.filter = constructFilterString();
		cmd.categoryIds = filterCategories.getKeys();
		// TODO set more command pars.

		GwtCommandFacade.executeWithRetry(cmd, 3, new StatusCallback<FindItems.Result>(messages.loadingProducts()) {
			public void onSuccess(FindItems.Result result) {
				updateFilterLabel();
				noProductsFoundLabel.setVisible(result.items.isEmpty());
				filteredProductList.setProducts(result.items);
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
			filterLabel.setHTML(messages.filterMessage(actualFilter)); 
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
				filteredProductList.setSelectedProduct(productId, result.categories, result.propertyData);
			}
		});
		
		// TODO See whether it was cached 
	}
}

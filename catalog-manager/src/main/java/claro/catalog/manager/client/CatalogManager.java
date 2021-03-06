package claro.catalog.manager.client;


import claro.catalog.manager.client.catalog.CatalogPage;
import claro.catalog.manager.client.importing.ImportPage;
import claro.catalog.manager.client.orders.OrderPage;
import claro.catalog.manager.client.webshop.OutputChannelPage;
import claro.catalog.manager.client.taxonomy.TaxonomyPage;
import claro.catalog.util.PropertyStringConverter;
import claro.jpa.party.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.command.gwt.GwtCommandFacade.BusyListener;
import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;

public class CatalogManager implements com.google.gwt.core.client.EntryPoint, Globals {

	public enum Styles implements Style, Globals {

		
		//old:
		button1,
		button2,
		button3,
		button4,
		button5,
		choices, 
		inherited,
		itemoverallactions, 
		itempanel,
		properties, 
		footer, 
		links, 
		headerimage, 
		itemName, 
		catalogresultspanel, 
		filterpanel, 
		productvariant, 
		masterdetailtest, 
		itemRowChanged, 
		productDetailsTitle, spinner
	}

	public static PropertyStringConverter propertyStringConverter = new PropertyStringConverter();
	
	private static User currentUser;
	private Label username;
	private FlowPanel spinner;

	private OutputChannelPage outputChannelPage;
	private CatalogPage catalogPage;


	public static Long getCurrentCatalogId() {
		return -1l;
	}
	
	public static String getUiLanguage() {
		if (currentUser != null) {
			return currentUser.getUiLanguage();
		}
		return null;
	}
	
	@Override
	public void onModuleLoad() {
		
		final LayoutPanel pageContainer = new LayoutPanel();
		
		EventBus eventBus = new SimpleEventBus();
		final PlaceController placeController = new PlaceController(eventBus);
		
		final ResourceBundle rb = GWT.create(ResourceBundle.class);
		RootLayoutPanel.get().add(new DockLayoutPanel(Unit.PX) {{
			// header
			addNorth(new HorizontalPanel() {{
				setStylePrimaryName(GlobalStylesEnum.header.toString());
				setWidth("100%");
				getElement().getStyle().setHeight(100, Unit.PC);
				add(new Image(rb.logo()) {{
					StyleUtil.addStyle(this, Styles.headerimage);
				}});
				add(spinner = new FlowPanel() {{
					setVisible(false);
					StyleUtil.addStyle(this, Styles.spinner);
					add(new Image(images.spinner()));
				}});
				GwtCommandFacade.setBusyListener(new BusyListener() {
					public void busyChanged(boolean busy) {
						spinner.setVisible(busy);
					}
				});
				
//				add(username = new Label());
//				add(new Anchor("Login...") {{
//					addClickHandler(new ClickHandler() {
//						public void onClick(ClickEvent event) {
//							Login loginCommand = new Login();
////							loginCommand.opendIdName = "reinier.bos@gmail.com";
//							loginCommand.opendIdName = "https://www.google.com/accounts/o8/id";
//							GwtCommandFacade.execute(loginCommand, new AsyncCallback<Login.Result>() {
//								public void onFailure(Throwable caught) {
//								}
//
//								public void onSuccess(Result result) {
//									username.setText(result.redirectUrl);
////									Window.open(result.redirectUrl, "_blank", "height=200,width=400,left=100," + "top=100,resizable=no,scrollbars=no,toolbar=no,status=yes");
//											            // this the most important line in order to make the authentication.
////											 Here, I am redirecting the user
//											            // from the client side to the OpenID provider URL with the discovery
////											 data generated from the
//											            // RPC call to the servlet.
//											            Window.Location.assign(result.redirectUrl);
//
//								}
//							});
//						}
//					});
//				}});
				add(new MainMenu(pageContainer) {{
					setStylePrimaryName(GlobalStylesEnum.menu.toString());
					addPage(catalogPage = new CatalogPage(placeController), messages.catalogMenu());
					addPage(new TaxonomyPage(placeController), messages.taxonomyMenu());
					addPage(new ImportPage(placeController), messages.dataExchangeMenu());
					addPage(outputChannelPage = new OutputChannelPage(placeController, messages.webshopMenu()), messages.webshopMenu());
//					addPage(new OrderPage(placeController, messages.ordersMenu()), messages.ordersMenu());
//					addPage(new EmptyPage(placeController, messages.campaignsMenu()), messages.campaignsMenu());
//					addPage(new EmptyPage(placeController, messages.contentLibraryMenu()), messages.contentLibraryMenu());
//					addPage(new EmptyPage(placeController, "Orders"), "Orders");
					addPage(new SettingsPage(placeController, "Settings"), "Settings");
//					addPage(new EmptyPage(placeController, messages.reportAndAnalysisMenu()), messages.reportAndAnalysisMenu());
//					addPage(new MasterDetailTestPage(placeController), "MasterDetail");
//					addPage(new TearupTabsTestPage(placeController), "TabTest");
					catalogPage.setOutputChannelModel(outputChannelPage.getModel());
				}});
//			topPanel.add(choices);
				
			}}, 95);

			// footer
			addSouth(new FlowPanel() {{
				StyleUtil.addStyle(this, Styles.footer);
				add(new HorizontalPanel() {{
					StyleUtil.addStyle(this, Styles.links);
					add(new Label("Powered By Kentivo"));
					add(new Label("|"));
					add(new Anchor("Privacy Policy"));
					add(new Label("|"));
					add(new Anchor("Legal Information"));
					add(new Label("|"));
					add(new Anchor("Terms of Use"));
				}}); 
				
			}}, 50);
			add(pageContainer);
			
//			if (false)
//			add(new TabLayoutPanel(50, Unit.PX) {{
//				this.addStyleName("mainTabPanel");
//				
//				add(new CatalogPage(placeController), "Catalog");
//				add(new ImportPage(placeController), "Import");
//				
//				add(new MasterDetailTestPage(placeController), "MasterDetail");
//				add(new TearupTabsTestPage(placeController), "TabTest");
////		tabPanel.add(catalog.getView(), i18n.catalog());
////		tabPanel.add(taxonomies.getView(), i18n.taxonomy());
////		tabPanel.add(navigation.getView(), i18n.navigation());
////		tabPanel.add(promotions.getView(), i18n.promotions());
////		tabPanel.add(orders.getView().asWidget(), i18n.orders());
////		tabPanel.add(settings.getView().asWidget(), i18n.settings());
//				addSelectionHandler(new SelectionHandler<Integer>() {
//					@Override
//					public void onSelection(SelectionEvent<Integer> event) {
//						currentPage = (Page)(getWidget(event.getSelectedItem()));
//						currentPage.onResize();
//						currentPage.show();
//					}
//				});
//				selectTab(0);
//			}});
		}});
		History.fireCurrentHistoryState();
	}
}

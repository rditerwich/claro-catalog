package claro.catalog.manager.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class CatalogManager implements com.google.gwt.core.client.EntryPoint {

//	private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

//	private GlobalChoices choices = new GlobalChoices();
//	private final ProductsPresenter catalog = new ProductsPresenter();
//	private final TaxonomiesPresenter taxonomies = new TaxonomiesPresenter();
//	// private final CatalogPresenter cp = new CatalogPresenter();
//	private final NavigationPresenter navigation = new NavigationPresenter();
//	private final PromotionPresenter promotions = new PromotionPresenter();
//	private final OrderPresenter orders = new OrderPresenter();
//	private final SettingsPresenter settings = new SettingsPresenter();


	@Override
	public void onModuleLoad() {
		EventBus eventBus = new SimpleEventBus();
		PlaceController placeController = new PlaceController(eventBus);
		
		final ResourceBundle rb = GWT.create(ResourceBundle.class);
		final DockLayoutPanel dlp = new DockLayoutPanel(Unit.PX);
		RootLayoutPanel.get().add(dlp);
			final HorizontalPanel topPanel = new HorizontalPanel();
			Util.add(topPanel, Styles.catalogheader);
			topPanel.getElement().getStyle().setHeight(100, Unit.PC);
				Image headerImage = new Image(rb.logo());
				Util.add(headerImage, Styles.headerimage);
			topPanel.add(headerImage);
//			topPanel.add(choices);
		dlp.addNorth(topPanel, 85);
			HorizontalPanel footer = new HorizontalPanel();
				Util.add(footer, Styles.footer);
				Label poweredBy = new Label("Powered By AgileXS");
				Util.add(poweredBy, Styles.legal);
			footer.add(poweredBy);
			footer.setCellVerticalAlignment(poweredBy, HorizontalPanel.ALIGN_MIDDLE);
				HorizontalPanel links = new HorizontalPanel(); 
				Util.add(links, Styles.links);
				
				links.add(new Anchor("Privacy Policy"));
				links.add(new Label("|"));
				links.add(new Anchor("Legal Information"));
				links.add(new Label("|"));
				links.add(new Anchor("Terms of Use"));
			footer.add(links);
			footer.setCellVerticalAlignment(links, HorizontalPanel.ALIGN_MIDDLE);
		dlp.addSouth(footer, 40);
			final TabLayoutPanel tabPanel = new TabLayoutPanel(50, Unit.PX);
			tabPanel.addStyleName("mainTabPanel");
		dlp.add(tabPanel);

		CatalogPage catalogPage = new CatalogPage(placeController);
		ImportPage importPage = new ImportPage(placeController);
		tabPanel.add(importPage, "Import"); // TODO Remove this again...
		tabPanel.add(catalogPage, "Details"); // TODO Remove this again...
//		tabPanel.add(catalog.getView(), i18n.catalog());
//		tabPanel.add(taxonomies.getView(), i18n.taxonomy());
//		tabPanel.add(navigation.getView(), i18n.navigation());
//		tabPanel.add(promotions.getView(), i18n.promotions());
//		tabPanel.add(orders.getView().asWidget(), i18n.orders());
//		tabPanel.add(settings.getView().asWidget(), i18n.settings());
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				((Page)tabPanel.getWidget(event.getSelectedItem())).show();
			}
		});
		importPage.show();
//				case 0:
//					catalog.show();
//					break;
//				case 1:
//					taxonomies.show();
//					break;
//				case 2:
//					navigation.show();
//					break;
//				case 3:
//					promotions.show();
//					break;
//				case 4:
//					orders.show();
//					break;
//				case 5:
//					settings.show();
//					break;
//				default:
//				}
//			}
//		});
	}
}

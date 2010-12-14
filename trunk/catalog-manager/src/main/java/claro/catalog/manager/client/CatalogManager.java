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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

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
		final PlaceController placeController = new PlaceController(eventBus);
		
		final ResourceBundle rb = GWT.create(ResourceBundle.class);
		RootLayoutPanel.get().add(new DockLayoutPanel(Unit.PX) {{
			// header
			addNorth(new HorizontalPanel() {{
				Styles.add(this, Styles.catalogheader);
				getElement().getStyle().setHeight(100, Unit.PC);
				add(new Image(rb.logo()) {{
					Util.add(this, Styles.headerimage);
				}});
//			topPanel.add(choices);
				
			}}, 85);

			// footer
			addSouth(new HorizontalPanel() {{
				Styles.add(this, Styles.footer);
				
				// PoweredBy
				Label poweredBy;
				add(poweredBy = new Label("Powered By AgileXS") {{
					Util.add(this, Styles.legal);
					
				}});
				setCellVerticalAlignment(poweredBy, HorizontalPanel.ALIGN_MIDDLE);
				
				// Links
				HorizontalPanel links;
				add(links = new HorizontalPanel() {{
					Styles.add(this, Styles.links);
					
					add(new Anchor("Privacy Policy"));
					add(new Label("|"));
					add(new Anchor("Legal Information"));
					add(new Label("|"));
					add(new Anchor("Terms of Use"));
				}}); 
				setCellVerticalAlignment(links, HorizontalPanel.ALIGN_MIDDLE);
				
			}}, 40);
			
			add(new TabLayoutPanel(50, Unit.PX) {{
				this.addStyleName("mainTabPanel");
				
				add(new CatalogPage(placeController), "Details");
				add(new ImportPage(placeController), "Import");
				
				add(new MasterDetailTestPage(placeController), "MasterDetail");
//		tabPanel.add(catalog.getView(), i18n.catalog());
//		tabPanel.add(taxonomies.getView(), i18n.taxonomy());
//		tabPanel.add(navigation.getView(), i18n.navigation());
//		tabPanel.add(promotions.getView(), i18n.promotions());
//		tabPanel.add(orders.getView().asWidget(), i18n.orders());
//		tabPanel.add(settings.getView().asWidget(), i18n.settings());
				addSelectionHandler(new SelectionHandler<Integer>() {
					@Override
					public void onSelection(SelectionEvent<Integer> event) {
//						((Page)((ScrollPanel)getWidget(event.getSelectedItem())).getWidget()).show();
						((Page)(getWidget(event.getSelectedItem()))).show();
					}
				});
				selectTab(1);
			}});
		}});
	}
}

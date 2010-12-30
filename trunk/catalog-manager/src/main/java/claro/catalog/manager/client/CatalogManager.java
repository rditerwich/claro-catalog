package claro.catalog.manager.client;


import claro.catalog.manager.client.importing.ImportPage;
import claro.catalog.util.PropertyStringConverter;

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

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;

public class CatalogManager implements com.google.gwt.core.client.EntryPoint {

	public enum Styles implements Style {
		button1,
		button2,
		button3,
		button4,
		button5,
		catalogheader,
		choices, 
		derived,
		itemoverallactions, 
		itempanel,
		properties, 
		footer, 
		legal, 
		links, 
		headerimage, 
		productprice, 
		productname, 
		product, 
		productpanel, 
		itemName, 
		catalogresultspanel, 
		filterpanel, 
		productvariant, 
		masterdetailtest, 
		itemRowChanged, 
		productDetailsTitle
	}

//	private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

//	private GlobalChoices choices = new GlobalChoices();
//	private final ProductsPresenter catalog = new ProductsPresenter();
//	private final TaxonomiesPresenter taxonomies = new TaxonomiesPresenter();
//	// private final CatalogPresenter cp = new CatalogPresenter();
//	private final NavigationPresenter navigation = new NavigationPresenter();
//	private final PromotionPresenter promotions = new PromotionPresenter();
//	private final OrderPresenter orders = new OrderPresenter();
//	private final SettingsPresenter settings = new SettingsPresenter();

	public static PropertyStringConverter propertyStringConverter = new PropertyStringConverter();
	
	@Override
	public void onModuleLoad() {
		EventBus eventBus = new SimpleEventBus();
		final PlaceController placeController = new PlaceController(eventBus);
		
		final ResourceBundle rb = GWT.create(ResourceBundle.class);
		RootLayoutPanel.get().add(new DockLayoutPanel(Unit.PX) {{
			// header
			addNorth(new HorizontalPanel() {{
				StyleUtil.add(this, Styles.catalogheader);
				getElement().getStyle().setHeight(100, Unit.PC);
				add(new Image(rb.logo()) {{
					StyleUtil.add(this, Styles.headerimage);
				}});
//			topPanel.add(choices);
				
			}}, 85);

			// footer
			addSouth(new HorizontalPanel() {{
				StyleUtil.add(this, Styles.footer);
				
				// PoweredBy
				Label poweredBy;
				add(poweredBy = new Label("Powered By AgileXS") {{
					StyleUtil.add(this, Styles.legal);
					
				}});
				setCellVerticalAlignment(poweredBy, HorizontalPanel.ALIGN_MIDDLE);
				
				// Links
				HorizontalPanel links;
				add(links = new HorizontalPanel() {{
					StyleUtil.add(this, Styles.links);
					
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
						((Page)(getWidget(event.getSelectedItem()))).show();
					}
				});
				selectTab(1);
			}});
		}});
	}
}

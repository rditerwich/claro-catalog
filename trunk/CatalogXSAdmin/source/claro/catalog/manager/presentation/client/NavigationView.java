package claro.catalog.manager.presentation.client;

import claro.catalog.manager.presentation.client.binding.BindingConverter;
import claro.catalog.manager.presentation.client.binding.HasTextBinding;
import claro.catalog.manager.presentation.client.binding.ListBoxBinding;
import claro.catalog.manager.presentation.client.cache.CatalogCache;
import claro.catalog.manager.presentation.client.i18n.I18NCatalogXS;
import claro.catalog.manager.presentation.client.page.View;
import claro.catalog.manager.presentation.client.shop.Navigation;
import claro.catalog.manager.presentation.client.shop.NavigationBinding;
import claro.catalog.manager.presentation.client.widget.ExtendedTree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NavigationView extends Composite implements View {

	private static final int VISIBLE_ITEMS = 15;

	private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

	final SplitLayoutPanel panel = new SplitLayoutPanel();
	final ExtendedTree tree = new ExtendedTree();
	final ItemParentsView top = new ItemParentsView();
	final Button saveButton = new Button(i18n.saveChanges());

	private Label navigationNameLabel;
	
	private NavigationBinding<Navigation> currentNavigationBinding = new NavigationBinding<Navigation>();
	private NavigationBinding<Navigation> currentChildNavigationBinding = new NavigationBinding<Navigation>();
	private String language;
	
	
	private final BindingConverter<Navigation, String> navigationConverter = new BindingConverter<Navigation, String>() {
		public Navigation convertFrom(String data) {
			// TODO
			return null;
		}
		public String convertTo(Navigation navigation) {
			if (navigation.getCategory() != null) {
				return CatalogCache.get().getCategoryName(navigation.getCategory().getId(), language);
			} else {
				return "group";
			}
		}
	};
	
	public NavigationView() {
		initWidget(panel);
		final DockLayoutPanel topLevelNavigationPanel = new DockLayoutPanel(Unit.PX);

		topLevelNavigationPanel.addNorth(new Label(i18n.explainNavigationList()), 70);
			final VerticalPanel verticalPanel = new VerticalPanel();
				navigationNameLabel = new Label();
			verticalPanel.add(navigationNameLabel);
				Grid subNavigationsAndCategories = new Grid(2, 3);
					ListBox subNavigations = new ListBox();
					subNavigations.setVisibleItemCount(VISIBLE_ITEMS);

					VerticalPanel upDown = new VerticalPanel();
						Button upNavigation = new Button(i18n.up());
						Util.add(upNavigation, Styles.button2);
						Button downNavigation = new Button(i18n.down());
						Util.add(downNavigation, Styles.button2);
					upDown.add(upNavigation);
					upDown.add(downNavigation);
					
				subNavigationsAndCategories.setWidget(0, 0, new Label(i18n.subNavigations()));
				subNavigationsAndCategories.setWidget(1, 0, subNavigations);
				subNavigationsAndCategories.setWidget(1, 1, upDown);
				Util.add(saveButton, Styles.button5);
			verticalPanel.add(subNavigationsAndCategories);
			verticalPanel.add(new Anchor(i18n.newNavigation()));
			verticalPanel.add(saveButton);
		topLevelNavigationPanel.add(verticalPanel);
		panel.addWest(tree, 300);
		panel.add(topLevelNavigationPanel);
		
		HasTextBinding.bind(navigationNameLabel, currentNavigationBinding, navigationConverter);
		ListBoxBinding.bind(subNavigations, currentNavigationBinding.subNavigation(), currentChildNavigationBinding, navigationConverter);
	}

	ItemParentsView getItemParentsView() {
		return top;
	}
	
	public void setCurrentLanguage(String language) {
		this.language = language;
		
	}
	
	public NavigationBinding<Navigation> getCurrentNavigationBinding() {
		return currentNavigationBinding;
	}

	@Override
	public Widget asWidget() {
		return this;
	}
	
	public ExtendedTree getTree() {
		return tree;
	}

	public HasClickHandlers getSaveButtonClickHandler() {
		return saveButton;
	}
}

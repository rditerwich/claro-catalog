package claro.catalog.manager.client;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

public abstract class Page extends Composite implements RequiresResize, ProvidesResize, Globals {
	private final PlaceController placeController;
	private MainMenu menu;

	public abstract void show();
	
	public Page(PlaceController placeController) {
		this.placeController = placeController;
	}
	
	public void goTo(Place place) {
		placeController.goTo(place);
	}
	
	@Override
	public void onResize() {
		if (MainMenu.isCurrentPage(this) && getWidget() instanceof RequiresResize) {
			((RequiresResize) getWidget()).onResize();
		}
	}
	
	void setMainMenu(MainMenu menu) {
		this.menu = menu;
	}
	
	protected void showPage(Page page) {
		if (menu != null) {
			menu.showPage(page);
		}
	}
	
	protected void initialize() {
	}
}

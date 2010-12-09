package claro.catalog.manager.client;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Composite;


public abstract class Page extends Composite {
	private final PlaceController placeController;

	public abstract Place getPlace();
	public abstract void show();
	
	public Page(PlaceController placeController) {
		this.placeController = placeController;
	}
	
	public void goTo(Place place) {
		placeController.goTo(place);
	}
}
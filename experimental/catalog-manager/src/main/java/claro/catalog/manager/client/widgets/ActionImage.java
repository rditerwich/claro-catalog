package claro.catalog.manager.client.widgets;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

public class ActionImage extends Image {

	public ActionImage(ImageResource image, ClickHandler handler) {
		super(image);
		addClickHandler(handler);
		setStyleName("ee-ActionImage");
	}

}

package claro.catalog.manager.client.widgets;

import org.cobogw.gwt.user.client.ui.RoundedPanel;

import claro.catalog.manager.client.Globals;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Panel;

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.widgets.MasterDetail;

public class CatalogManagerMasterDetail extends MasterDetail implements Globals {

	public static enum Styles implements Style { closeButton };
	
	public CatalogManagerMasterDetail(int headerSize) {
		super(headerSize, 0, 24);
	}

	@Override
	protected HasWidgets createHeaderWrapper(LayoutPanel parent) {
		Panel result = new RoundedPanel(RoundedPanel.ALL, 4) {{
			setBorderColor("white");
		}};
		parent.add(result);
		return result;
	}
	
	@Override
	protected HasWidgets createMasterWrapper(LayoutPanel parent) {
		Panel result = new RoundedPanel(RoundedPanel.ALL, 4) {{
			setBorderColor("white");
		}};
		parent.add(result);
		return result;
	}
	
	@Override
	protected HasWidgets createDetailWrapper(LayoutPanel parent) {
		
		final ClickHandler closeHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				closeDetail(true);
				detailClosed();
			}
		};
		
		Panel panel;
		parent.add(panel = new DockLayoutPanel(Unit.PX) {{
			addNorth(new DockLayoutPanel(Unit.PX) {{
				addWest(createCloseButton(closeHandler), 22);
				addEast(createCloseButton(closeHandler), 22);
			}}, 18);
		}});
		return panel;
	}


	public static Panel createCloseButton(final ClickHandler closeHandler) {
		return new FlowPanel() {{
			final Image image = new Image(images.clearImage1());
			add(image);
			image.setHeight("0");
			getElement().getStyle().setCursor(Cursor.POINTER);
			getElement().getStyle().setPaddingTop(2, Unit.PX);
			getElement().getStyle().setPaddingLeft(4, Unit.PX);
			getElement().getStyle().setPaddingRight(4, Unit.PX);
			addDomHandler(closeHandler, ClickEvent.getType());
		  addDomHandler(new MouseOverHandler() {
				public void onMouseOver(MouseOverEvent event) {
					image.setHeight("14px");
				}
			}, MouseOverEvent.getType());
			addDomHandler(new MouseOutHandler() {
				public void onMouseOut(MouseOutEvent event) {
					image.setHeight("0");
				}
			}, MouseOutEvent.getType());
		}};
	}
	
	protected void detailClosed() {
		
	}
}

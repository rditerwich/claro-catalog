package agilexs.catalogxsadmin.presentation.client;

import agilexs.catalogxsadmin.presentation.client.i18n.I18NCatalogXS;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;


public class ItemDetails extends Composite {

	private Image productImage;
	private Label priceLabel;

	public ItemDetails() {
		initWidget(new FlowPanel() {{
//			add(new Trail());

			// Title
			add(new TextBox(){{
				Util.add(this, Styles.itemName);
			}});
			
			// Image, Price, RelatedInfo
			add(new HorizontalPanel(){{
			    add(productImage = new Image() {{
			    	setSize("150px", "150px");
			    }});
			    add(priceLabel = new Label(){{
			    	Util.add(this, Styles.productprice);
			    	setCellVerticalAlignment(this, HorizontalPanel.ALIGN_MIDDLE);
			    }});
			    add(new FlowPanel(){{
			    	add(new Anchor(Util.i18n.containedProducts(0)));
			    }});
			}});
			
			
		}});
	}
}

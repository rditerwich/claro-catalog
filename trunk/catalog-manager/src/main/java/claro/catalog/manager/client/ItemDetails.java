package claro.catalog.manager.client;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import claro.catalog.data.PropertyData;
import claro.catalog.data.PropertyGroupInfo;
import claro.catalog.data.PropertyInfo;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyValue;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.util.SMap;


public class ItemDetails extends Composite {
	
	private Label productPrice;
	private Image productImage;
	
	private ItemPropertyValues propertyValues;
	
	public ItemDetails() {
		initWidget(new FlowPanel() {{
//			add(new Trail());

			// Title
			add(new TextBox() {{
				Util.add(this, Styles.itemName);
			}});
			
			// Image, Price, RelatedInfo
			add(new HorizontalPanel(){{
			    add(productImage = new Image() {{
			    	setSize("150px", "150px");
			    }});
			    add(productPrice = new Label() {{
			    	Util.add(this, Styles.productprice);
			    	setCellVerticalAlignment(this, HorizontalPanel.ALIGN_MIDDLE);
			    }});
			    add(new FlowPanel() {{
			    	add(new Anchor(Util.i18n.containedProducts(0)));
			    }});
			}});
			
			// Properties
			add(new Label(Util.i18n.properties()));
			add(propertyValues = new ItemPropertyValues());		
			
			// TODO Add a popup panel at the bottom with property definitions (+ values??).
			// TODO add a popup panel at the bottom with property groups?
		}});
	}
	
	/**
	 * set the item data and (re)render.
	 * 
	 * @param item
	 * @param values
	 */
	public void setItemData(Long itemId, SMap<PropertyGroupInfo, SMap<PropertyInfo, PropertyData>> values) {
		propertyValues.setItemData(itemId, values);
	}
	

}

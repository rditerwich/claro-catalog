package claro.catalog.manager.client;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import claro.catalog.data.PropertyData;
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
	private static int NAME_COLUMN = 0;
	private static int TYPE_COLUMN = 1;
	private static int VALUE_COLUMN = 2;
	private static int LANG_COLUMNS = 3;
	private static int NR_FIXED_COLS = 3;

	private Image productImage;
	private ItemPropertyValues propertyValues;
	private SMap<PropertyInfo, PropertyData> values;
	
	public ItemDetails() {
		initWidget(new FlowPanel() {{
//			add(new Trail());

			// Title
			add(new TextBox() {{
				Util.add(this, Styles.itemName);
//				HasTextBinding.bind(this, itemBinding.name());
			}});
			
			// Image, Price, RelatedInfo
			add(new HorizontalPanel(){{
			    add(productImage = new Image() {{
			    	setSize("150px", "150px");
			    }});
			    add(new Label() {{
			    	Util.add(this, Styles.productprice);
			    	setCellVerticalAlignment(this, HorizontalPanel.ALIGN_MIDDLE);
			    	
//			    	HasTextBinding.bind(this, itemBinding.price());
			    }});
			    add(new FlowPanel() {{
			    	add(new Anchor(Util.i18n.containedProducts(0)));
			    }});
			}});
			
			// Properties
			add(new Label(Util.i18n.properties()));
			add(propertyValues = new ItemPropertyValues());			
		}});
	}
	
	/**
	 * set the item data and (re)render.
	 * 
	 * @param item
	 * @param values
	 */
	public void setItemData(Long itemId, SMap<PropertyInfo, PropertyData> values) {
		this.values = values;
		
		propertyValues.setItemData(itemId, values);
	}
	

}

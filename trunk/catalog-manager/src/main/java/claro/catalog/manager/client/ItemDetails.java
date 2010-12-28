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


abstract public class ItemDetails extends Composite {
	
	private Label productPrice;
	private Image productImage;
	
	private ItemPropertyValues propertyValues;
	
	public ItemDetails(final String uiLanguage, final String language, final OutputChannel outputChannel) {
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
			add(propertyValues = new ItemPropertyValues(uiLanguage, language, outputChannel) {
				protected void propertyValueSet(Long itemId, PropertyInfo propertyInfo, String language, Object value) {
					ItemDetails.this.propertyValueSet(itemId, propertyInfo, language, value);
				}
				protected void propertyValueErased(Long itemId, PropertyInfo propertyInfo, String language) {
					ItemDetails.this.propertyValueRemoved(itemId, propertyInfo, language);
				}
			});		
			
			// TODO Add a popup panel at the bottom with property definitions (+ values??).
			// TODO add a popup panel at the bottom with property groups?
		}});
	}
	
	
	public void setUiLanguage(String uiLanguage) {
		propertyValues.setUiLanguage(uiLanguage);
	}
	
	public void setLanguage(String language) {
		propertyValues.setLanguage(language);
	}
	
	public void setOutputChannel(OutputChannel outputChannel) {
		propertyValues.setOutputChannel(outputChannel);
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
	
	abstract protected void propertyValueSet(Long itemId, PropertyInfo propertyInfo, String language, Object value);

	abstract protected void propertyValueRemoved(Long itemId, PropertyInfo propertyInfo, String language);

}

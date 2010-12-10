package claro.catalog.manager.client;

import com.google.gwt.user.client.ui.UIObject;

public enum Styles {
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
	masterdetail, 
	detailPanel, 
	masterselection, 
	erasePanel, 
	selectionPanel, 
	masterdetailtest;
	
	public static void add(UIObject uiObject, Styles style) {
		uiObject.addStyleName(style.toString());
	}

}

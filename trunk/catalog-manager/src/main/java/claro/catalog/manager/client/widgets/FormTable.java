package claro.catalog.manager.client.widgets;

import claro.catalog.manager.client.Globals;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.Style;

public class FormTable extends Composite implements Globals {

	public enum Styles implements Style {
		FormTable, FormTableLabel, FormTableWidget, FormTableHelp;
		
		public String toString() {
			return "ee-" + super.toString();
		};
	}

	private FlexTable table;
	private Label helpText;
	private Widget lastWidget;
	private Element textCell;
	private int currentHelpRow = -1;

	public FormTable() {
		table = new FlexTable();
		table.getElement().getStyle().setWidth(100, Unit.PCT);
		table.setStylePrimaryName(Styles.FormTable.toString());
		table.getColumnFormatter().setStylePrimaryName(0, Styles.FormTableLabel.toString());
		table.getColumnFormatter().setStylePrimaryName(1, Styles.FormTableWidget.toString());
		table.getColumnFormatter().setStylePrimaryName(2, Styles.FormTableHelp.toString());
		table.getCellFormatter().setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);
		table.getFlexCellFormatter().setRowSpan(0, 2, 100);
		textCell = table.getFlexCellFormatter().getElement(0, 2);
		textCell.setClassName(Styles.FormTableHelp.toString());
		helpText = new Label();
		helpText.setStylePrimaryName(Styles.FormTableHelp.toString());
		table.setWidget(0, 2, helpText);
		table.getCellFormatter().getElement(0, 0).getParentElement().getStyle().setHeight(0, Unit.PX);
		initWidget(table);
	}
	
	public <T extends Widget> T add(T widget, String help) {
		return add((Widget) null, widget, help);
	}
	
	public <T extends Widget> T add(String label, final T widget, final String help) {
		return add(new InlineLabel(label), widget, help);
	}
	
	public <T extends Widget> T add(Widget label, final T widget, final String help) {
		final int row = table.getRowCount();
		final MouseMoveHandler MouseMove = new MouseMoveHandler() {
			public void onMouseMove(MouseMoveEvent event) {
				showHelp(row, widget, help);
			}
		};
		final MouseOutHandler mouseOut = new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				showHelp(row, null, help);
			}
		};
		final KeyUpHandler keyUp = new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				showHelp(row, widget, help);
			}
		};

		if (label != null) {
			label.addDomHandler(MouseMove, MouseMoveEvent.getType());
			label.addDomHandler(mouseOut, MouseOutEvent.getType());
			table.setWidget(row, 0, label);
			table.setWidget(row, 1, new FlowPanel() {{
				add(widget);
				addDomHandler(MouseMove, MouseMoveEvent.getType());
				addDomHandler(mouseOut, MouseOutEvent.getType());
				widget.addDomHandler(keyUp, KeyUpEvent.getType());
			}});

		} else {
			table.setWidget(row, 0, widget);
			table.getFlexCellFormatter().setColSpan(row, 0, 2);
		}
		lastWidget = widget;
		return widget;
	}
	
	private void showHelp(int row, Widget widget, String help) {
		if (currentHelpRow != -1) {
			setBackGround(table.getWidget(currentHelpRow, 1).getElement(), "");
			table.getWidget(currentHelpRow, 1).getElement();
			helpText.setText("");
			table.getFlexCellFormatter().setColSpan(currentHelpRow, 1, 1);
			currentHelpRow = -1;
		}
		if (widget != null && help != null && !help.toString().equals("")) {
			currentHelpRow = row;
			setBackGround(table.getWidget(currentHelpRow, 1).getElement(), "images/dash.png");
			helpText.setText(help);
			
			int bottom = lastWidget.getAbsoluteTop() + lastWidget.getOffsetHeight() - table.getAbsoluteTop();
			
			// try place text under the line
			boolean extendDash = true;
			int padding = widget.getAbsoluteTop() - textCell.getAbsoluteTop() + widget.getOffsetHeight() / 2 + 8;
			if (padding + helpText.getOffsetHeight() > bottom) {
				
				// try above the line
				padding = widget.getAbsoluteTop() - textCell.getAbsoluteTop() - helpText.getOffsetHeight(); 
				if (padding < 0) {
					padding = 0;
					extendDash = false;
				}
			}
			textCell.getStyle().setPaddingTop(padding, Unit.PX);
			
			if (extendDash) {
				table.getFlexCellFormatter().setColSpan(currentHelpRow, 1, 2);
			}
		}
	}
	
	public native static void setBackGround(
      Element panel, String imgPath) /*-{
if(panel != null) {
panel.style.background  = " transparent url(" + imgPath + ") repeat-x 20px 10px";
}	
}-*/;
}

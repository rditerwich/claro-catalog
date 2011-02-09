package claro.catalog.manager.client.widgets;

import claro.catalog.manager.client.Globals;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;

public class FormTable extends Composite implements Globals {

	public enum Styles implements Style {
		FormTable, FormTableLabel, FormTableWidget, FormTableSepHelp, FormTableHelp;
		
		public String toString() {
			return "ee-" + super.toString();
		};
	}

	private FlexTable table;
	private HTML helpText;
	private Widget lastWidget;
	private Element textCell;
	private int currentHelpRow = -1;

	public FormTable() {
		table = new FlexTable();
		table.setStylePrimaryName(Styles.FormTable.toString());
		table.getColumnFormatter().setStylePrimaryName(0, Styles.FormTableLabel.toString());
		table.getColumnFormatter().setStylePrimaryName(1, Styles.FormTableWidget.toString());
		table.getColumnFormatter().setStylePrimaryName(2, Styles.FormTableSepHelp.toString());
		table.getColumnFormatter().setStylePrimaryName(3, Styles.FormTableHelp.toString());
		table.getCellFormatter().setVerticalAlignment(0, 3, HasVerticalAlignment.ALIGN_TOP);
		table.getFlexCellFormatter().setRowSpan(0, 3, 100);
		table.getFlexCellFormatter().getElement(0, 0).setClassName(Styles.FormTableLabel.toString());
		table.getFlexCellFormatter().getElement(0, 1).setClassName(Styles.FormTableWidget.toString());
		table.getFlexCellFormatter().getElement(0, 2).setClassName(Styles.FormTableSepHelp.toString());
		textCell = table.getFlexCellFormatter().getElement(0, 3);
		textCell.setClassName(Styles.FormTableHelp.toString());
		helpText = new HTML();
		helpText.setStylePrimaryName(Styles.FormTableHelp.toString());
		table.setWidget(0, 2, new Label(" "));
		table.setWidget(0, 3, helpText);
		table.getCellFormatter().getElement(0, 0).getParentElement().getStyle().setHeight(0, Unit.PX);
		initWidget(table);
	}
	
	public int findRow(Widget w) {
		for (int row = 0; row < table.getRowCount(); row++) {
			if (table.getWidget(row, 1) == w.getParent())
				return row;
		}
		return -1;
	}
	
	public void setRowVisible(Widget w, boolean visible) {
		int row = findRow(w);
		if (row >= 0) {
			table.getRowFormatter().setVisible(row, visible);
		}
	}
	
	public <T extends Widget> T add(T widget, String help) {
		return add((Widget) null, widget, help);
	}
	
	public <T extends Widget> T add(String label, final T widget) {
		return add(label, widget, null);
	}
	
	public <T extends Widget> T add(String label, final T widget, final String help) {
		return add(new Label(label), widget, help);
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
			table.setWidget(row, 2, new Label(""));

		} else {
			table.setWidget(row, 0, widget);
			table.getFlexCellFormatter().setColSpan(row, 0, 3);
		}
		lastWidget = widget;
		return widget;
	}
	
	private void showHelp(int row, Widget widget, String help) {
		if (currentHelpRow != -1) {
			StyleUtil.setBackGround(table.getWidget(currentHelpRow, 1).getElement(), "");
			table.getWidget(currentHelpRow, 1).getElement();
			helpText.setHTML("");
			table.getFlexCellFormatter().setColSpan(currentHelpRow, 1, 1);
			currentHelpRow = -1;
		}
		if (row == 0) return;
		if (widget != null && help != null && !help.toString().equals("")) {
			currentHelpRow = row;
			Element dashElement = table.getWidget(currentHelpRow, 1).getElement();
			StyleUtil.setBackGround(dashElement, "images/dash.png");

			helpText.setHTML(help);
			
			int bottom = table.getOffsetHeight();
			
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
				table.getFlexCellFormatter().setColSpan(currentHelpRow, 1, 3);
			} else {
				table.getFlexCellFormatter().setColSpan(currentHelpRow, 1, 2);
			}
		}
	}
}

package claro.catalog.manager.presentation.client;


import claro.catalog.manager.presentation.client.Util.DeleteHandler;
import claro.catalog.manager.presentation.client.i18n.I18NCatalogXS;
import claro.catalog.manager.presentation.client.page.View;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

public class ItemParentsView extends Composite implements View {

	private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);
	private static final ResourceBundle rb = GWT.create(ResourceBundle.class);
	private static final int DELETE_COLUMN = 1;

	private final FlowPanel panel = new FlowPanel();
	private final Grid parentGrid = new Grid(5, 2);
	private final ListBox allParentsListBox = new ListBox();
	private final Button addButton = new Button(i18n.add());
	private DeleteHandler<Integer> deleteHandler;

	public ItemParentsView() {
		initWidget(panel);
		panel.add(parentGrid);
		parentGrid.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (deleteHandler == null)
					return;
				final Cell c = parentGrid.getCellForEvent(event);

				if (c.getCellIndex() == DELETE_COLUMN) {
					deleteHandler.onDelete(Integer.valueOf(c.getRowIndex()));
				}
			}
		});
		final HorizontalPanel hp = new HorizontalPanel();

		panel.add(hp);
		hp.add(allParentsListBox);
		addButton.getElement().getStyle().setMarginLeft(20, Unit.PX); // Yuck!!
		Util.add(addButton, Styles.button1);
		hp.add(addButton);
	}

	@Override
	public Widget asWidget() {
		return this;
	}

	public void clearParentTable() {
		parentGrid.resizeRows(0);
	}

	public void addParentToList(String parent) {
		int row = parentGrid.getRowCount();
		parentGrid.resizeRows(row + 1);
		parentGrid.setWidget(row, 0, new Label(parent));
		parentGrid.setWidget(row, DELETE_COLUMN, new Image(rb.deleteImage()));
	}

	public int getSelectedNewParent() {
		return allParentsListBox.getSelectedIndex();
	}

	public ListBox getAllParentsListBox() {
		return allParentsListBox;
	}

	public HasClickHandlers buttonAddHasClickHandlers() {
		return addButton;
	}

	public void setDeleteHandler(DeleteHandler<Integer> deleteHandler) {
		this.deleteHandler = deleteHandler;
	}
}

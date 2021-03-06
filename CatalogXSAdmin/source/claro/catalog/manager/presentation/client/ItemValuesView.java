package claro.catalog.manager.presentation.client;

import java.util.ArrayList;

import claro.catalog.manager.presentation.client.catalog.PropertyType;

import claro.catalog.manager.presentation.client.i18n.I18NCatalogXS;
import claro.catalog.manager.presentation.client.page.View;
import claro.catalog.manager.presentation.client.util.CatalogXSWidgetUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * View that displays a table of inherited properties for one parent group.
 * The user can modify the values.
 */
public class ItemValuesView extends Composite implements View {

  private final static I18NCatalogXS i18n = GWT.create(I18NCatalogXS.class);

  public class PGPRowView {
    final Label name = new Label();
    final Label type = new Label();
    final SimplePanel valueWrapper = new SimplePanel();
    final SimplePanel defaultValueWrapper = new SimplePanel();

    public HasText getName() {
      return name;
    }

    public HasText getType() {
      return type;
    }

    public Widget getDefaultValueWidget() {
      return defaultValueWrapper;
    }

    public Widget getValueWidget() {
      return valueWrapper;
    }

    public Widget setDefaultValueWidget(PropertyType type) {
      return CatalogXSWidgetUtil.setPropertyTypeWidget(defaultValueWrapper, type, false);
    }

    public Widget setValueWidget(PropertyType type) {
      return CatalogXSWidgetUtil.setPropertyTypeWidget(valueWrapper, type, false);
    }
  }

  private Grid grid = new Grid(1, 4);

  private ArrayList<PGPRowView> rowViews = new ArrayList<PGPRowView>();

  public ItemValuesView() {
    initWidget(grid);
    addHeader();
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  public void resizeRows(int rows) {
    grid.resizeRows(rows + 1);
  }

  public PGPRowView setRow(int row) {
    PGPRowView rowView;

    if (rowViews.size() > row) {
      rowView = rowViews.get(row);
    } else {
      rowView = new PGPRowView();
      rowViews.add(rowView);
    }
    row = row + 1; //offset header
    if (row >= grid.getRowCount()) {
      grid.resizeRows(row + 1);
    }
    grid.setWidget(row, 0, (Widget) rowView.getName());
    grid.setWidget(row, 1, (Widget) rowView.getType());
    grid.setWidget(row, 2, rowView.getDefaultValueWidget());
    grid.setWidget(row, 3, rowView.getValueWidget());
    grid.getCellFormatter().addStyleName(row, 3, "languageField");
    return rowView;
  }

  private void addHeader() {
    grid.setWidget(0, 0, new InlineLabel(i18n.name()));
    grid.setWidget(0, 1, new InlineLabel(i18n.type()));
    grid.setWidget(0, 2, new InlineLabel(i18n.value()));
    grid.setWidget(0, 3, new InlineLabel(i18n.languageSpecificValue()));
    grid.getCellFormatter().addStyleName(0, 3, "languageField");
  }
}

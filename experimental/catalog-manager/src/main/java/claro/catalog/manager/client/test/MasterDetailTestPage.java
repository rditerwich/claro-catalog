package claro.catalog.manager.client.test;

import static easyenterprise.lib.gwt.client.StyleUtil.createStyle;

import java.util.ArrayList;
import java.util.List;

import org.cobogw.gwt.user.client.ui.RoundedPanel;

import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Page;
import claro.catalog.manager.client.CatalogManager.Styles;
import claro.catalog.manager.client.widgets.CatalogManagerMasterDetail;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;

import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.gwt.client.widgets.MasterDetail;
import easyenterprise.lib.gwt.client.widgets.Table;

public class MasterDetailTestPage extends Page {

	private final LayoutPanel mainPanel;
	private boolean initialized;
	private Table masterTable;

	public MasterDetailTestPage(PlaceController placeController) {
		super(placeController);
		initWidget(mainPanel = new LayoutPanel());
	}

	@Override
	public void show() {
		initializeMainPanel();
	}

	private void initializeMainPanel() {
		if (initialized) {
			return;
		}
		initialized = true;
		
		MasterDetail m;
		final List<String> masterData = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			masterData.add("bladibladibladibla" + i);
		}
		mainPanel.add(m = new CatalogManagerMasterDetail(100) {{
			final Table masterTable = new Table();
			masterTable.resizeColumns(5);
			masterTable.resizeHeaderRows(1);
			masterTable.setHeaderText(0, 0, "col1");
//			masterTable.setHeaderText(0, 1, "col2");
//			masterTable.setHeaderText(0, 2, "col2");
//			masterTable.setHeaderText(0, 3, "col2");
//			masterTable.setHeaderText(0, 4, "col2");
			
			setMaster(masterTable);
			// We need a row for every master:
			masterTable.resizeRows(masterData.size());
			
			int i = 0;
			for (String master : masterData) {
				final int row = i++;
				System.out.println("Row: " + row);
				masterTable.setWidget(row, 0, new Anchor(master + " " + row) {{
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							event.stopPropagation();
							System.out.println("Opening Row: " + row);
							setDetail(new FlowPanel() {{
									StyleUtil.addStyle(this, CatalogManager.Styles.masterdetailtest);
									add(new Anchor("Close") {{
										addClickHandler(new ClickHandler() {
											public void onClick(ClickEvent event) {
												event.stopPropagation();
												System.out.println("Closing Row: " + row);
												closeDetail(true);
											}
										});
									add(new Label("Details for " + masterData.get(row) + "..."));
								}});
							}});

							openDetail(row);
						}
					});
				}});
//				masterTable.setWidget(row, 1, new Label(master + row));
//				masterTable.setWidget(row, 2, new Label(master + row));
//				masterTable.setWidget(row, 3, new Label(master + row));
//				masterTable.setWidget(row, 4, new Label(master + row));
			}

		}});
//		int height = m.getHeight();
//		mainPanel.setHeight(height + "px");
	}
}


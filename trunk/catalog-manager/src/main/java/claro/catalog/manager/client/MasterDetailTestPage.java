package claro.catalog.manager.client;

import java.util.Arrays;
import java.util.List;

import claro.catalog.manager.client.widgets.Table;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;

public class MasterDetailTestPage extends Page {

	private final LayoutPanel mainPanel;
	private boolean initialized;

	public MasterDetailTestPage(PlaceController placeController) {
		super(placeController);
		mainPanel = new LayoutPanel();
		initWidget(mainPanel);
	}

	@Override
	public Place getPlace() {
		return null;
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
		
		MasterDetail<String> m;
		final List<String> masterData = Arrays.asList(
				"bladibladibladibla0",
				"bladibladibladibla1",
				"bladibladibladibla2",
				"bladibladibladibla3",
				"bladibladibladibla4",
				"bladibladibladibla5",
				"bladibladibladibla6",
				"bladibladibladibla7",
				"bladibladibladibla8",
				"bladibladibladibla9"
		);
		mainPanel.add(m = new MasterDetail<String>(50, 50) {{
			final Table masterTable = getMaster();
			masterTable.resizeColumns(5);
			masterTable.resizeHeaderRows(1);
			masterTable.setHeaderText(0, 0, "col1");
			masterTable.setHeaderText(0, 1, "col2");
			masterTable.setHeaderText(0, 2, "col2");
			masterTable.setHeaderText(0, 3, "col2");
			masterTable.setHeaderText(0, 4, "col2");
			
			
			// We need a row for every master:
			masterTable.resizeRows(masterData.size());
			
			int i = 0;
			for (String master : masterData) {
				final int row = i++;
				System.out.println("Row: " + row);
				masterTable.setWidget(row, 0, new Anchor(master + row) {{
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							System.out.println("Opening Row: " + row);
							getDetail().clear();
							
							getDetail().add(new FlowPanel() {{
								Styles.add(this, Styles.masterdetailtest);
								add(new Anchor("Close") {{
									addClickHandler(new ClickHandler() {
										public void onClick(ClickEvent event) {
											System.out.println("Closing Row: " + row);
											closeDetail();
										}
									});
								}});
								add(new Label("Details for " + masterData.get(row) + "..."));
								add(new ItemDetails());
							}});

							openDetail(row);
						}
					});
				}});
				masterTable.setWidget(row, 1, new Label(master + row));
				masterTable.setWidget(row, 2, new Label(master + row));
				masterTable.setWidget(row, 3, new Label(master + row));
				masterTable.setWidget(row, 4, new Label(master + row));
			}

		}});
	}
}


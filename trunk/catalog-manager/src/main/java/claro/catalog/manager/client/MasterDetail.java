package claro.catalog.manager.client;

import claro.catalog.manager.client.widgets.Table;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.LayoutPanel;

public abstract class MasterDetail extends Composite {

	private final LayoutPanel mainPanel;
	private LayoutPanel headerPanel;
	private LayoutPanel footerPanel;

	
	private Table masterTable;
	private LayoutPanel detailPanel;
	
	private boolean initialized;
	private boolean detailInitialized;

	private boolean detailOpen;

	private LayoutPanel eraseLine;

	private LayoutPanel selectionLine;

	private int currentRow;

	private final int headerSize;

	private final int footerSize;
	private DockLayoutPanel masterPanel;

	
	
	public MasterDetail(int headerSize, int footerSize) {
		this.headerSize = headerSize;
		this.footerSize = footerSize;
		initWidget(mainPanel = new LayoutPanel() {{
			Styles.add(this, Styles.masterdetail);
		}});

	}

	protected LayoutPanel getMasterHeader() {
		initializeMainPanel();
		
		return headerPanel;
	}
	
	protected LayoutPanel getMasterFooter() {
		initializeMainPanel();

		return footerPanel; 
	}
	
	protected Table getMaster() {
		initializeMainPanel();
		
		return masterTable;
	}
	
	protected LayoutPanel getDetail() {
		initializeDetailPanel();
		
		return detailPanel;
	}
	
	public boolean getDetailOpen() {
		return detailOpen;
	}
	
	protected void openDetail(final int row) {
		
		initializeDetailPanel();
		
		// Determine size/offset of selected table row.
		int offsetTop = masterTable.getRowFormatter().getElement(row).getOffsetTop() + headerSize;
		int offsetHeight = masterTable.getRowFormatter().getElement(row).getOffsetHeight();
		int offsetLeft = masterTable.getRowFormatter().getElement(row).getOffsetLeft();
		int offsetWidth = masterTable.getRowFormatter().getElement(row).getOffsetWidth();
		
		// Set selection 
		
		// If the details is not open, position to get a nice starting position
		if (!detailOpen) {
			mainPanel.setWidgetLeftWidth(masterPanel, 0, Unit.PX, 100, Unit.PC);

			mainPanel.setWidgetTopHeight(detailPanel, offsetTop -1, Unit.PX, offsetHeight + 2, Unit.PX);
			mainPanel.setWidgetLeftWidth(detailPanel, 20, Unit.PCT, 0, Unit.PX);
			
			int detailPanelOffset = mainPanel.getElement().getOffsetWidth() / 5;
			mainPanel.setWidgetTopHeight(selectionLine, offsetTop - 1, Unit.PX, offsetHeight + 2, Unit.PX);
			mainPanel.setWidgetLeftWidth(selectionLine, detailPanelOffset, Unit.PX, 0, Unit.PX);
			
			mainPanel.setWidgetTopHeight(eraseLine, offsetTop + 1, Unit.PX, offsetHeight - 2, Unit.PX);
			mainPanel.setWidgetLeftWidth(eraseLine, 20, Unit.PCT, 2, Unit.PX);
			
			mainPanel.forceLayout();
		}
		detailOpen = true;

		// Update selection
		mainPanel.setWidgetTopHeight(selectionLine, offsetTop - 1, Unit.PX, offsetHeight + 2, Unit.PX);
		mainPanel.setWidgetLeftWidth(selectionLine, offsetLeft - 1, Unit.PX, offsetWidth + 2, Unit.PX);

		// erase a bit of the detail panel:
		mainPanel.setWidgetTopHeight(eraseLine, offsetTop + 1, Unit.PX, offsetHeight - 2, Unit.PX);
		mainPanel.setWidgetLeftWidth(eraseLine, 20, Unit.PCT, 2, Unit.PX);
		
		mainPanel.setWidgetTopHeight(detailPanel, 0, Unit.PX, 100, Unit.PCT);
		mainPanel.setWidgetLeftWidth(detailPanel, 20, Unit.PCT, 80, Unit.PCT);
		

		final int oldRow = currentRow;
		mainPanel.animate(300, new AnimationCallback() {
			public void onLayout(Layer layer, double progress) {
			}
			public void onAnimationComplete() {
				// move style to new master row
				if (oldRow != -1) {
					masterTable.getRowFormatter().removeStyleName(oldRow, Styles.masterselection.toString());
				}
				masterTable.getRowFormatter().addStyleName(row, Styles.masterselection.toString());
			}
		});
		
		currentRow = row;
	}
	
	protected void closeDetail() {
		if (!detailOpen) {
			return;
		}
		
		detailOpen = false;
		
		// animate
		// Determine size/offset of selected table row.
		final int offsetTop = masterTable.getRowFormatter().getElement(currentRow).getOffsetTop() + headerSize;
		final int offsetHeight = masterTable.getRowFormatter().getElement(currentRow).getOffsetHeight();
		int offsetLeft = masterTable.getRowFormatter().getElement(currentRow).getOffsetLeft();
		
		int mainPanelHeight = mainPanel.getOffsetHeight();
		
		// This forcelayout should not have to be necessary.

		mainPanel.setWidgetTopHeight(selectionLine, offsetTop - 1, Unit.PX, offsetHeight + 2, Unit.PX);
		mainPanel.setWidgetLeftWidth(selectionLine, offsetLeft - 1, Unit.PX, 25, Unit.PCT);
		mainPanel.setWidgetTopHeight(eraseLine, offsetTop + 1, Unit.PX, offsetHeight - 2, Unit.PX);
		mainPanel.setWidgetLeftWidth(eraseLine, 20, Unit.PCT, 2, Unit.PX);
		mainPanel.setWidgetTopHeight(detailPanel, 0, Unit.PX, mainPanelHeight, Unit.PX);
		mainPanel.setWidgetLeftWidth(detailPanel, 20, Unit.PCT, 80, Unit.PCT);
		mainPanel.forceLayout();
		
		mainPanel.setWidgetTopHeight(detailPanel, offsetTop - 1, Unit.PX, offsetHeight + 2, Unit.PX);
		mainPanel.setWidgetLeftWidth(detailPanel, 20, Unit.PCT, 0, Unit.PCT);
		
		mainPanel.setWidgetTopHeight(selectionLine, offsetTop - 1, Unit.PX, offsetHeight + 2, Unit.PX);
		mainPanel.setWidgetLeftWidth(selectionLine, 20, Unit.PCT, 0, Unit.PCT);
			

		final int row = currentRow;
		mainPanel.animate(300, new AnimationCallback() {
			public void onLayout(Layer layer, double progress) {
			}
			public void onAnimationComplete() {
				// Delay this until the animation is done, otherwise you see the erased line reappear.
				mainPanel.setWidgetTopHeight(eraseLine, offsetTop + 1, Unit.PX, offsetHeight - 2, Unit.PX);
				mainPanel.setWidgetLeftWidth(eraseLine, 20, Unit.PCT, 0, Unit.PX);
				
				// Remove style on current master.
				if (masterTable.getRowCount() > row && row >= 0) {
					masterTable.getRowFormatter().removeStyleName(row, Styles.masterselection.toString());
				}
			}
		});
		currentRow = -1;

	}
	
	private void initializeMainPanel() {
		if (initialized) {
			return;
		}
		initialized = true;
		
		mainPanel.add(masterPanel = new DockLayoutPanel(Unit.PX) {{
			addNorth(headerPanel = new LayoutPanel(), headerSize);
			addSouth(footerPanel = new LayoutPanel(), footerSize);
			add(masterTable = new Table());
		}});
		
		mainPanel.add(selectionLine = new LayoutPanel() {{
			Styles.add(this, Styles.selectionPanel);
		}});

		// Make sure the selection panel does not overlap the master
		mainPanel.setWidgetLeftWidth(selectionLine, 100, Unit.PCT, 0, Unit.PX);

	}
	
	private void initializeDetailPanel() {
		if (detailInitialized) {
			return;
		}
		detailInitialized = true;

		mainPanel.add(detailPanel = new LayoutPanel() {{
			Styles.add(this, Styles.detailPanel);
		}});
		mainPanel.add(eraseLine = new LayoutPanel() {{
			Styles.add(this, Styles.erasePanel);
		}});

		// make sure erase panel does not overlap the detail panel
		mainPanel.setWidgetLeftWidth(eraseLine, 100, Unit.PCT, 0, Unit.PX);
	}
}
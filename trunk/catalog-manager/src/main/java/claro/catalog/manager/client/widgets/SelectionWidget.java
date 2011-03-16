package claro.catalog.manager.client.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import claro.catalog.manager.client.Globals;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.util.CollectionUtil;

public abstract class SelectionWidget<T> extends Composite implements Globals {
	enum Styles implements Style { mouseOverStyle, selectionStyle, selectionDisplayName, selectionAddNoSelection, selectionAdd }
	
	// State
	private List<T> selection = new ArrayList<T>();
	private boolean multiSelect;
	private final boolean canSelect;

	// Widgets
	private FlowPanel mainPanel;
	
	public SelectionWidget(boolean canSelectSelection, boolean multiSelect) {
		this.canSelect = canSelectSelection;
		this.multiSelect = multiSelect;
		initWidget(mainPanel = new FlowPanel() {{
			StyleUtil.addStyle(this, Styles.selectionStyle);
		}});
	}

	/**
	 * Note: does *not* re-render. Only {@link #setData(Collection, String)} re-renders.
	 * @param multiSelect
	 */
	public void setMultiSelect(boolean multiSelect) {
		this.multiSelect = multiSelect;
	}
	
	public boolean isMultiSelect() {
		return multiSelect;
	}

	public void setSelection(Iterable<T> selection) {
		this.selection.clear();
		for (T selectedObject : CollectionUtil.notNull(selection)) {
			if (selectedObject != null) {
				this.selection.add(selectedObject);
			}
		}
		
		render();
	}

	public List<T> getSelection() {
		return selection;
	}
	
	/**
	 * Subclasses must override either this method or {@link #getDisplayNames(List, DisplayNamesCallback)}
	 * @param selectedObject
	 * @return
	 */
	public String displayName(T selectedObject) {
		return "" + selectedObject;
	}

	// methods that must be implemented by subclasses:
	abstract protected void addSelectionClicked(Widget addWidget);

	// Methods that can be overridden by subclasses.
	
	/**
	 * Subclasses must override either this method or {@link #displayName(T)}
	 */
	protected void getDisplayNames(List<T> selection, DisplayNamesCallback displayNamesCallback) {
		List<String> result = new ArrayList<String>();
		for (T selectedObject : selection) {
			result.add(displayName(selectedObject));
		}
		
		displayNamesCallback.displayNames(result);
	}
	
	protected interface DisplayNamesCallback {
		void displayNames(List<String> names);
	}

	protected String getAddToSelectionLabel() {
		return messages.addToSelectionLink();
	}
	
	protected String getSelectedObjectTooltip(String selectedObjectName) {
		return "";
	}
	
	protected String getRemoveSelectedObjectTooltip(String selectedObjectName) {
		return "";
	}
	
	protected String getAddSelectionTooltip() {
		return "";
	}
	
	protected void selectedObjectClicked(T categoryId) {
		
	}
	
	protected void removeObjectClicked(T categoryId) {
		removeSelectedObject(categoryId);
	}

	protected void selectionChanged() {
	}

	// methods for subclasses to invoke.
	
	protected void addSelectedObject(T object) {
		selection.add(object);
		selectionChanged();
		render();
	}
	
	protected void removeSelectedObject(T object) {
		removeSelectedObject(selection.indexOf(object));
	}
	
	protected void removeSelectedObject(int index) {
		selection.remove(index);
		selectionChanged();
		render();
	}
	

	private void render() {
		getDisplayNames(selection, new DisplayNamesCallback() {
			public void displayNames(List<String> displayNames) {
				mainPanel.clear();
				if (displayNames != null && displayNames.size() >= selection.size()) {
					int i = 0;
					for (T selectedObject : selection) {
						final boolean lastSelectedObject = i == selection.size() - 1;
						final T finalSelectedObject = selectedObject;
						final String displayName = displayNames.get(i);
						mainPanel.add(new Grid(1, lastSelectedObject ? 3 : 2) {{
							setWidget(0, 0, new Anchor(displayName) {{
								StyleUtil.addStyle(this, Styles.selectionDisplayName);
								setTitle(getSelectedObjectTooltip(displayName));
								if (canSelect) {
									addHoverStyles(this);
									addClickHandler(new ClickHandler() {
										public void onClick(ClickEvent event) {
											selectedObjectClicked(finalSelectedObject);
										}
									});
								}
							}});
							setWidget(0, 1, new Anchor("X") {{
								addHoverStyles(this);
								setTitle(getRemoveSelectedObjectTooltip(displayName));
								addClickHandler(new ClickHandler() {
									public void onClick(ClickEvent event) {
										removeSelectedObject(finalSelectedObject);
									}
								});
							}});
							if (lastSelectedObject && multiSelect) {
								setWidget(0, 2, createAddAnchor("+", false));
							}
						}});
						i++;
					}
				} else {
					mainPanel.add(new Label(messages.pleaseWait())); // TODO  Maybe different message (retrieving details....).
				}

				if (selection.isEmpty()) {
					mainPanel.add(createAddAnchor(getAddToSelectionLabel(), true));
				}
			}
		});
		
	}

	private Anchor createAddAnchor(String addSelectionText, final boolean emptySelection) {
		return new Anchor(addSelectionText) {{ // TODO Use image instead?
			if (emptySelection) {
				StyleUtil.addStyle(this, Styles.selectionAddNoSelection);
			} else {
				StyleUtil.addStyle(this, Styles.selectionAdd);
			}
			setTitle(getAddSelectionTooltip());
			addHoverStyles(this);
			addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					addSelectionClicked((Widget) event.getSource());
				}
			});
			
			// TODO add show on mouse over.
		}};
	}
	
	
	private void addHoverStyles(final Anchor anchor) {
		anchor.addMouseOverHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				StyleUtil.addStyle(anchor, Styles.mouseOverStyle);
			}
		});
		anchor.addMouseOutHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				StyleUtil.remove(anchor, Styles.mouseOverStyle);
			}
		});
	}


}

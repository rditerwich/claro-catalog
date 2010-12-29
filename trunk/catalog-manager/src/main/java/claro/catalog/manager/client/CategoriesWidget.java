package claro.catalog.manager.client;

import java.util.List;

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

import easyenterprise.lib.gwt.client.Style;
import easyenterprise.lib.gwt.client.StyleUtil;
import easyenterprise.lib.util.SMap;

public class CategoriesWidget extends Composite implements Globals {
	
	enum Styles implements Style { mouseOverStyle, categoryStyle }
	
	private FlowPanel mainPanel;

	public CategoriesWidget() {
		initWidget(mainPanel = new FlowPanel() {{
			StyleUtil.add(this, Styles.categoryStyle);
		}});
	}
	
	public void setData(SMap<Long, SMap<String, String>> categories, String language, final boolean canRemove, final boolean canAdd, final boolean canSelect) {
		
		List<Long> categoryKeys = categories.getKeys();
		mainPanel.clear();
		for (Long categoryId : categoryKeys) {
			final Long catId = categoryId;
			final String categoryName = categories.getOrEmpty(categoryId).tryGet(language, null);
			mainPanel.add(new Grid(1, canRemove ? 2 : 1) {{
				setWidget(0, 0, new Anchor(categoryName) {{
					setTitle(getCategoryTooltip(categoryName));
					if (canSelect) {
						addHoverStyles(this);
						addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								categoryClicked(catId);
							}
						});
					}
				}});
				if (canRemove) {
					setWidget(0, 1, new Anchor("X") {{
						addHoverStyles(this);
						setTitle(getRemoveCategoryTooltip(categoryName));
						addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								removeCategory(catId);
							}
						});
					}});
				}
			}});
		}
		if (canAdd) {
			mainPanel.add(new Anchor(categoryKeys.isEmpty() ? messages.addCategoriesLink() : "+") {{ // TODO Use image instead?
				addHoverStyles(this);
				// TODO add click handler
				// TODO add show on mouse over.
			}});
		}
	}
	
	protected String getCategoryTooltip(String categoryName) {
		return "";
	}
	
	protected String getRemoveCategoryTooltip(String categoryName) {
		return "";
	}
	
	protected String getAddCategoryTooltip() {
		return "";
	}
	
	protected void categoryClicked(Long categoryId) {
		
	}

	protected void removeCategory(Long categoryId) {
		
	}
	
	private void addHoverStyles(final Anchor anchor) {
		anchor.addMouseOverHandler(new MouseOverHandler() {
			public void onMouseOver(MouseOverEvent event) {
				StyleUtil.add(anchor, Styles.mouseOverStyle);
			}
		});
		anchor.addMouseOutHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				StyleUtil.remove(anchor, Styles.mouseOverStyle);
			}
		});
	}
}

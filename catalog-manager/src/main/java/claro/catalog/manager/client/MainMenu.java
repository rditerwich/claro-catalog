package claro.catalog.manager.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;

public class MainMenu extends Composite {

	private Map<String, PageWrapper> pages = new HashMap<String, MainMenu.PageWrapper>();
	private Map<Page, PageWrapper> pagesToWrappers = new HashMap<Page, MainMenu.PageWrapper>();
	
	public static PageWrapper currentPage;

	private final HasWidgets panelContainer;
	private final HorizontalPanel menuPanel;
	
	public MainMenu(HasWidgets panelContainer) {
		this.panelContainer = panelContainer;
		// history
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			public void onValueChange(ValueChangeEvent<String> event) {
				showPage(event.getValue());
			}
		});
		initWidget(menuPanel = new HorizontalPanel());
	}
	
	public void addPage(Page page, String menuText) {
		PageWrapper wrapper = new PageWrapper();
		wrapper.page = page;
		wrapper.token = menuText.replace(' ', '-').toLowerCase();
		wrapper.link = new Hyperlink("<span><span>" + menuText + "</span></span>", true, wrapper.token);
		menuPanel.add(wrapper.link);
		pages.put(wrapper.token, wrapper);
		pagesToWrappers.put(page, wrapper);
		page.setMainMenu(this);
	}
	
	public static boolean isCurrentPage(Page page) {
		return currentPage != null && currentPage.page == page;
	}
	
	protected void showPage(Page page) {
		History.newItem(pagesToWrappers.get(page).token);
	}
	
	private void showPage(String historyToken) {
		PageWrapper page = pages.get(historyToken);
		if (page != currentPage) {
			if (currentPage != null) {
				currentPage.link.removeStyleName(GlobalStylesEnum.active.toString());
//				currentPage.page.setVisible(false);
				currentPage.page.removeFromParent();
			}
			if (page != null) {
				currentPage = page;
				currentPage.link.addStyleName(GlobalStylesEnum.active.toString());
				if (!page.initialized) {
					page.initialized = true;
					page.page.initialize();
					page.page.onResize();
				}
				panelContainer.add(page.page);
//				page.page.setVisible(true);
				page.page.show();
			}
		}
	}
	
	private static class PageWrapper {
		boolean initialized;
		Page page;
		String token;
		Hyperlink link;
	}
}

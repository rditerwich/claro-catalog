package claro.catalog.manager.client.catalog;

import claro.catalog.command.items.PerformStaging;
import claro.catalog.data.ItemType;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.command.StatusCallback;
import claro.catalog.manager.client.widgets.ConfirmationDialog;
import claro.catalog.manager.client.widgets.ItemSelectionWidget;
import claro.catalog.manager.client.widgets.LanguageAndShopSelector;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.gwt.client.widgets.EEButton;
import easyenterprise.lib.gwt.client.widgets.EERibbon;
import easyenterprise.lib.gwt.client.widgets.EERibbonPanel;
import easyenterprise.lib.util.SMap;

public class CatalogRibbon extends EERibbon implements Globals {

	private final CatalogPage page;
	private final CatalogPageModel model;

	LanguageAndShopSelector languageSelection;
	ItemSelectionWidget filterCategories;
	EERibbonPanel filterRibbon;

	public CatalogRibbon(final CatalogPage page, final CatalogPageModel model) {
		this.page = page;
		this.model = model;
		add(new VerticalPanel() {{
			add(languageSelection = new LanguageAndShopSelector() {
				protected void selectionChanged() {
					model.setSelectedLanguage(getSelectedLanguage());
					model.setSelectedShop(getSelectedShop());
					page.updateProductList();
				}
			});
			add(new EEButton(messages.newProduct()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						page.createNewProduct(model.getRootCategory());
					}
				});
			}});
			add(new Anchor(messages.refresh()) {{
				addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						languageSelection.refreshData();
						page.updateProductList();
					}
				});
			}});

		}});
		add(filterRibbon = new EERibbonPanel() {{
			add(new TextBox() {{
				addChangeHandler(new ChangeHandler() {
					public void onChange(ChangeEvent event) {
						model.setFilterString(getText());
						page.updateProductList();
					}
				});
			}});
			add(filterCategories = new ItemSelectionWidget(false, ItemType.category, true) {{
				setData(model.getFilterCategories(), model.getSelectedLanguage());
			}
			protected String getAddToSelectionLabel() {
				return messages.addCategoriesLink();
			};
			protected String getAddSelectionTooltip() {
				return messages.addCategoryFilter();
			}
			protected String getRemoveSelectedObjectTooltip(String categoryName) {
				return messages.removeCategoryFilterTooltip(categoryName);
			}
			protected void removeCategory(Long categoryId) {
				super.removeCategory(categoryId);
				page.updateProductList();
			}
			protected void addCategory(Long categoryId, SMap<String, String> labels) {
				super.addCategory(categoryId, labels);
				page.updateProductList();
			}
			});
		}});
		add(new EERibbonPanel() {{
			add(new VerticalPanel() {{
				add(new Anchor(messages.publishToPreviewButton()) {{
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							new ConfirmationDialog(messages.publishToPreviewConfirmation(), images.warning()) {
								@Override
								protected void yesPressed() {
									PerformStaging command = new PerformStaging();
									command.catalogId = CatalogManager.getCurrentCatalogId();
									command.toStagingName = PerformStaging.STAGING_AREA_PREVIEW;
									GwtCommandFacade.execute(command, new StatusCallback<PerformStaging.Result>() {});
								}
							}.show();
						}
					});
				}});
				add(new Anchor(messages.publishPreviewToProductionButton()) {{
					addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							new ConfirmationDialog(messages.publishPreviewToProductionConfirmation(), images.warning()) {
								@Override
								protected void yesPressed() {
									PerformStaging command = new PerformStaging();
									command.catalogId = CatalogManager.getCurrentCatalogId();
									command.fromStagingName = PerformStaging.STAGING_AREA_PREVIEW;
									command.toStagingName = PerformStaging.STAGING_AREA_PUBLISHED;
									GwtCommandFacade.execute(command, new StatusCallback<PerformStaging.Result>() {});
								}
							}.show();
						}
					});
				}});
			}});
		}});
	}
	
	public void render() {
		languageSelection.refreshData();
	}

}

package claro.catalog.manager.client.catalog;

import claro.catalog.command.items.PerformStaging;
import claro.catalog.data.ItemType;
import claro.catalog.manager.client.CatalogManager;
import claro.catalog.manager.client.Globals;
import claro.catalog.manager.client.command.StatusCallback;
import claro.catalog.manager.client.widgets.ConfirmationDialog;
import claro.catalog.manager.client.widgets.ItemSelectionWidget;
import claro.catalog.manager.client.widgets.LanguageAndOutputChannelSelector;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.gwt.client.widgets.EEButton;
import easyenterprise.lib.gwt.client.widgets.EERibbon;
import easyenterprise.lib.gwt.client.widgets.EERibbonPanel;
import easyenterprise.lib.gwt.client.widgets.Header;
import easyenterprise.lib.util.SMap;

public class CatalogRibbon extends EERibbon implements Globals {

	private final CatalogPage page;
	private final CatalogPageModel model;

	LanguageAndOutputChannelSelector languageSelection;
	ItemSelectionWidget filterCategories;
	EERibbonPanel filterRibbon;

	public CatalogRibbon(final CatalogPage page, final CatalogPageModel model) {
		this.page = page;
		this.model = model;
		add(new EERibbonPanel() {{
		  add(new Header(1, "View"));
		  add(new VerticalPanel() {{
		    add(languageSelection = new LanguageAndOutputChannelSelector() {
		      protected void selectionChanged() {
		        model.setSelectedLanguage(getSelectedLanguage());
		        model.setSelectedOutputChannel(getSelectedOutputChannel());
		        if (model.getSelectedProductId() != null) {
		          page.updateProductSelection(model.getSelectedProductId());
		        }
		        
		        page.updateProductList();
		      }
		    });
		    add(new Anchor(messages.refresh()) {{
		      addClickHandler(new ClickHandler() {
		        public void onClick(ClickEvent event) {
		          languageSelection.refreshData();
		          page.updateProductList();
		        }
		      });
		    }});
		  }});
		}});
		add(new EERibbonPanel() {{
      add(new Header(1, "Search"));
      add(new TextBox() {{
        addChangeHandler(new ChangeHandler() {
          public void onChange(ChangeEvent event) {
            model.setFilterString(getText());
            page.updateProductList();
          }
        });
      }});
		}});
		add(filterRibbon = new EERibbonPanel() {{
		  add(new Header(1, "Filter by Category"));
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
			protected void removeItem(Long categoryId) {
				super.removeItem(categoryId);
				model.setFilterCategories(model.getFilterCategories().removeKey(categoryId));
				page.updateProductList();
			}
			protected void addItem(Long categoryId, SMap<String, String> labels) {
				super.addItem(categoryId, labels);
				model.setFilterCategories(model.getFilterCategories().add(categoryId, labels));
				page.updateProductList();
			}
			});
		}});
		add(new EERibbonPanel() {{
		  add(new Header(1, "Actions"));
		  add(new HorizontalPanel() {{
	      add(new Button(messages.newProduct()) {{
	        addClickHandler(new ClickHandler() {
	          public void onClick(ClickEvent event) {
	            page.createNewProduct(model.getRootCategory());
	          }
	        });
	      }});
  		  add(new VerticalPanel() {{
  		    setStylePrimaryName("ribbonPanelColumn");
  				add(new Button(messages.publishToPreviewButton()) {{
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
  				add(new Button(messages.publishPreviewToProductionButton()) {{
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
		}});
	}
	
	public void render() {
		languageSelection.refreshData();
	}

}

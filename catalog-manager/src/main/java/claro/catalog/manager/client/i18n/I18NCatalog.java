package claro.catalog.manager.client.i18n;

import com.google.gwt.i18n.client.Messages;

public interface I18NCatalog extends Messages {


	//	// TODO Move these to a more appropriate util.
//	@DefaultMessage("<h2>{0}</h2>") 
//	String h2(String text);
//
//	@DefaultMessage("<h3>{0}</h3>")
//	String h3(String text);
//
//	String inheritedProperties();
//
//	@DefaultMessage("Today {0} products in promotion")
//	@PluralText( { "none", "Today no promotions", "one",
//			"Today 1 product in promotion" })
//	String nrOfPromotions(@PluralCount int nrOfPromotions);
//
//	String newCategory();
//
//	String saveChanges();
//
//	String containsProducts();
//
//	String newProduct();
//
//	String noProducts();
//
//	String backToProductOverview();
//
//	String explainNavigationList();
//
//	String name();
//
//	String type();
//
//	String value();
//
//	String languageSpecificValue();
//
//	String languageSpecificName();
//
//	String categoryOnly();
//
//	String add();
//
//	String products();
//
//	String category();
//	
//	String categoryName(String categoryName);
//
//	String catalog();
//
//	String shop();
//
//	String navigation();
//
//	String promotions();
//
//	String orders();
//
//	String settings();
//
//	String todo();
//
//	String promotionSaved();
//
//	String productSaved();
//
//	String productCategorySaved(String groupName);
//
	String fileUploaded(String fileName);
//
//	String navigationSaved();
//
	String loading();
//
	String upload();
//
	String uploadFile();
//
//	String deletePromotionQuestion();
//
//	String promotionDeleted();
//
//	String deletePropertyQuestion();
//
//	String propertiesFrom(String name);
//
//	String explainLanguages();
//
//	String save();
//
//	String languagesSaved();
//
//	String deleteProductQuestion();
//
//	String productDeleted();
//
//	String explainEnums();
//
//	String cancel();
//
//	String ok();
//
//	String editEnumTitle();
//
//	String noValue();
//
//	String relatedTo();
//
//	String parents();
//
	String properties();
//
//	String propertyValues();
//
//	String orderDate();
//
//	String orderVolume();
//
//	String orderPrice();
//
//	String publish();
//
//	String publishSucess();
//
//	String explainPublish();
//
//	String orderCustomer();
//
//	String orderStatus();
//
//	String orderStatusUpdated();
//
//	String backToOrdersOverview();
//
//	String saving();
//
//	String noProperties(String groupName);
//
//	String filter();
//
//	String taxonomy();
//
//	String allProducts();
//
//	String refresh();
//
//	String explainOwnerCategory();
//
//	String orderDateDetail();
//
//	String orderCustomerDetail();
//
//	String orderStatusDetail();
//
//	String language();
//
//	String subNavigations();
//
//	String newNavigation();
//
//	String categories();
//
//	String productDetailsOf(String name);
//
//	String productsIn(String name);
//
//	String up();
//	
//	String down();
//
	String addToCategoriesLink();
	String addToProductsLink();
	String addToItemsLink();
	String addCategoriesLink();
	String addCategoryFilter();
	String addCategoryProductDetailsTooltip(String productName);
	String categoryTreeRootName();
	String clearValue();
	String containedProducts(int nr);
	String failureRetryingMessage(String action, int retryNr);
	String failureMessage(String action);
	String successMessage(String action);
	String filterMessage(String filter);
	String internalFailureRetryingMessage(int retryNr);
	String internalFailureMessage();
	String loadingCategories();
	String loadingCategoryDetails();
	String newCategory();
	String loadingProducts();
	String loadingProductDetails();
	String newProduct();
	String noProductsFound();
	String price();
	String product();
	String removeCategoryFilterTooltip(String categoryName);
	String removeCategoryProductDetailsTooltip(String categoryName);


	


	String loadingImportSourcesMessage();
	String importSourceLabel();
	String newImportSourceLink();
	String creatingImportSourceMessage();
	String importSourceNameHelp();
	String importUrlLabel();
	String importUrlHelp();
	String multiFileImportLabel();
	String multiFileImportHelp();
	String fileFormatLink();
	String dataMappingsLink();
	String importNowHelp();
	String importNowButton();
	String importFileButton();
	String incrementalImportLabel();
	String incrementalImportHelp();
	String sequentialImportNamesLabel();
	String sequentialImportNamesHelp();
	String orderedImportNamesLabel();
	String orderedImportNamesHelp();
	String relativeImportUrlLabel();
	String addNestedFileLink();
	String multiFileTab();
	String notAMultiFileImportSourceMessage();


	String name();
	String lastStatusLabel();
	String lastStatusHelp();
	String success();
	String failed();
	String notRunMessage();
	String log();
	String showLogLink();
	String addPropertyMapping();
	String propertyMappings();
	String property();
	String expression();
	String selectProperty();
	String matchProperty();
	String matchPropertyHelp();
	String importMenu();
	String jobStatus();
	String history();
	String pleaseWait();
	String refresh();
	String savingCategoryDetailsStatus();
	String savingCategoryDetailsFailedStatus();
	String savingCategoryDetailsSuccessStatus();
	String savingProductDetailsStatus();
	String savingProductDetailsFailedStatus();
	String savingProductDetailsSuccessStatus();
	String removeCategoryConfirmationMessage(String text);
	String removeCategoryLink();
	String removeProductConfirmationMessage(String text);
	String removeProductLink();
	
	String propertySourceTooltip(String propertyName, String sourceName);
	String unknownPropertySourceTooltip(String propertyName);
	String defaultValuesTab();
	String propertyGroupsTab();
	String detailsLabel();
	String addParentCategoriesLink();
	String importNowLink();
	String lastRunLabel();
	String lastRunUrlLabel();
	String statusHeader();
	String healthHeader();
	String actionsHeader();
	String nameLabel();
	String fileFormatTab();
	String selectNestedFileLabel();
	String selectNestedFileHelp();

	String catalogMenu();
	String taxonomyMenu();
	String dataExchangeMenu();
	String webshopMenu();
	String ordersMenu();
	String campaignsMenu();
	String contentLibraryMenu();
	String reportAndAnalysisMenu();
	String newChildCategory();
	String noCategoriesAvailable();

	String dataMappingTab();
	String fileFormatLabel();
	String fileFormatHelp();
	String headerLineLabel();
	String fieldSeparatorLabel();
	String charsetLabel();
	String headerLineHelp();
	String fieldSeparatorHelp();
	String charsetHelp();
	String selectCategoryLabel();
	String selectCategoryHelp();
	String removeImportSourceLink();
	String yesButton();
	String noButton();
	String removeImportSourceConfirmationMessage();
	String noProductNameSet();
	String noCategoryNameSet();
	String shopNameLabel();
	String shopUrlLabel();
	String newWebshopLink();
	String removeImportedDataMessage();
	String removeImportedDataButton();
	
	String loadingOutputChannelsMessage();
	String addLanguageLink();
	String setLanguageLink();
	String noLanguagesAvailable();
	String shopNameHelp();
	String shopUrlPrefixLabel();
	String urlPrefixHelp();
	String defaultlanguageLabel();
	String defaultLanguageHelp();
	String shopLanguagesLabel();
	String shopLanguagesHelp();
	String removeShopConfirmationMessage(String name);
	String removeShopLink();
	String loadingloadingWebShopsAction();
	String defaultLanguageName();
	String savingWebShopsAction();
	String topLevelCategoriesLabel();
	String topLevelCategoriesHelp();
	
	String orderCanceledStatus();
  String orderClosedStatus();
  String orderCompleteStatus();
  String orderInShoppingCartStatus();
  String orderOnHoldStatus();
  String orderPendingPaymentStatus();
  String orderProcessingStatus();
  String orderReceivedPaymentStatus();
  String orderShippedStatus();
  
	String orderShopNameLabel();
	String orderDateLabel();
	String orderStatusLabel();
	String loadingOrdersMessage();
	String removeOrderConfirmationMessage(String name);
	String removeOrderLink();
	String loadingOrdersAction();
	String savingOrdersAction();
	String newOrderLink();
	String orderUserLabel();
	String orderAmountPaidLabel();
	String orderProductOrderTab();
	String orderHistoryTab();
	String orderFilter();
	String publishToPreviewButton();
	String publishToPreviewConfirmation();
	String publishPreviewToProductionButton();
	String publishPreviewToProductionConfirmation();
	String promotionsTab();
	String shippingOptionsTab();
	String shippingCostsLabel();
	String shippingCostsHelp();
	String expressDeliveryChargeLabel();
	String expressDeliveryChargeHelp();
	String addPromotionLink();
	String promotionsLink(String promotionText);
	String propertyIsMany();
	String propertyIsSingle();
	String addToSelectionLink();
}

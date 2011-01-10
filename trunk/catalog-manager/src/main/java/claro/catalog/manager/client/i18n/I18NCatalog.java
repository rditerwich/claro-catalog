package claro.catalog.manager.client.i18n;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

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
	String addCategoriesLink();
	String addCategoryFilter();
	String addCategoryProductDetailsTooltip(String productName);
	String categoryTreeRootName();
	String clearValue();
	String containedProducts(int nr);
	String failureRetryingMessage(String action, int retryNr);
	String failureMessage(String action);
	String filterMessage(String filter);
	String internalFailureRetryingMessage(int retryNr);
	String internalFailureMessage();
	String loadingProducts();
	String loadingProductDetails();
	String newProduct();
	String noProductsFound();
	String price();
	String product();
	String removeCategoryFilterTooltip(String categoryName);
	String removeCategoryProductDetailsTooltip(String categoryName);


	


	String loadingImportSources();
	String importSource();
	String newImportSource();
	String creatingImportSource();
	
	String name();
	String lastStatus();
	String success();
	String failed();
	String notRun();
	String log();
	String importUrl();
	String showLog();
	SafeHtml addPropertyMapping();
	String propertyMappings();
	String property();
	String expression();
	String selectProperty();
	String importUrlHelp();
	String matchProperty();
	String matchPropertyHelp();
	String importMenu();


}

package agilexs.catalogxsadmin.presentation.client.i18n;

import com.google.gwt.i18n.client.Messages;

public interface I18NCatalogXS extends Messages {

  @DefaultMessage("<h2>{0}</h2>")
  String h2(String text);

  @DefaultMessage("<h3>{0}</h3>")
  String h3(String text);

  String inheritedProperties();

  @DefaultMessage("Today {0} products in promotion")
  @PluralText({"none", "Today no promotions",
    "one", "Today 1 product in promotion"})
  String nrOfPromotions(@PluralCount int nrOfPromotions);

  String newGroup();

  String saveChanges();

  String containsProducts();

  String newProduct();

  String noProducts();

  String backToProductOverview();

  String explainNavigationList();

  String name();

  String type();

  String value();

  String languageSpecificValue();

  String languageSpecificName();

  String groupOnly();

  String add();

  String products();

  String group();

  String catalog();

  String navigation();

  String promotions();
  
  String orders();

  String settings();

  String todo();

  String promotionSaved();

  String productSaved();

  String productGroupSaved(String groupName);

  String fileUploaded(String fileName);

  String navigationSaved();

  String loading();

  String upload();

  String uploadFile();

  String deletePromotionQuestion();

  String promotionDeleted();

  String deletePropertyQuestion();
  
  String propertiesFrom(String name);
  
  String explainLanguages();

  String save();

  String languagesSaved();

  String deleteProductQuestion();

  String productDeleted();

  String explainEnums();

  String cancel();

  String ok();

  String editEnumTitle();

  String noValue();

  String relatedTo();

  String parents();

  String properties();

  String propertyValues();

  String orderDate();

  String orderVolume();

  String orderPrice();

  String publish();

  String publishSucess();
  
  String explainPublish();

  String orderCustomer();
  
  String orderStatus();

  String orderStatusUpdated();

  String backToOrdersOverview();
  
  String saving();
  
  String noProperties(String groupName);

  String filter();

  String taxonomy();

  String allProducts();

  String refresh();

  String explainOwnerGroup();

  String orderDateDetail();

  String orderCustomerDetail();

  String orderStatusDetail();
}
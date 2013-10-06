package claro.catalog.model.test.util;

import claro.catalog.CatalogDao;
import claro.catalog.model.PropertyModel;
import claro.jpa.catalog.PropertyType;

public class TestCatalogModel extends claro.catalog.model.CatalogModel {
  public static final String VARIANT = "Variant";
  public static final String ARTICLENUMBER = "Article Number";
  public static final String DESCRIPTION = "Description";
  public static final String SMALLIMAGE = "Small Image";
  public static final String PRICE = "Price";
  public static final String SUPPLIER = "Supplier";
  public static final String SUPPLIER_ARTICLENUMBER = "Supplier Article Number";
  public static final String MANUAL = "Manual";

  public final PropertyModel variantProperty;
  public final PropertyModel articleNumberProperty;
  public final PropertyModel descriptionProperty;
  public final PropertyModel smallImageProperty;
  public final PropertyModel priceProperty;
  public final PropertyModel supplierProperty;
  public final PropertyModel supplierArticleNumberProperty;

  public TestCatalogModel(Long id, CatalogDao dao) {
    super(id, dao);
    this.variantProperty = root.findOrCreateProperty(VARIANT, null, PropertyType.String, generalPropertyGroup);
    this.articleNumberProperty = root.findOrCreateProperty(ARTICLENUMBER, null, PropertyType.String, generalPropertyGroup);
    this.descriptionProperty = root.findOrCreateProperty(DESCRIPTION, null, PropertyType.String, generalPropertyGroup);
    this.smallImageProperty = root.findOrCreateProperty(SMALLIMAGE, null, PropertyType.Media, imagesPropertyGroup);
    this.priceProperty = root.findOrCreateProperty(PRICE, null, PropertyType.Money, generalPropertyGroup);
    this.supplierProperty = root.findOrCreateProperty(SUPPLIER, null, PropertyType.String, generalPropertyGroup);
    this.supplierArticleNumberProperty = root.findOrCreateProperty(SUPPLIER_ARTICLENUMBER, null, PropertyType.String, generalPropertyGroup);
    this.manualProperty = root.findOrCreateProperty(MANUAL, null, PropertyType.Media, documentsPropertyGroup);
  }

}

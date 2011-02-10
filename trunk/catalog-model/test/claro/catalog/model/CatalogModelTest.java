package claro.catalog.model;

import static claro.catalog.model.CatalogModelTestUtil.addCategory;
import static claro.catalog.model.CatalogModelTestUtil.addShop;

import java.io.IOException;
import java.sql.SQLException;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.junit.Test;

import claro.catalog.model.test.util.CatalogTestBase;
import claro.jpa.catalog.Category;
import claro.jpa.shop.Shop;
import easyenterprise.lib.util.SMap;

public class CatalogModelTest extends CatalogTestBase {
	@Test
	public void test() throws IOException, SQLException {
//		DBScript script = new DBScript("DROP SCHEMA catalog CASCADE");
//		script.execute(getConnection());
//		script = new DBScript(getClass().getResourceAsStream("/CreateSchema.sql"));
//		script.execute(getConnection()).assertSuccess();
//		EntityManager em = getEntityManager();
//		CatalogDao dao = getCatalogDao();
		CatalogModel model = getCatalogModel();

	}
	
	
	// TODO Add test to invalidate item without childextent.
	
	
	@Test
	public void testEffectiveValues() throws Exception {
		ensureDatabaseCreated();

		EntityManager entityManager = getCatalogDao().getEntityManager();
		CatalogModel model = getCatalogModel();

		Shop shop = addShop(entityManager, model, "Webshop");
		
		Category root = model.catalog.getRoot();

		Category printers = addCategory(entityManager, model, root, "Printers");
		
		ItemModel printerModel = model.getItem(printers.getId());

		PropertyModel visibleProperty = printerModel.findProperty(model.visibleProperty.getEntity(), true);
		Assert.assertNotNull(visibleProperty);
		
		SMap<String,Object> effectiveValues = visibleProperty.getEffectiveValues(null, null);
		Object defaultLanguageValue = effectiveValues.get(null);
		Assert.assertTrue((Boolean) defaultLanguageValue);

		// It should also be visible for shop:
		effectiveValues = visibleProperty.getEffectiveValues(null, shop);
		defaultLanguageValue = effectiveValues.get(null);
		Assert.assertTrue((Boolean) defaultLanguageValue);
	}
	
	@Test
	public void testEffectiveValuesOutputChannel() throws Exception {
		ensureDatabaseCreated();
		
		EntityManager entityManager = getCatalogDao().getEntityManager();
		CatalogModel model = getCatalogModel();

		Shop shop = addShop(entityManager, model, "Webshop");
		
		Category root = model.catalog.getRoot();
		Category hpPrinters = addCategory(entityManager, model, root, "HP Printers");
		Category hpColorPrinters = addCategory(entityManager, model, hpPrinters, "HP Color Printers");
		
		ItemModel hpPrinterModel = model.getItem(hpPrinters.getId());
		
		PropertyModel supplierProperty = hpPrinterModel.findProperty(model.supplierProperty.getEntity(), true);
		Assert.assertNotNull(supplierProperty);

		supplierProperty.setValue(null, null, shop, null, "HP");

		// Look on hpColorPrinter model
		ItemModel hpColorPrinterModel = model.getItem(hpColorPrinters.getId());
		
		supplierProperty = hpColorPrinterModel.findProperty(model.supplierProperty.getEntity(), true);
		Assert.assertNotNull(supplierProperty);
		
		// It should only be visible for this shop:
		SMap<String,Object> effectiveValues = supplierProperty.getEffectiveValues(null, null);
		Object defaultLanguageValue = effectiveValues.get(null);
		Assert.assertNull(defaultLanguageValue);

		// Do we have it?
		effectiveValues = supplierProperty.getEffectiveValues(null, shop);
		defaultLanguageValue = effectiveValues.get(null);
		Assert.assertEquals("HP", defaultLanguageValue);
	}
	
	@Test
	public void testEffectiveValuesOverrideForOutputChannel() throws Exception {
		ensureDatabaseCreated();
		
		EntityManager entityManager = getCatalogDao().getEntityManager();
		CatalogModel model = getCatalogModel();

		// Test data setup
		Shop shop = addShop(entityManager, model, "Webshop");
		Category root = model.catalog.getRoot();
		Category hpPrinters = addCategory(entityManager, model, root, "HP Printers");
		Category hpColorPrinters = addCategory(entityManager, model, hpPrinters, "HP Color Printers");
		
		// values to set.
		String nonShopValueToSet = "HP";
		String shopValueToSet = "HP For Shop";
		
		// Make sure the values are not set (AND: test the case where the data has already been accessed in the model
		ItemModel hpColorPrinterModelBefore = model.getItem(hpColorPrinters.getId());
		PropertyModel supplierPropertyBefore = hpColorPrinterModelBefore.findProperty(model.supplierProperty.getEntity(), true);
		Assert.assertNotNull(supplierPropertyBefore);
		
		// Check for null shop
		SMap<String,Object> effectiveValues = supplierPropertyBefore.getEffectiveValues(null, null);
		Object defaultLanguageValue = effectiveValues.get(null);
		Assert.assertNotSame(nonShopValueToSet, defaultLanguageValue);
		
		// Check for the shop
		effectiveValues = supplierPropertyBefore.getEffectiveValues(null, shop);
		defaultLanguageValue = effectiveValues.get(null);
		Assert.assertNotSame(shopValueToSet, defaultLanguageValue);

		// Make the changes.
		ItemModel hpPrinterModel = model.getItem(hpPrinters.getId());
		PropertyModel supplierProperty = hpPrinterModel.findProperty(model.supplierProperty.getEntity(), true);
		Assert.assertNotNull(supplierProperty);
		
		supplierProperty.setValue(null, null, null, null, nonShopValueToSet);
		supplierProperty.setValue(null, null, shop, null, shopValueToSet);
		
		
		// Look on hpColorPrinter model
		ItemModel hpColorPrinterModelAfter = model.getItem(hpColorPrinters.getId());
		
		PropertyModel supplierPropertyAfter = hpColorPrinterModelAfter.findProperty(model.supplierProperty.getEntity(), true);
		Assert.assertNotNull(supplierPropertyAfter);
		
		// It should only be visible for this shop:
		SMap<String,Object> effectiveValuesAfter = supplierPropertyAfter.getEffectiveValues(null, null);
		Object defaultLanguageValueAfter = effectiveValuesAfter.get(null);
		Assert.assertEquals(nonShopValueToSet, defaultLanguageValueAfter);
		
		// Do we have it?
		effectiveValuesAfter = supplierPropertyAfter.getEffectiveValues(null, shop);
		defaultLanguageValueAfter = effectiveValuesAfter.get(null);
		Assert.assertEquals(shopValueToSet, defaultLanguageValueAfter);
	}
}

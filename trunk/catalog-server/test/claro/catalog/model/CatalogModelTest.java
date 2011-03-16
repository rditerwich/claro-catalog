package claro.catalog.model;

import static claro.catalog.model.CatalogModelTestUtil.addCategory;
import static claro.catalog.model.CatalogModelTestUtil.addShop;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.junit.Test;

import claro.catalog.model.test.util.CatalogTestBase;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.Item;
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
	
	
	@Test
	public void testParentExtentWithRemoveItemUntouched() throws Exception {
		ensureDatabaseCreated();
		
		EntityManager entityManager = getCatalogDao().getEntityManager();
		CatalogModel model = getCatalogModel();

		Category root = model.catalog.getRoot();
		Category hpPrinters = addCategory(entityManager, model, root, "HP Printers");
		Category hpColorPrinters = addCategory(entityManager, model, hpPrinters, "HP Color Printers");
		Category hpColorPlotters = addCategory(entityManager, model, hpColorPrinters, "HP Color Plotters");
		
		ItemModel hpColorPrinterModel = model.getItem(hpColorPrinters.getId());
		
		model.removeItem(hpColorPrinterModel.itemId);
		
		model.flush();

		ItemModel hpColorPlottersModel = model.getItem(hpColorPlotters.getId());

		// check model:
		Assert.assertNull(model.items.get(hpColorPrinters.getId()));
		
		// Check parent.
		Set<ItemModel> parents = hpColorPlottersModel.getParents();
		Assert.assertEquals(1, parents.size());
		Assert.assertEquals(hpPrinters, parents.iterator().next().getEntity());

		// Check properties
		hpColorPlottersModel.getPropertyExtent();
		PropertyModel supplierProperty = hpColorPlottersModel.findProperty(model.supplierProperty.getEntity(), true);
		Assert.assertNotNull(supplierProperty);
		
	}
	
	@Test
	public void testParentExtentWithRemoveItemTouched() throws Exception {
		ensureDatabaseCreated();
		
		EntityManager entityManager = getCatalogDao().getEntityManager();
		CatalogModel model = getCatalogModel();
		
		Category root = model.catalog.getRoot();
		Category hpPrinters = addCategory(entityManager, model, root, "HP Printers");
		Category hpColorPrinters = addCategory(entityManager, model, hpPrinters, "HP Color Printers");
		Category hpColorPlotters = addCategory(entityManager, model, hpColorPrinters, "HP Color Plotters");
		
		ItemModel hpColorPrinterModel = model.getItem(hpColorPrinters.getId());
		
		// Touch item, trigger parent extent etc.
		hpColorPrinterModel.getPropertyExtent();
		
		model.removeItem(hpColorPrinterModel.itemId);
		
		model.flush();
		
		ItemModel hpColorPlottersModel = model.getItem(hpColorPlotters.getId());
		hpColorPlottersModel.getPropertyExtent();

		// check model:
		Assert.assertNull(model.items.get(hpColorPrinters.getId()));
		
		// Check parent.
		Set<ItemModel> parents = hpColorPlottersModel.getParents();
		Assert.assertEquals(1, parents.size());
		Assert.assertEquals(hpPrinters, parents.iterator().next().getEntity());

		// Check properties
		hpColorPlottersModel.getPropertyExtent();
		PropertyModel supplierProperty = hpColorPlottersModel.findProperty(model.supplierProperty.getEntity(), true);
		Assert.assertNotNull(supplierProperty);
	}
	
	@Test
	public void testParentExtentWithRemoveItemBelowTouched() throws Exception {
		ensureDatabaseCreated();
		
		EntityManager entityManager = getCatalogDao().getEntityManager();
		CatalogModel model = getCatalogModel();
		
		Category root = model.catalog.getRoot();
		Category hpPrinters = addCategory(entityManager, model, root, "HP Printers");
		Category hpColorPrinters = addCategory(entityManager, model, hpPrinters, "HP Color Printers");
		Category hpColorPlotters = addCategory(entityManager, model, hpColorPrinters, "HP Color Plotters");
		
		ItemModel hpColorPrinterModel = model.getItem(hpColorPrinters.getId());
		ItemModel hpColorPlotterModelBefore = model.getItem(hpColorPlotters.getId());
		
		// Touch child.
		hpColorPlotterModelBefore.getPropertyExtent();
		
		model.removeItem(hpColorPrinterModel.itemId);
		
		// Push changes through the model.
		model.flush();
		
		ItemModel hpColorPlottersModel = model.getItem(hpColorPlotters.getId());
		hpColorPlottersModel.getPropertyExtent();
		
		// check model:
		Assert.assertNull(model.items.get(hpColorPrinters.getId()));
		
		// Check parent.
		Set<ItemModel> parents = hpColorPlottersModel.getParents();
		Assert.assertEquals(1, parents.size());
		Assert.assertEquals(hpPrinters, parents.iterator().next().getEntity());
		
		// Check properties
		hpColorPlottersModel.getPropertyExtent();
		PropertyModel supplierProperty = hpColorPlottersModel.findProperty(model.supplierProperty.getEntity(), true);
		Assert.assertNotNull(supplierProperty);
	}
	
	
	@Test
	public void testParentChildExtentAfterChildSetParent() {
		// get childextent of parent
		// change a child to not include parent as a parent.
		// See whether the child is removed from the parent.
		
	}
	
	
	@Test
	public void testCategoryCycleSet() throws Exception {
		ensureDatabaseCreated();
		
		EntityManager entityManager = getCatalogDao().getEntityManager();
		CatalogModel model = getCatalogModel();
		
		Category root = model.catalog.getRoot();
		Category hpPrinters = addCategory(entityManager, model, root, "HP Printers");
		Category hpColorPrinters = addCategory(entityManager, model, hpPrinters, "HP Color Printers");
		Category hpColorPlotters = addCategory(entityManager, model, hpColorPrinters, "HP Color Plotters");
		
		ItemModel hpPrintersModel = model.getItem(hpPrinters.getId());
		ItemModel hpColorPlottersModel = model.getItem(hpColorPlotters.getId());
		
		// Try to set child as parent.
		Set<ItemModel> parentsBefore = hpPrintersModel.getParents();
		hpPrintersModel.setParents(Collections.singletonList(hpColorPlottersModel));
		Set<ItemModel> parentsAfter = hpPrintersModel.getParents();
		// TODO Make the setter throw an exception??
//		Assert.assertEquals(parentsBefore.size(), parentsAfter.size());
//		Assert.assertEquals(parentsBefore.iterator().next(), parentsAfter.iterator().next());
	}
	
	@Test
	public void testCategoryCycleGet() throws Exception {
		ensureDatabaseCreated();
		
		EntityManager entityManager = getCatalogDao().getEntityManager();
		CatalogModel model = getCatalogModel();
		
		Category root = model.catalog.getRoot();
		
		Category hpPrinters = addCategory(entityManager, model, root, "HP Printers");
		Category hpColorPrinters = addCategory(entityManager, model, hpPrinters, "HP Color Printers");
		Category hpColorPlotters = addCategory(entityManager, model, hpColorPrinters, "HP Color Plotters");
		
		// Create a cycle in the categories:
		model.dao.setItemParents(hpPrinters, Collections.singletonList((Item)hpColorPrinters));
		
		// Try to detect the cycle:
		ItemModel hpPrintersModel = model.getItem(hpPrinters.getId());
		// TODO not sure what the result should be (impl dependent).
		
		ItemModel hpColorPlottersModel = model.getItem(hpColorPlotters.getId());
		// TODO not sure what the result should be (impl dependent).
	}
	
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
		
		model.flush();

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
		
		model.flush();
		
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

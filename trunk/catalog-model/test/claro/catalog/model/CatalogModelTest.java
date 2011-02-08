package claro.catalog.model;

import static claro.catalog.model.CatalogModelTestUtil.addCategory;

import java.io.IOException;
import java.sql.SQLException;

import javax.persistence.EntityManager;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import claro.catalog.model.test.util.CatalogTestBase;
import claro.jpa.catalog.Category;
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

		Category root = model.catalog.getRoot();

		Category printers = addCategory(entityManager, model, root, "Printers");
		
		ItemModel printerModel = model.getItem(printers.getId());

		PropertyModel visibleProperty = printerModel.findProperty(model.visibleProperty.getEntity(), true);
		Assert.assertNotNull(visibleProperty);
		
		SMap<String,Object> effectiveValues = visibleProperty.getEffectiveValues(null, null);
		Object defaultLanguageValue = effectiveValues.get(null);
		Assert.assertTrue((Boolean) defaultLanguageValue);
	}
}

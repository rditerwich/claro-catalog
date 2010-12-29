package claro.catalog.model;

import static claro.catalog.model.PropertyModel.setTypedValue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import claro.catalog.CatalogDao;
import claro.catalog.model.test.util.CatalogTestBase;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.ParentChild;
import claro.jpa.catalog.Product;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyValue;
import claro.jpa.catalog.StagingArea;

import com.google.common.base.Objects;

import easyenterprise.lib.util.Money;
import easyenterprise.lib.util.Paging;
import easyenterprise.lib.util.Tuple;

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
}

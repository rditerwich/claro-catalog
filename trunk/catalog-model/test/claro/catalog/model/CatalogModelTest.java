package claro.catalog.model;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import claro.catalog.model.test.util.CatalogTestBase;

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
}

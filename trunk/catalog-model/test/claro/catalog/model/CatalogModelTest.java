package claro.catalog.model;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import claro.catalog.model.test.util.CatalogTestBase;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.ParentChild;
import claro.jpa.catalog.Product;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyType;
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
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testFindItems() throws Exception {
		CatalogDao dao = getCatalogDao();
		CatalogModel model = getCatalogModel();

		// First create some categories and items
		Category root = model.catalog.getRoot();
		Category printers = addCategory(root, "Printers");
		Category ink = addCategory(root, "Ink");
		
		addProduct(printers, "HP Deskjet 540 B&W", new Tuple[] { 
			Tuple.create(model.articleNumberProperty, "ART123123123"),
			Tuple.create(model.variantProperty, "HP Deskjet 540 Color"),
			Tuple.create(model.priceProperty, "300"),
		});
		
		addProduct(printers, "HP Deskjet 888 ", new Tuple[] { 
				Tuple.create(model.articleNumberProperty, "ART123123123"),
				Tuple.create(model.variantProperty, "HP Deskjet 888 Color"),
				Tuple.create(model.priceProperty, "300"),
		});
		
		addProduct(printers, "Canon Bla", new Tuple[] { 
				Tuple.create(model.articleNumberProperty, "ART111222333"),
				Tuple.create(model.variantProperty, "Canon Bla2"),
				Tuple.create(model.priceProperty, "200"),
		});
		
		addProduct(ink, "HP CYM ", new Tuple[] { 
				Tuple.create(model.articleNumberProperty, "ART123123111"),
				Tuple.create(model.variantProperty, "HP CYM XL"),
				Tuple.create(model.priceProperty, "60"),
		});
		
		addProduct(ink, "Canon Cyan", new Tuple[] { 
				Tuple.create(model.articleNumberProperty, "ART111222444"),
				Tuple.create(model.variantProperty, "Canon Cyan XL"),
				Tuple.create(model.priceProperty, "20"),
		});
		
		addProduct(ink, "Canon Magenta", new Tuple[] { 
				Tuple.create(model.articleNumberProperty, "ART111222555"),
				Tuple.create(model.variantProperty, "Canon Megenta XL"),
				Tuple.create(model.priceProperty, "20"),
		});
		
		getEntityManager().flush();
		
		List<ItemModel> items = model.findItems(null, null, null, null, "Deskjet", Paging.NO_PAGING);
		System.out.println("Items:");
		for (ItemModel item : items) {
			System.out.println("  Item: " + item.getItemId());
		}

		items = model.findItems(null, null, null, null, "arti:123", Paging.NO_PAGING);
		System.out.println("Items:");
		for (ItemModel item : items) {
			System.out.println("  Item: " + item.getItemId());
		}
	}
	
	private Category addCategory(Item parent, String name) throws SQLException {
		Category c = new Category();
		c.setCatalog(parent.getCatalog());
		addChild(parent, c);
		
		getCatalogDao().setPropertyValue(null, null, c, getCatalogModel().nameProperty, null, name);
		getEntityManager().persist(c);
		
		return c;
	}
	
	private Product addProduct(Item parent, String name, Tuple<Property, Object>... properties) throws SQLException {
		Product p = new Product();
		p.setCatalog(parent.getCatalog());
		addChild(parent, p);
		
		CatalogDao dao = getCatalogDao();
		dao.setPropertyValue(null, null, p, getCatalogModel().nameProperty, null, name);
		for (Tuple<Property, Object> property : properties) {
			dao.setPropertyValue(null, null, p, property.getFirst(), null, property.getSecond());
		}
		
		getEntityManager().persist(p);
		
		return p;
	}
	
	private void addChild(Item item, Item newChild) throws SQLException {
		ParentChild pc = new ParentChild();
		item.getChildren().add(pc);
		newChild.getParents().add(pc);
		
		pc.setParent(item);
		pc.setChild(newChild);
		pc.setIndex(-1);

		getEntityManager().persist(pc);
	}
}

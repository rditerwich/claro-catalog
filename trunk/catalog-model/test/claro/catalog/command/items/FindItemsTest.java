package claro.catalog.command.items;

import static claro.catalog.model.CatalogModelTestUtil.addCategory;
import static claro.catalog.model.CatalogModelTestUtil.addProduct;

import java.sql.SQLException;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;

import claro.catalog.model.CatalogModel;
import claro.catalog.model.ItemModel;
import claro.catalog.model.test.util.CatalogTestBase;
import claro.jpa.catalog.Category;
import easyenterprise.lib.util.Money;
import easyenterprise.lib.util.Tuple;

public class FindItemsTest extends CatalogTestBase {
	
	@Test
	public void testFindItemsSimple() throws Exception {
		
		FindItems cmd = new FindItems();
		cmd.catalogId = TEST_CATALOG_ID;
		cmd.filter = "Deskjet";
		cmd.resultType = ItemType.product;
		
		FindItems.Result result = executeCommand(cmd);

		CatalogModel model = getCatalogModel();
		System.out.println("Items:");
		for (Long itemId: result.items.getKeys()) {
			ItemModel item = model.getItem(itemId);
			System.out.println("  Item: " + item.getItemId());
		}
	}

	@Test
	public void testFindItemsProperty() throws Exception {
		FindItems cmd = new FindItems();
		cmd.catalogId = TEST_CATALOG_ID;
		cmd.filter = "arti:123";
		cmd.resultType = ItemType.product;
		
		FindItems.Result result = executeCommand(cmd);
		
		CatalogModel model = getCatalogModel();
		System.out.println("Items:");
		for (Long itemId: result.items.getKeys()) {
			ItemModel item = model.getItem(itemId);
			System.out.println("  Item: " + item.getItemId());
		}
	}
	
	@Before
	@SuppressWarnings("unchecked")
	public void setupItems() throws Exception {
		ensureDatabaseCreated();
		getCatalogDao().startTransaction();
		try {
			CatalogModel model = getCatalogModel();
			EntityManager entityManager = getEntityManager();
			
			// First create some categories and items
			Category root = model.catalog.getRoot();
			Category printers = addCategory(entityManager, model, root, "Printers");
			Category ink = addCategory(entityManager, model, root, "Ink");
			
			addProduct(entityManager, model, printers, "HP Deskjet 540 B&W", new Tuple[] { 
					Tuple.create(model.articleNumberProperty, "ART123123123"),
					Tuple.create(model.variantProperty, "HP Deskjet 540 Color"),
					Tuple.create(model.priceProperty, new Money(300.00, "EUR")),
			});
			
			addProduct(entityManager, model, printers, "HP Deskjet 888 ", new Tuple[] { 
					Tuple.create(model.articleNumberProperty, "ART123123123"),
					Tuple.create(model.variantProperty, "HP Deskjet 888 Color"),
					Tuple.create(model.priceProperty, new Money(300.00, "EUR")),
			});
			
			addProduct(entityManager, model, printers, "Canon Bla", new Tuple[] { 
					Tuple.create(model.articleNumberProperty, "ART111222333"),
					Tuple.create(model.variantProperty, "Canon Bla2"),
					Tuple.create(model.priceProperty, new Money(200.00, "EUR")),
			});
			
			addProduct(entityManager, model, ink, "HP CYM ", new Tuple[] { 
					Tuple.create(model.articleNumberProperty, "ART123123111"),
					Tuple.create(model.variantProperty, "HP CYM XL"),
					Tuple.create(model.priceProperty, new Money(60.00, "EUR")),
			});
			
			addProduct(entityManager, model, ink, "Canon Cyan", new Tuple[] { 
					Tuple.create(model.articleNumberProperty, "ART111222444"),
					Tuple.create(model.variantProperty, "Canon Cyan XL"),
					Tuple.create(model.priceProperty, new Money(20.00, "EUR")),
			});
			
			addProduct(entityManager, model, ink, "Canon Magenta", new Tuple[] { 
					Tuple.create(model.articleNumberProperty, "ART111222555"),
					Tuple.create(model.variantProperty, "Canon Megenta XL"),
					Tuple.create(model.priceProperty, new Money(20.00, "EUR")),
			});
	
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			getCatalogDao().commitTransaction();
		}
	}
}

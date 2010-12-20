package claro.catalog.model;

import static claro.catalog.model.PropertyModel.setTypedValue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import claro.catalog.CatalogDao;
import claro.catalog.data.MoneyValue;
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
			Tuple.create(model.priceProperty, new MoneyValue(300.00, "EUR")),
		});
		
		addProduct(printers, "HP Deskjet 888 ", new Tuple[] { 
				Tuple.create(model.articleNumberProperty, "ART123123123"),
				Tuple.create(model.variantProperty, "HP Deskjet 888 Color"),
				Tuple.create(model.priceProperty, new MoneyValue(300.00, "EUR")),
		});
		
		addProduct(printers, "Canon Bla", new Tuple[] { 
				Tuple.create(model.articleNumberProperty, "ART111222333"),
				Tuple.create(model.variantProperty, "Canon Bla2"),
				Tuple.create(model.priceProperty, new MoneyValue(200.00, "EUR")),
		});
		
		addProduct(ink, "HP CYM ", new Tuple[] { 
				Tuple.create(model.articleNumberProperty, "ART123123111"),
				Tuple.create(model.variantProperty, "HP CYM XL"),
				Tuple.create(model.priceProperty, new MoneyValue(60.00, "EUR")),
		});
		
		addProduct(ink, "Canon Cyan", new Tuple[] { 
				Tuple.create(model.articleNumberProperty, "ART111222444"),
				Tuple.create(model.variantProperty, "Canon Cyan XL"),
				Tuple.create(model.priceProperty, new MoneyValue(20.00, "EUR")),
		});
		
		addProduct(ink, "Canon Magenta", new Tuple[] { 
				Tuple.create(model.articleNumberProperty, "ART111222555"),
				Tuple.create(model.variantProperty, "Canon Megenta XL"),
				Tuple.create(model.priceProperty, new MoneyValue(20.00, "EUR")),
		});
		
		getEntityManager().flush();
		
		List<ItemModel> items = model.findItems(null, null, null, null, Collections.<Category>emptyList(), "Deskjet", Product.class, Collections.<Property>emptyList(), Paging.NO_PAGING);
		System.out.println("Items:");
		for (ItemModel item : items) {
			System.out.println("  Item: " + item.getItemId());
		}

		items = model.findItems(null, null, null, null, Collections.<Category>emptyList(), "arti:123", Product.class, Collections.<Property>emptyList(), Paging.NO_PAGING);
		System.out.println("Items:");
		for (ItemModel item : items) {
			System.out.println("  Item: " + item.getItemId());
		}
	}
	
	// TODO Add test to invalidate item without childextent.
	
	private Category addCategory(Item parent, String name) throws SQLException {
		Category c = new Category();
		c.setCatalog(parent.getCatalog());
		addChild(parent, c);
		
		
		setPropertyValue(null, null, c, getCatalogModel().nameProperty.getEntity(), null, name);
		getEntityManager().persist(c);
		
		return c;
	}
	
	private Product addProduct(Item parent, String name, Tuple<PropertyModel, Object>... properties) throws SQLException {
		Product p = new Product();
		p.setCatalog(parent.getCatalog());
		addChild(parent, p);
		
		setPropertyValue(null, null, p, getCatalogModel().nameProperty.getEntity(), null, name);
		for (Tuple<PropertyModel, Object> property : properties) {
			setPropertyValue(null, null, p, property.getFirst().getEntity(), null, property.getSecond());
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
	
	private void setPropertyValue(StagingArea stagingArea, OutputChannel outputChannel, Item item, Property property, String language, Object value) throws SQLException {
		for (PropertyValue propertyValue : item.getPropertyValues()) {
			if (propertyValue.getProperty().equals(property) 
			&& Objects.equal(propertyValue.getStagingArea(), stagingArea)
			&& Objects.equal(propertyValue.getOutputChannel(), outputChannel)
			&& Objects.equal(propertyValue.getLanguage(), language)) {
				setTypedValue(propertyValue, value);
				return;
			}
		}

		// No candidate found, create a new one
		PropertyValue newPropertyValue = new PropertyValue();
		item.getPropertyValues().add(newPropertyValue);
		newPropertyValue.setItem(item);
		
		newPropertyValue.setProperty(property);
		newPropertyValue.setStagingArea(stagingArea);
		newPropertyValue.setOutputChannel(outputChannel);
		setTypedValue(newPropertyValue, value);
		
		getEntityManager().persist(newPropertyValue);
	}
}

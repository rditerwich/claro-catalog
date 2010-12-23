package claro.catalog.model;

import static claro.catalog.model.PropertyModel.setTypedValue;

import java.sql.SQLException;

import javax.persistence.EntityManager;

import claro.jpa.catalog.Category;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.ParentChild;
import claro.jpa.catalog.Product;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyValue;
import claro.jpa.catalog.StagingArea;

import com.google.common.base.Objects;

import easyenterprise.lib.util.Tuple;

public class CatalogModelTestUtil {
	
	public static Category addCategory(EntityManager entityManager, CatalogModel catalogModel, Item parent, String name) throws SQLException {
		Category c = new Category();
		c.setCatalog(parent.getCatalog());
		addChild(entityManager, parent, c);
		
		
		setPropertyValue(entityManager, null, null, c, catalogModel.nameProperty.getEntity(), null, name);
		entityManager.persist(c);
		
		return c;
	}
	
	public static Product addProduct(EntityManager entityManager, CatalogModel catalogModel, Item parent, String name, Tuple<PropertyModel, Object>... properties) throws SQLException {
		Product p = new Product();
		p.setCatalog(parent.getCatalog());
		addChild(entityManager, parent, p);
		
		setPropertyValue(entityManager, null, null, p, catalogModel.nameProperty.getEntity(), null, name);
		for (Tuple<PropertyModel, Object> property : properties) {
			setPropertyValue(entityManager, null, null, p, property.getFirst().getEntity(), null, property.getSecond());
		}
		
		entityManager.persist(p);
		
		return p;
	}
	
	public static void addChild(EntityManager entityManager, Item item, Item newChild) throws SQLException {
		ParentChild pc = new ParentChild();
		item.getChildren().add(pc);
		newChild.getParents().add(pc);
		
		pc.setParent(item);
		pc.setChild(newChild);
		pc.setIndex(-1);

		entityManager.persist(pc);
	}
	
	public static void setPropertyValue(EntityManager entityManager, StagingArea stagingArea, OutputChannel outputChannel, Item item, Property property, String language, Object value) throws SQLException {
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
		
		entityManager.persist(newPropertyValue);
	}

}

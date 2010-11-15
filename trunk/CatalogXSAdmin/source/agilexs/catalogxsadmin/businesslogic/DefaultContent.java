package agilexs.catalogxsadmin.businesslogic;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;

import agilexs.catalogxsadmin.jpa.catalog.Category;
import agilexs.catalogxsadmin.jpa.catalog.Item;
import agilexs.catalogxsadmin.jpa.catalog.Label;
import agilexs.catalogxsadmin.jpa.catalog.Property;
import agilexs.catalogxsadmin.jpa.catalog.PropertyType;
import agilexs.catalogxsadmin.jpa.catalog.PropertyValue;
import agilexs.catalogxsadmin.jpa.catalog.Status;


public class DefaultContent {
	private final EntityManager entityManager;
	private Category root;
	private Property nameProperty;

	public DefaultContent(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	/*		  def catalog = currentCatalog.value match {
    case null => throw new Exception("No catalog set")
    case catalog => catalog
  }
 
  def product(name : String) : Option[Product] = {
    querySingle(
        "SELECT p " +
        " FROM Product p" +
        " JOIN p.propertyValues v" +
        " JOIN v.property.labels l" +
        " WHERE l.label = 'Name'" +
        " AND l.language IS NULL" +
        " AND v.stringValue = :name", "name" -> name)
  }
 
  def getOrCreateProduct(name : String) : Product = transaction { em =>
    product(name) match {
      case Some(product) => product
      case None => new Product useIn { product =>
        product.setCatalog(catalog)
        set(product, Properties.name, name)
        em.persist(product)
      }
    }
  }
 
  def createProduct(name : String, variant : String, articleNumber : String, description : String, price : Double, imageClass : Class[_], imageName : String, categories : Category*) = {
                val product = getOrCreateProduct(name)
                set(product, Properties.name, name)
                set(product, Properties.variant, variant)
                set(product, Properties.articleNumber, articleNumber)
                set(product, Properties.description, description)
                set(product, Properties.price, price)
                setImage(product, Properties.image, imageClass, imageName)
                categories foreach (_.getChildren.add(product))
                product
  }
 
 
 
  def getOrCreateShop(name : String) : Shop = {
    shop(name) match {
      case Some(shop) => shop
      case None => new Shop useIn { shop =>
        shop.setName(name)
        shop.setCatalog(catalog)
        catalog.getShops.add(shop)
      }
    }
  }
 
  object Properties {
    def name = getOrCreateProperty(catalog.getRoot, "Name", PropertyType.String)
    def variant = getOrCreateProperty(catalog.getRoot, "Variant", PropertyType.String)
    def articleNumber = getOrCreateProperty(catalog.getRoot, "ArticleNumber", PropertyType.String)
    def description = getOrCreateProperty(catalog.getRoot, "Description", PropertyType.String)
    def image = getOrCreateProperty(catalog.getRoot, "Image", PropertyType.Media)
    def imageLarge = getOrCreateProperty(catalog.getRoot, "ImageLarge", PropertyType.Media)
    def price = getOrCreateProperty(catalog.getRoot, "Price", PropertyType.Money)
    def synopsis = getOrCreateProperty(catalog.getRoot, "Synopsis", PropertyType.String)
    def product(catalog : Catalog) = {}
  }
 
 
 
  def setImage(item : Item, property : Property, cl : java.lang.Class[_], name : String, language : String = null) = {
    val propertyValue = item.getPropertyValues.find(v => v.getProperty == property && v.getLanguage == language) getOrElse new PropertyValue useIn(item.getPropertyValues.add(_))
    propertyValue.setItem(item)
    propertyValue.setProperty(property)
    propertyValue.setLanguage(language)
    val bytes = cl.getResourceAsStream(name).readBytes
    propertyValue.setMediaValue(bytes)
    if (name.endsWith(".jpg")) {
      propertyValue.setMimeType("image/jpeg")
    }
    if (name.endsWith(".gif")) {
      propertyValue.setMimeType("image/gif")
    }
    if (name.endsWith(".png")) {
      propertyValue.setMimeType("image/png")
    }
  }
 
 
  def childExtent(items : Set[Item], visited : Set[Item]) : Set[Item] = {
    val parents = items filterNot visited
    if (parents.isEmpty) return items
    val children = query("SELECT child FROM Item child JOIN item.parents parent WHERE parent IN :parents", "parents" -> parents)
    items ++ childExtent(children.toSet, visited ++ parents)
  }
 
  def findProducts(parents : Iterable[Item], relatedItems : List[Item], page : Page = AllPages) : Set[Product] = {
    childExtent(parents.toSet, Set()).classFilter(classOf[Product])
  }
 
   
  def createInitialCatalog(createInitalData : => Any) : Catalog = transaction { em =>
    querySingle("SELECT c FROM Catalog c") match {
      case Some(catalog) => catalog
      case None => createCatalog("Catalog") useIn (withCatalog(_)(createInitalData))
    }
  }
}*/



	public agilexs.catalogxsadmin.jpa.catalog.Catalog getOrCreateCatalog(String name) {
		agilexs.catalogxsadmin.jpa.catalog.Catalog result = findSingle("SELECT c FROM Catalog c WHERE c.name = :name", Tuple.create("name", name));
		if (result == null) {
			result = new agilexs.catalogxsadmin.jpa.catalog.Catalog();
			result.setName(name);
			entityManager.persist(result);
			entityManager.flush();

			root = new Category();
			root.setCatalog(result);
			result.setRoot(root);
			result.getItems().add(root);
			entityManager.persist(root);
			entityManager.flush();

			setProperty(root, nameProperty(), "All products", null);

			// create shop
			getOrCreateShop("shop", result);
		}

		return result;
	}

	private Property nameProperty() {
		if (nameProperty == null) {
			nameProperty = getOrCreateProperty(root, "Name", PropertyType.String);
		}
		return nameProperty;
	}

	private agilexs.catalogxsadmin.jpa.shop.Shop getOrCreateShop(String name, agilexs.catalogxsadmin.jpa.catalog.Catalog catalog) {
		agilexs.catalogxsadmin.jpa.shop.Shop result = findShop(name);

		if (result == null) {
			result = new agilexs.catalogxsadmin.jpa.shop.Shop();
			result.setName(name);
			result.setCatalog(catalog);
			catalog.getOutputChannels().add(result);
			entityManager.persist(result);
		}
		return result;
	}

	private Category getOrCreateCategory(String name, Category root) {
		Category result = findCategory(name);

		if (result == null) {
			result = new Category();
			setProperty(result, nameProperty(), name, null);
			entityManager.persist(result);
		}

		return result;
	}

	private Property getOrCreateProperty(Item item, String name, PropertyType type) {
		for (Property property : item.getProperties()) {
			for (Label label : property.getLabels()) {
				if (label.getLabel().equals(name) && label.getLanguage() == null) {
					property.setType(type); // TODO correct???
					return property;
				}
			}
		}

		// Not found, create new one
		Property property = new Property();

		item.getProperties().add(property);
		property.setItem(item);
		property.setType(type);
		property.setCategoryProperty(false);
		property.setIsMany(false);
		getOrCreateLabel(property, name, null);
		entityManager.persist(property);

		return property;
	}

	private Label getOrCreateLabel(Property property, String name, String language) {

		for (Label label : property.getLabels()) {
			if (label.getProperty().equals(property) && ObjectUtil.equals(label.getLanguage(), language)) {
				return label;
			}
		}

		// No label found, create a new one.
		Label label = new Label();

		property.getLabels().add(label);
		label.setLanguage(language);
		label.setLabel(name);
		label.setProperty(property);

		entityManager.persist(label);

		return label;
	}

	private void setProperty(Item item, Property property, Object value, String language) {
		PropertyValue propertyValue = findValue(item.getPropertyValues(), property, language);
		if (propertyValue == null) {
			propertyValue = new PropertyValue();
			item.getPropertyValues().add(propertyValue);
			propertyValue.setItem(item);
			propertyValue.setProperty(property);
			propertyValue.setLanguage(language);
			entityManager.persist(propertyValue);
		}

		switch (property.getType()) {
		case String:
			propertyValue.setStringValue((String) value);
			break;
		case Money:
			propertyValue.setMoneyValue((Double) value);
		case Media:
			propertyValue.setMediaValue((byte[]) value);
		}
	}

	private PropertyValue findValue(Collection<PropertyValue> values, Property property, String language) {
		for (PropertyValue value : values) {
			if (value.getProperty().equals(property) && ObjectUtil.equals(value.getLanguage(), language)) {
				return value;
			}
		}
		return null;
	}

	private Catalog findCatalog(String name) {
		return findSingle("SELECT c FROM Catalog c WHERE c.name = :name", Tuple.create("name", name));
	}

	private Category findCategory(String name) {
		return findSingle("SELECT c " + " FROM Category c"
				+ " JOIN c.propertyValues v" + " JOIN v.property.labels l"
				+ " WHERE l.label = 'Name'" + " AND l.language IS NULL"
				+ " AND v.stringValue = :name", Tuple.create("name", name));
	}

	private agilexs.catalogxsadmin.jpa.shop.Shop findShop(String name) {
		return findSingle("SELECT s FROM Shop s WHERE s.name = :name", Tuple.create("name", name));
	}

	private <T> T findSingle(String queryString, Tuple<String, ?>... parameters) {
		Query query = entityManager.createQuery(queryString);

		for (Tuple<String, ?> par : parameters) {
			query.setParameter(par.getFirst(), par.getSecond());
		}

		List<T> result = query.getResultList();
		if (result.size() == 0) {
			return null;
		}
		if (result.size() == 1) {
			return result.get(0);
		}

		throw new NonUniqueResultException("Query: " + queryString
				+ " parameters: " + parameters);
	}

}

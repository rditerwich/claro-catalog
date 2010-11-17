package claro.catalog.model;

import javax.persistence.EntityManager;

import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.Label;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyType;

import com.google.common.base.Objects;

public class CatalogModelUtil {

	public static Catalog findOrCreateCatalog(Long id, EntityManager em) {
		Catalog catalog = em.find(Catalog.class, id);
		if (catalog == null) {
			catalog = new Catalog();
			catalog.setId(id);
			em.persist(catalog);
		}
		findOrCreateRootCategory(catalog, em);
		return catalog;
}

	public static Category findOrCreateRootCategory(Catalog catalog, EntityManager em) {
	  Category root = catalog.getRoot();
	  if (root == null) {
	  	root = new Category();
	  	catalog.setRoot(root);
	  	root.setCatalog(catalog);
	  }
	  return root;
  }

	public static Property findOrCreateProperty(Item item, String name, PropertyType type, EntityManager em) {
	  for (Property property : item.getProperties()) {
	  	for (Label label : property.getLabels()) {
	  		if (label.getLabel().equals(name) && label.getLanguage() == null) {
	  			property.setType(type);
	  			return property;
	  		}
	  	}
	  }
	  Property property = new Property();
	  property.setType(type);
	  property.setItem(item);
	  item.getProperties().add(property);
	  getOrCreateLabel(property, name, null);
	  return property;
  }

	public static Label getOrCreateLabel(Property property, String label, String language) {
  	for (Label lbl : property.getLabels()) {
  		if (lbl.getLabel().equals(label) && Objects.equal(lbl.getLanguage(), language)) {
  			return lbl;
  		}
  	}
  	Label lbl = new Label();
  	lbl.setLabel(label);
  	lbl.setLanguage(language);
  	lbl.setProperty(property);
  	property.getLabels().add(lbl);
  	return lbl;
  }
}

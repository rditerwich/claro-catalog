package claro.catalog.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.Label;
import claro.jpa.catalog.ParentChild;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyType;

import com.google.common.base.Objects;

public class CatalogDao {

	private final EntityManager em;

	public CatalogDao(EntityManager em) {
		this.em = em;
  }
	
	public Item getItem(Long id) {
		return em.find(Item.class, id);
	}

	public Property getProperty(Long id) {
		return em.find(Property.class, id);
	}

	public List<Item> getItems(Collection<Long> ids) {
		List<Item> result = new ArrayList<Item>();
		for (Long id : ids) {
			result.add(getItem(id));
		}
		return result;
	}
	
	public Catalog findOrCreateCatalog(Long id) {
		Catalog catalog = em.find(Catalog.class, id);
		if (catalog == null) {
			catalog = new Catalog();
			catalog.setId(id);
			em.persist(catalog);
		}
		findOrCreateRootCategory(catalog);
		return catalog;
}

	public Category findOrCreateRootCategory(Catalog catalog) {
	  Category root = catalog.getRoot();
	  if (root == null) {
	  	root = new Category();
	  	catalog.setRoot(root);
	  	root.setCatalog(catalog);
	  }
	  return root;
  }

	public Property findOrCreateProperty(Item item, String name, PropertyType type) {
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

	public Label getOrCreateLabel(Property property, String label, String language) {
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
  	em.persist(lbl);
  	return lbl;
  }
	
	public boolean setItemParents(Item item, List<Item> parents) {
		boolean changed = false;
		// remove old parents
		for (ParentChild parentChild : new ArrayList<ParentChild>(item.getParents())) {
			Item parent = parentChild.getParent();
			if (parent != null) {
				if (!parents.contains(parent)) {
					parent.getChildren().remove(parentChild);
					item.getParents().remove(parentChild);
					em.remove(parentChild);
					changed = true;
				}
			}
		}
		// add new parents
		for (Item parent : parents) {
			boolean found = false;
			for (ParentChild currentParentChild : item.getParents()) {
				if (currentParentChild.getParent() != null && currentParentChild.getParent().equals(parent)) {
					found = true;
				}
			}			
			if (!found) {
				ParentChild parentChild = new ParentChild();
				parentChild.setParent(parent);
				parentChild.setChild(item);
				parent.getChildren().add(parentChild);
				item.getParents().add(parentChild);
				em.persist(parentChild);
				changed = true;
			}
		}
		// re-index parents
		for (ParentChild parentChild : new ArrayList<ParentChild>(item.getParents())) {
			int index = parents.indexOf(parentChild.getParent());
			if (parentChild.getIndex() != index) {
				parentChild.setIndex(index);
				changed = true;
			}
		}
		return changed;
	}
}

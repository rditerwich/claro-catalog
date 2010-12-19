package claro.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import claro.catalog.data.RootProperties;
import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.Category_;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.Label;
import claro.jpa.catalog.Label_;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.ParentChild;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyType;
import claro.jpa.catalog.PropertyValue;
import claro.jpa.catalog.PropertyValue_;
import claro.jpa.catalog.Property_;
import claro.jpa.catalog.StagingArea;
import claro.jpa.importing.ImportDefinition;
import claro.jpa.importing.ImportDefinition_;

import com.google.common.base.Objects;

import easyenterprise.lib.util.Paging;

public class CatalogDao {

	private final EntityManager entityManager;

	public CatalogDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	public CriteriaBuilder getCriteriaBuilder() {
		return entityManager.getCriteriaBuilder();
	}
	
	public <T> T findOrCreate(Class<T> type, Long id) {
		if (id != null) {
			return entityManager.find(type, id);
		} else {
			try {
				T impl = type.newInstance();
				entityManager.persist(impl);
				return impl;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public Item getItem(Long id) {
		return entityManager.find(Item.class, id);
	}

	public Property getProperty(Long id) {
		return entityManager.find(Property.class, id);
	}

	public List<Item> getItems(Collection<Long> ids) {
		List<Item> result = new ArrayList<Item>();
		for (Long id : ids) {
			result.add(getItem(id));
		}
		return result;
	}
	
	public Catalog findOrCreateCatalog(Long id) {
		Catalog catalog = entityManager.find(Catalog.class, id);
		if (catalog == null) {
			catalog = new Catalog();
			catalog.setId(id);
			catalog.setName(""); // TODO Should we pass the name as par here?
			entityManager.persist(catalog);
			entityManager.flush(); // Force id generation, because there is an interdependency between catalog and the root category.
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
	  	entityManager.persist(root);
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
	  property.setCategoryProperty(false);
	  property.setType(type);
	  property.setItem(item);
	  property.setIsMany(false);
	  item.getProperties().add(property);
	  getOrCreateLabel(property, name, null);
	  
	  entityManager.persist(property);
	  
	  return property;
  }
	
	public void setPropertyValue(StagingArea stagingArea, OutputChannel outputChannel, Item item, Property property, String language, Object value) {
		for (PropertyValue propertyValue : item.getPropertyValues()) {
			if (propertyValue.getProperty().equals(property) 
			&& Objects.equal(propertyValue.getStagingArea(), stagingArea)
			&& Objects.equal(propertyValue.getOutputChannel(), outputChannel)
			&& Objects.equal(propertyValue.getLanguage(), language)) {
				setValue(propertyValue, value, property.getType());
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
		setValue(newPropertyValue, value, property.getType());
		
		entityManager.persist(newPropertyValue);
	}
	
	private void setValue(PropertyValue propertyValue, Object value, PropertyType type) {
		switch(type) {
		// TODO add actual cases...
		default:
			propertyValue.setStringValue(value.toString());
			
		}
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
  	
  	entityManager.persist(lbl);
  	
  	return lbl;
  }
	
	public Category getCategoryByName(Catalog catalog, String name) {
		CriteriaBuilder cb = getCriteriaBuilder();
		CriteriaQuery<Category> c = cb.createQuery(Category.class);
		
		ParameterExpression<String> nameParam = cb.parameter(String.class);
		
		Root<Category> root = c.from(Category.class);
		Join<Category, PropertyValue> valuesJoin = root.join(Category_.propertyValues);
		Join<PropertyValue, Property> propertyJoin = valuesJoin.join(PropertyValue_.property);
		Join<Property,Label> labelsJoin = propertyJoin.join(Property_.labels);
		
		CriteriaQuery<Category> query = c.select(root).where(cb.and(
			cb.isNull(labelsJoin.get(Label_.language)),
			cb.equal(labelsJoin.get(Label_.label), RootProperties.NAME),
			cb.equal(valuesJoin.get(PropertyValue_.stringValue), nameParam)));
		
		return getEntityManager().createQuery(c).setParameter(nameParam, name).getSingleResult();
		
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
					entityManager.remove(parentChild);
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
				entityManager.persist(parentChild);
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
	
	
	public List<Item> findItems(Catalog catalog, OutputChannel outputChannel, boolean productsOnly) {
		StringBuilder queryString = new StringBuilder("select item from ");
		
		if (productsOnly) {
			queryString.append("Product ");
		} else {
			queryString.append("Item ");
		}
		
		queryString.append("item where item.catalog = :catalog");
		if (outputChannel != null) {
			queryString.append(" and not exists(select channel from OutputChannel channel where channel.excludedItems contains :channel)");
		}
		
		TypedQuery<Item> query = entityManager.createQuery(queryString.toString(), Item.class);
		query.setParameter("catalog", catalog);
		if (outputChannel != null) {
			query.setParameter("outputChannel", outputChannel);
		}
		
		return query.getResultList();
	}
	
	public OutputChannel getOutputChannel(Long outputChannelId) {
		return entityManager.find(OutputChannel.class, outputChannelId);
	}

	public List<ImportDefinition> getImportDefinitions(Paging paging) {
		CriteriaBuilder cb = getCriteriaBuilder();
		CriteriaQuery<ImportDefinition> c = cb.createQuery(ImportDefinition.class);
		Root<ImportDefinition> importDefinition = c.from(ImportDefinition.class);
		c.select(importDefinition);
		
		TypedQuery<ImportDefinition> query = entityManager.createQuery(c);
		if (paging.shouldPage()) {
			query.setFirstResult(paging.getPageStart());
			query.setMaxResults(paging.getPageSize());
		}
		return query.getResultList();
	}
	
	public ImportDefinition getImportDefinitionById(Long id) {
		CriteriaBuilder cb = getCriteriaBuilder();
		CriteriaQuery<ImportDefinition> c = cb.createQuery(ImportDefinition.class);
		Parameter<Long> idParam = cb.parameter(Long.class);
		Root<ImportDefinition> importDefinition = c.from(ImportDefinition.class);
		Path<Long> idAttr = importDefinition.get(ImportDefinition_.id);
		c.select(importDefinition).where(cb.equal(idAttr, idParam));
		
		TypedQuery<ImportDefinition> query = entityManager.createQuery(c).setParameter(idParam, id);
		return query.getSingleResult();
	}
	
	public List<ImportDefinition> getImportDefinitionsByName(String name, Paging paging) {
		CriteriaBuilder cb = getCriteriaBuilder();
		CriteriaQuery<ImportDefinition> c = cb.createQuery(ImportDefinition.class);
		ParameterExpression<String> nameParam = cb.parameter(String.class);
		Root<ImportDefinition> importDefinition = c.from(ImportDefinition.class);
		Path<String> nameAttr = importDefinition.get(ImportDefinition_.name);
		c.select(importDefinition).where(cb.like(nameAttr, nameParam));

		TypedQuery<ImportDefinition> query = entityManager.createQuery(c).setParameter(nameParam, "%" + name + "%");
		if (paging.shouldPage()) {
			query.setFirstResult(paging.getPageStart());
			query.setMaxResults(paging.getPageSize());
		}
		return query.getResultList();
	}

	public StagingArea getStagingArea(Long stagingAreaId) {
		return entityManager.find(StagingArea.class, stagingAreaId);
	}
}

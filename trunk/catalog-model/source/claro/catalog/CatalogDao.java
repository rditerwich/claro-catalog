package claro.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import claro.catalog.data.RootProperties;
import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.Catalog_;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.Category_;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.Label;
import claro.jpa.catalog.Label_;
import claro.jpa.catalog.OutputChannel;
import claro.jpa.catalog.OutputChannel_;
import claro.jpa.catalog.ParentChild;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyGroup;
import claro.jpa.catalog.PropertyGroupAssignment;
import claro.jpa.catalog.PropertyValue;
import claro.jpa.catalog.PropertyValue_;
import claro.jpa.catalog.Property_;
import claro.jpa.catalog.Source;
import claro.jpa.catalog.StagingArea;
import claro.jpa.catalog.StagingArea_;
import claro.jpa.importing.ImportJobResult;
import claro.jpa.importing.ImportJobResult_;
import claro.jpa.importing.ImportSource;
import claro.jpa.importing.ImportSource_;
import claro.jpa.jobs.Job;
import claro.jpa.jobs.JobResult;
import claro.jpa.jobs.JobResult_;
import claro.jpa.shop.Shop;

import com.google.common.base.Objects;

import easyenterprise.lib.jpa.AbstractDao;
import easyenterprise.lib.util.CollectionUtil;
import easyenterprise.lib.util.Paging;

public class CatalogDao extends AbstractDao {
	
	public CatalogDao(Map<String, String> properties) {
		super("claro.jpa.catalog", properties);
	}
	
	public CriteriaBuilder getCriteriaBuilder() {
		return getEntityManager().getCriteriaBuilder();
	}
	
	public <T> T findOrCreate(Class<T> type, Long id) {
		EntityManager entityManager = getEntityManager();
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
		return getEntityManager().find(Item.class, id);
	}

	public Property getProperty(Long id) {
		return getEntityManager().find(Property.class, id);
	}

	public PropertyValue getPropertyValue(Item item, Property property, Source source, StagingArea stagingArea, OutputChannel outputChannel, String language) {
		CriteriaBuilder cb = getCriteriaBuilder();
		CriteriaQuery<PropertyValue> c = cb.createQuery(PropertyValue.class);
		
		ParameterExpression<Item> itemParam = cb.parameter(Item.class);
		ParameterExpression<Property> propertyParam = cb.parameter(Property.class);
		ParameterExpression<Source> sourceParam = source != null ? cb.parameter(Source.class) : null;
		ParameterExpression<StagingArea> stagingParam = stagingArea != null ? cb.parameter(StagingArea.class) : null;
		ParameterExpression<OutputChannel> outputChannelParam = outputChannel != null ? cb.parameter(OutputChannel.class) : null;
		ParameterExpression<String> languageParam = language != null ? cb.parameter(String.class) : null;
		
		Root<PropertyValue> root = c.from(PropertyValue.class);
		Expression<?> property1 = root.get(PropertyValue_.source);
		Expression<?> property2 = root.get(PropertyValue_.stagingArea);
		Expression<?> property3 = root.get(PropertyValue_.outputChannel);
		Expression<?> property4 = root.get(PropertyValue_.language);
		c.select(root).where(
				cb.equal(root.get(PropertyValue_.item), itemParam),
				cb.equal(root.get(PropertyValue_.property), propertyParam),
				sourceParam == null ?
						cb.isNull(property1) :
						cb.equal(property1, sourceParam),
				stagingParam == null ? 
					cb.isNull(property2) :
					cb.equal(property2, stagingParam),
				outputChannelParam == null ? 
					cb.isNull(property3) :
					cb.equal(property3, outputChannelParam),
				languageParam == null ? 
					cb.isNull(property4) :
					cb.equal(property4, languageParam));
		
		TypedQuery<PropertyValue> query = getEntityManager().createQuery(c);
		query.setParameter(itemParam, item);
		query.setParameter(propertyParam, property);
		if (source != null)
			query.setParameter(sourceParam, source);
		if (stagingArea != null)
				query.setParameter(stagingParam, stagingArea);
		if (outputChannel != null)
				query.setParameter(outputChannelParam, outputChannel);
		if (language != null)
				query.setParameter(languageParam, language);

		return CollectionUtil.firstOrNull(query.getResultList());
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
  	
  	getEntityManager().persist(lbl);
  	
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
		
		c.select(root).where(cb.and(
			cb.isNull(labelsJoin.get(Label_.language)),
			cb.equal(labelsJoin.get(Label_.label), RootProperties.NAME),
			cb.equal(valuesJoin.get(PropertyValue_.stringValue), nameParam)));
		
		return getEntityManager().createQuery(c).setParameter(nameParam, name).getSingleResult();
		
	}
	
	public StagingArea findStagingAreaByName(String name) {
		CriteriaBuilder cb = getCriteriaBuilder();
		CriteriaQuery<StagingArea> c = cb.createQuery(StagingArea.class);
		Root<StagingArea> root = c.from(StagingArea.class);
		c.select(root).where(cb.equal(root.get(StagingArea_.name), name));
		List<StagingArea> result = getEntityManager().createQuery(c).getResultList();
		if (!result.isEmpty()) {
			return result.get(0);
		}
		return null;
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
					getEntityManager().remove(parentChild);
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
				getEntityManager().persist(parentChild);
				changed = true;
			}
		}
		// re-index parents
		for (ParentChild parentChild : new ArrayList<ParentChild>(item.getParents())) {
			int index = parents.indexOf(parentChild.getParent());
			if (parentChild.getIndex() == null || parentChild.getIndex() != index) {
				parentChild.setIndex(index);
				changed = true;
			}
		}
		return changed;
	}
	
	public void removeItem(Item item) {
		EntityManager entityManager = getEntityManager();
		// Clear parents, but remember the first parent
		Item firstParent = item.getParents().iterator().next().getParent();
		for (ParentChild parent : item.getParents()) {
			parent.getParent().getChildren().remove(parent);
			entityManager.remove(parent);
		}
		item.getParents().clear();
		
		// Clear children, reassign children to firstparent.
		ArrayList<ParentChild> children = new ArrayList<ParentChild>(item.getChildren());
		item.getChildren().clear();
		for (ParentChild child : children) {
			// reassign to firstparent
			child.setParent(firstParent);
			firstParent.getChildren().add(child);
			
			// Find double:
			ParentChild first = null;
			ParentChild second = null;
			for (ParentChild childParent : child.getChild().getParents()) {
				if (childParent.getParent().getId().equals(firstParent.getId())) {
					if (first != null) {
						second = childParent;
						break;  // Found a double
					} else {
						first = childParent;
					}
				}
			}
			
			// Remove double
			if (second != null) {
				child.getChild().getParents().remove(second);
				firstParent.getChildren().remove(second);
				second.setChild(null);
				second.setParent(null);
				
				entityManager.remove(second);
			}
		}
		
		// Remove from catalog
		item.getCatalog().getItems().remove(item);
		
		// Finally remove from db.
		entityManager.remove(item);
	}
	

	
	
	public List<Item> findItems(Catalog catalog, OutputChannel outputChannel, Class<? extends Item> itemClass) {
		StringBuilder queryString = new StringBuilder("select item from ");
		
		if (itemClass != null) {
			queryString.append(itemClass.getSimpleName() + " ");
		} else {
			queryString.append("Item ");
		}
		
		queryString.append("item where item.catalog = :catalog");
		if (outputChannel != null) {
			queryString.append(" and not exists(select channel from OutputChannel channel where channel.excludedItems contains :channel)");
		}
		
		TypedQuery<Item> query = getEntityManager().createQuery(queryString.toString(), Item.class);
		query.setParameter("catalog", catalog);
		if (outputChannel != null) {
			query.setParameter("outputChannel", outputChannel);
		}
		
		return query.getResultList();
	}
	
	public OutputChannel getOutputChannel(Long outputChannelId) {
		return getEntityManager().find(OutputChannel.class, outputChannelId);
	}

	public List<ImportSource> getImportSources(Paging paging) {
		CriteriaBuilder cb = getCriteriaBuilder();
		CriteriaQuery<ImportSource> c = cb.createQuery(ImportSource.class);
		Root<ImportSource> importSource = c.from(ImportSource.class);
		c.select(importSource).orderBy(cb.asc(importSource.get(ImportSource_.name)));
		
		TypedQuery<ImportSource> query = getEntityManager().createQuery(c);
		if (paging.shouldPage()) {
			query.setFirstResult(paging.getPageStart());
			query.setMaxResults(paging.getPageSize());
		}
		return query.getResultList();
	}
	
	public ImportSource getImportSourceById(Long id) {
		CriteriaBuilder cb = getCriteriaBuilder();
		CriteriaQuery<ImportSource> c = cb.createQuery(ImportSource.class);
		Parameter<Long> idParam = cb.parameter(Long.class);
		Root<ImportSource> ImportSource = c.from(ImportSource.class);
		Path<Long> idAttr = ImportSource.get(ImportSource_.id);
		c.select(ImportSource).where(cb.equal(idAttr, idParam));
		
		TypedQuery<ImportSource> query = getEntityManager().createQuery(c).setParameter(idParam, id);
		return query.getSingleResult();
	}
	
	public List<ImportJobResult> getImportSourceHistory(Long importSourceId, Paging paging) {
		CriteriaBuilder cb = getCriteriaBuilder();
		CriteriaQuery<ImportJobResult> c = cb.createQuery(ImportJobResult.class);
		Root<ImportJobResult> jobResult = c.from(ImportJobResult.class);
		Path<ImportSource> importSourceAttr = jobResult.get(ImportJobResult_.importSource);
		Path<Long> importSourceIdAttr = importSourceAttr.get(ImportSource_.id);
		c.select(jobResult).
			where(cb.equal(importSourceIdAttr, importSourceId)).
		  orderBy(cb.desc(jobResult.get(JobResult_.endTime)));
		
		TypedQuery<ImportJobResult> query = getEntityManager().createQuery(c);
		if (paging.shouldPage()) {
			query.setFirstResult(paging.getPageStart());
			query.setMaxResults(paging.getPageSize());
		}
		return query.getResultList();
	}
	
	public List<ImportSource> getImportSourcesByName(String name, Paging paging) {
		CriteriaBuilder cb = getCriteriaBuilder();
		CriteriaQuery<ImportSource> c = cb.createQuery(ImportSource.class);
		ParameterExpression<String> nameParam = cb.parameter(String.class);
		Root<ImportSource> ImportSource = c.from(ImportSource.class);
		Path<String> nameAttr = ImportSource.get(ImportSource_.name);
		c.select(ImportSource).
			where(cb.like(nameAttr, nameParam)).
			orderBy(cb.asc(nameAttr));

		TypedQuery<ImportSource> query = getEntityManager().createQuery(c).setParameter(nameParam, "%" + name + "%");
		if (paging.shouldPage()) {
			query.setFirstResult(paging.getPageStart());
			query.setMaxResults(paging.getPageSize());
		}
		return query.getResultList();
	}

	public List<PropertyValue> getPropertyValuesBySource(Source source) {
		CriteriaBuilder cb = getCriteriaBuilder();
		CriteriaQuery<PropertyValue> c = cb.createQuery(PropertyValue.class);
		Root<PropertyValue> root = c.from(PropertyValue.class);
		c.where(cb.equal(root.get(PropertyValue_.source), source));
		TypedQuery<PropertyValue> query = getEntityManager().createQuery(c);
		return query.getResultList();
	}
	
	public StagingArea getStagingArea(Long stagingAreaId) {
		return getEntityManager().find(StagingArea.class, stagingAreaId);
	}

	public List<JobResult> getLastJobResults(Job job, int count) {
		CriteriaBuilder cb = getCriteriaBuilder();
		CriteriaQuery<JobResult> c = cb.createQuery(JobResult.class);
		Root<JobResult> root = c.from(JobResult.class);
		c.where(cb.equal(root.get(JobResult_.job), job));
		c.orderBy(cb.desc(root.get(JobResult_.endTime)));
		TypedQuery<JobResult> query = getEntityManager().createQuery(c);//.setParameter(jobParam, job);
		query.setMaxResults(count);
		return query.getResultList();
	}

	public void removeGroupAssignment(PropertyGroupAssignment groupAssignment) {
		groupAssignment.getCategory().getPropertyGroupAssignments().remove(groupAssignment);
		getEntityManager().remove(groupAssignment);
	}

	public PropertyGroupAssignment createGroupAssignment(Property property, PropertyGroup group, Category item) {
		PropertyGroupAssignment result = new PropertyGroupAssignment();
		
		result.setCategory(item);
		result.setProperty(property);
		result.setPropertyGroup(group);
		item.getPropertyGroupAssignments().add(result);
		
		getEntityManager().persist(result);
		
		return result;
	}

	public void removeProperty(Property entity) {
		for (Label label : entity.getLabels()) {
			getEntityManager().remove(label);
		}
		getEntityManager().remove(entity);
	}

	public List<Shop> getShops(Long catalogId, Paging paging) {
		CriteriaBuilder cb = getCriteriaBuilder();
		CriteriaQuery<Shop> cQuery = cb.createQuery(Shop.class);
		
		Root<Shop> shops = cQuery.from(Shop.class);
		Path<Long> catalogIdAttr = shops.get(OutputChannel_.catalog).get(Catalog_.id);

		cQuery
			.select(shops)
			.where(cb.equal(catalogIdAttr, catalogId))
			.orderBy(cb.asc(shops.get(OutputChannel_.name)));
		
		
		TypedQuery<Shop> query = getEntityManager().createQuery(cQuery);
		if (paging.shouldPage()) {
			query.setFirstResult(paging.getPageStart());
			query.setMaxResults(paging.getPageSize());
		}
		
		return query.getResultList();
	}

	public void removeAllStagingValues(StagingArea to) {
		if (to == null || to.getId() == null) throw new IllegalArgumentException("to must not be null");
		EntityManager entityManager = getEntityManager();
		Query query = entityManager.createQuery("DELETE FROM PropertyValue WHERE stagingArea=:stagingArea").setParameter("stagingAreao", to);
		query.executeUpdate();
	}
}
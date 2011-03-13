package claro.catalog;

import static easyenterprise.lib.util.CollectionUtil.firstOrNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.QueryHints;

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
import claro.jpa.catalog.StagingStatus;
import claro.jpa.importing.ImportJobResult;
import claro.jpa.importing.ImportJobResult_;
import claro.jpa.importing.ImportSource;
import claro.jpa.importing.ImportSource_;
import claro.jpa.jobs.Job;
import claro.jpa.jobs.JobResult;
import claro.jpa.jobs.JobResult_;
import claro.jpa.media.Media;
import claro.jpa.media.MediaContent;
import claro.jpa.order.Order;
import claro.jpa.order.OrderStatus;
import claro.jpa.shop.Promotion;
import claro.jpa.shop.Shop;
import claro.jpa.shop.VolumeDiscountPromotion;

import com.google.common.base.Objects;

import easyenterprise.lib.jpa.AbstractDao;
import easyenterprise.lib.util.Paging;

public class CatalogDao extends AbstractDao {
	
	public static final String PERSISTENCE_UNIT_NAME = "claro.jpa.catalog";

	public CatalogDao(Map<String, String> properties) {
		super(PERSISTENCE_UNIT_NAME, properties);
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

	public List<PropertyValue> getPropertyValues(Item item) {
		return getEntityManager().createQuery("select v from propertyvalue v where v.item = :item", PropertyValue.class).setParameter("item", item).getResultList();
	} 
	
	public PropertyValue getPropertyValue(Item item, Property property, Source source, StagingArea stagingArea, OutputChannel outputChannel, String language) {
		StringBuilder queryString = new StringBuilder();
		
		queryString.append("select pv from PropertyValue pv where pv.item = :item and pv.property = :property");
		if (source != null) {
			queryString.append(" and pv.source = :source");
		} else {
			queryString.append(" and pv.source is null");
		}
		if (stagingArea != null) {
			queryString.append(" and pv.stagingArea = :stagingArea");
		} else {
			queryString.append(" and pv.stagingArea is null");
		}
		if (outputChannel != null) {
			queryString.append(" and pv.outputChannel = :outputChannel");
		} else {
			queryString.append(" and pv.outputChannel is null");
		}
		if (language != null) {
			queryString.append(" and pv.language = :language");
		} else {
			queryString.append(" and pv.language is null");
		}
		
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("item", item);
		query.setParameter("property", property);
		if (source != null) {
			query.setParameter("source", source);
		}
		if (stagingArea != null) {
			query.setParameter("stagingArea", stagingArea);
		}
		if (outputChannel != null) {
			query.setParameter("outputChannel", outputChannel);
		}
		if (language != null) {
			query.setParameter("language", language);
		}
		
		@SuppressWarnings("unchecked")
		List<PropertyValue> propertyValues = query.getResultList();
		
		// Return the result
		if (propertyValues.size() == 1) {
			return propertyValues.get(0);
		} else if (propertyValues.isEmpty()) {
			return null;
		} else {
			throw new IllegalStateException("Multiple propertyvalues found for property id " + property.getId() + "source " + source + ", stagingArea" + stagingArea + ", outputChannel " + outputChannel + " and language " + language);
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

	public void removePropertyValue(PropertyValue propertyValue) {
		propertyValue.getItem().getPropertyValues().remove(propertyValue);
		getEntityManager().remove(propertyValue);
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
		Query query = entityManager.createQuery("DELETE FROM PropertyValue v WHERE v.stagingArea=:stagingArea").setParameter("stagingArea", to);
		query.executeUpdate();
	}
	
	public StagingStatus getOrCreateStagingStatus(Catalog catalog, StagingArea stagingArea) {
		@SuppressWarnings("unchecked")
		List<StagingStatus> result = getEntityManager().createQuery("SELECT s FROM StagingStatus s WHERE s.catalog=:catalog AND s.stagingArea=:stagingArea").
			setHint(QueryHints.CACHE_USAGE, CacheUsage.DoNotCheckCache).
			setParameter("catalog", catalog).
			setParameter("stagingArea", stagingArea).
			getResultList();
		if (!result.isEmpty()) {
			StagingStatus status = result.get(0);
			getEntityManager().refresh(status);
			return status;
		}
		StagingStatus status = new StagingStatus();
		status.setCatalog(catalog);
		status.setStagingArea(stagingArea);
		status.setUpdateSequenceNr(0);
		getEntityManager().persist(status);
		return status;
	}
	
	
	public StagingArea getOrCreateStagingArea(String name) {
		@SuppressWarnings("unchecked")
		List<StagingArea> result = getEntityManager().createQuery("SELECT s FROM StagingArea s WHERE s.name=:name").
		setParameter("name", name).
		getResultList();
		if (!result.isEmpty()) {
			StagingArea status = result.get(0);
			return status;
		}
		StagingArea stagin = new StagingArea();
		stagin.setName(name);
		getEntityManager().persist(stagin);
		return stagin;
	}
	

	@SuppressWarnings("unchecked")
	public List<Order> getOrders(OrderStatus statusFilter, Paging paging) {
		EntityManager entityManager = getEntityManager();
		String queryString = "select o from Order o " + (statusFilter != null? " where o.status = :orderStatus " : "") + "order by o.orderDate desc";
		Query query = entityManager.createQuery(queryString);
		
		if (statusFilter != null) {
			query.setParameter("orderStatus", statusFilter);
		}
		
		if (paging.shouldPage()) {
			query.setFirstResult(paging.getPageStart());
			query.setMaxResults(paging.getPageSize());
		}
		
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public void setPropertyLabel(Property property, String language, String value) {
		EntityManager entityManager = getEntityManager();

		Query query = entityManager
			.createQuery("select l from Label l where l.property.id = :propId" + (language != null? " and l.language = :language" : " and l.language is null"))
			.setParameter("propId", property.getId());
		
		if (language != null) {
			query.setParameter("language", language);
		}
		
		List<Label> labels = query.getResultList();
		if (labels.size() == 1) {
			Label label = labels.get(0);
			label.setLanguage(language);
			label.setLabel(value);
		} else if (labels.isEmpty()) {
			// New label 
			Label label = new Label();
			label.setLanguage(language);
			label.setLabel(value);
			label.setProperty(property);

			property.getLabels().add(label);
			
			entityManager.persist(label);
		} else {
			throw new IllegalStateException("Multiple labels found for property id " + property.getId() + " and language " + language);
		}
		
	}

	@SuppressWarnings("unchecked")
	public List<Promotion> findPromotions(Long catalogId, List<Shop> shops) {
		EntityManager entityManager = getEntityManager();
		
		boolean shopsRequested = shops != null && !shops.isEmpty();
		
		Query query = entityManager
//			.createQuery("select p from Promotion p where " + (shopsRequested? " p.shop in :shops" : " p.shop.catalog.id = :catalogId"));
			.createQuery("select p from Promotion p where " + (shopsRequested? " p.shop = :firstShop" : " p.shop.catalog.id = :catalogId") + " order by p.startDate desc");

		if (shopsRequested) {
//			query.setParameter("shops", shops);
			query.setParameter("firstShop", shops.get(0));
		} else {
			query.setParameter("catalogId", catalogId);
		}

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<VolumeDiscountPromotion> findPromotionsForProduct(Long catalogId, Long productId) {
		EntityManager entityManager = getEntityManager();
		Query query = entityManager.createQuery("select p from VolumeDiscountPromotion p where p.product.id = :productId and (p.endDate is null or p.endDate > CURRENT_DATE)");
		
		query.setParameter("productId", productId);
		
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<MediaContent> findMediaContent(String mimeType, String hash) {
		Query query = getEntityManager().createQuery("select c from MediaContent c where c.mimeType = :mimeType and c.hash = :hash");
		query.setParameter("mimeType", mimeType);
		query.setParameter("hash", hash);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public MediaContent findMediaContentById(Long mediaContentId) {
		Query query = getEntityManager().createQuery("select m from MediaContent m where m.id = :id");
		query.setParameter("id", mediaContentId);
		return (MediaContent) firstOrNull(query.getResultList());
	}
	
	@SuppressWarnings("unchecked")
	public Media findMediaById(Long mediaContentId) {
		Query query = getEntityManager().createQuery("select m from Media m where m.id = :id");
		query.setParameter("id", mediaContentId);
		return (Media) firstOrNull(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	public List<Media> findMediaByContent(MediaContent mediaContent) {
		Query query = getEntityManager().createQuery("select m from Media m where m.content = :content");
		query.setParameter("content", mediaContent);
		return query.getResultList();
		
	}
	
}

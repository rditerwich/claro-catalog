package claro.catalog.manager.businesslogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

import claro.catalog.manager.businesslogic.query.AllProductsQuery;
import claro.jpa.catalog.Catalog;
import claro.jpa.catalog.Category;
import claro.jpa.catalog.Item;
import claro.jpa.catalog.Label;
import claro.jpa.catalog.Language;
import claro.jpa.catalog.ParentChild;
import claro.jpa.catalog.Product;
import claro.jpa.catalog.Property;
import claro.jpa.catalog.PropertyValue;
import claro.jpa.shop.Shop;


@Stateless
public class CatalogBean extends CatalogBeanBase implements claro.catalog.manager.businesslogic.Catalog {

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Catalog getOrCreateCatalog(String name) {
		return new DefaultContent(entityManager).getOrCreateCatalog(name);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<PropertyValue> findAllCategoryNames(Catalog catalog) {
		final Query query = entityManager
				.createQuery("select pv from PropertyValue pv, in(pv.property.labels) lbl where type(pv.item) in (Category) and lbl.label = 'Name' and pv.item.catalog = :catalog");

		query.setParameter("catalog", catalog);
		final List<PropertyValue> result = query.getResultList();

		for (PropertyValue pv : result) {
			pv.getProperty().setLabels(pv.getProperty().getLabels());
		}
		return result;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Product> findAllProducts(Integer fromIndex, Integer pageSize, AllProductsQuery filter) {
		// Validate the input arguments
		if (fromIndex == null || fromIndex < 0) {
			throw new IllegalArgumentException(
					"fromIndex < 0, from index must be 0 at least");
		}
		if (pageSize == null || pageSize < 1) {
			throw new IllegalArgumentException(
					"pageSize < 1, page size must be 1 at least");
		}
		final List<Long> parents = new ArrayList<Long>();
		final List<Category> queue = new ArrayList<Category>();
		// final List<Long> done = new ArrayList<Long>();

		queue.add(filter.getCategory());
		while (!queue.isEmpty()) {
			final String queryString = "select p from Category p, IN(p.parents) pg where pg.parent = :productgroup";
			final Query query = entityManager.createQuery(queryString);
			final Category item = queue.remove(0);

			query.setParameter("productgroup", item);
			// done.add(item.getId());
			parents.add(item.getId());
			for (Category pg : (List<Category>) query.getResultList()) {
				// if (!done.contains(pg.getId())) {
				if (!parents.contains(pg.getId())) {
					queue.add(pg);
				}
			}
		}
		final StringBuffer sb = new StringBuffer();

		sb.append("(");
		for (int i = 0; i < parents.size(); i++) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(parents.get(i));
		}
		sb.append(")");
		final boolean filterString = filter.getStringValue() != null
				&& !"".equals(filter.getStringValue());
		final String queryString = "select distinct p from Product p, in(p.propertyValues) pv, in(p.parents) parent"
				+ " where p.catalog = :catalog"
				+ (filterString ? " and pv.stringValue like :stringValue" : "")
				+ (!parents.isEmpty() ? " and parent.id in " + sb.toString()
						: "");
		final Query query = entityManager.createQuery(queryString);

		query.setParameter("catalog", filter.getShop().getCatalog());
		if (filterString) {
			query.setParameter("stringValue", "%" + filter.getStringValue()
					+ "%");
		}
		query.setFirstResult(fromIndex);
		query.setMaxResults(pageSize);
		return query.getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Catalog findCatalogById(Long id) {
		final Catalog catalog = super.findCatalogById(id);

		if (catalog != null) {
			final Query query = entityManager
					.createQuery("select pg from Category pg where pg.catalog = :catalog");

			query.setParameter("catalog", catalog);
			final Collection result = query.getResultList();

			catalog.setItems(result);
			for (Category productGroup : (Collection<Category>) result) {
				// check, because db containsProduct table may be null
				if (productGroup.getContainsProducts() == null) {
					productGroup.setContainsProducts(Boolean.FALSE);
				}
				productGroup.setCatalog(productGroup.getCatalog());
				for (Property property : productGroup.getProperties()) {
					property.setLabels(property.getLabels());
					for (Label lbl : property.getLabels()) {
						lbl.setEnumValue(lbl.getEnumValue());
					}
					property.setEnumValues(property.getEnumValues());
					property.setItem(property.getItem());
				}
				for (PropertyValue value : productGroup.getPropertyValues()) {
					value.setItem(value.getItem());
					value.setProperty(value.getProperty());
				}
			}
		}
		return catalog;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Language> getAllLanguages() {
		final Locale list[] = SimpleDateFormat.getAvailableLocales();
		final List<Language> languages = new ArrayList<Language>();

		Arrays.sort(list, new Comparator<Locale>() {
			@Override
			public int compare(Locale o1, Locale o2) {
				return o1.getDisplayName().compareTo(o2.getDisplayName());
			}
		});
		for (int i = 0; i < list.length; i++) {
			final Language lang = new Language();

			lang.setId(Long.valueOf(i));
			lang.setName(list[i].getLanguage());
			lang.setDisplayName(list[i].getDisplayName());
			languages.add(lang);
		}
		return languages;
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Collection<Product> findProductsByCatalogId(Catalog catalog) {
		final ArrayList<Product> products = new ArrayList<Product>();

		if (catalog != null) {
			final Query query = entityManager
					.createQuery("select p from Product p where p.catalog = :catalog");

			query.setParameter("catalog", catalog);
			for (Product p : (Collection<Product>) query.getResultList()) {
				products.add(findProductById(p.getId()));
			}
		}
		return products;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Product> findAllByCategoryProducts(Integer fromIndex, Integer pageSize, Category filter) {
		// Validate the input arguments
		if (fromIndex == null || fromIndex < 0) {
			throw new IllegalArgumentException(
					"fromIndex < 0, from index must be 0 at least");
		}
		if (pageSize == null || pageSize < 1) {
			throw new IllegalArgumentException(
					"pageSize < 1, page size must be 1 at least");
		}
		final String queryString = "select p from Product p, IN(p.parents) pg where pg = :productgroup";
		final Query query = entityManager.createQuery(queryString);

		query.setParameter("productgroup", filter);
		query.setFirstResult(fromIndex);
		query.setMaxResults(pageSize);
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Product findProductById(Long id) {
		final Product p = super.findProductById(id);

		p.setCatalog(p.getCatalog());
		for (PropertyValue pv : p.getPropertyValues()) {
			pv.setProperty(pv.getProperty());
		}
		return p;
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Collection<Category> findAllCategoryChildren(Shop shop, Category parent) {
		Query query;
		if (parent == null) {
			// query =
			// entityManager.createQuery("select p from Category p where p.view = :view");
			query = entityManager
					.createQuery("select p from Category p where p.parents is empty");

			// query.setParameter("view", view);
		} else {
			// FIXME: trying to do exclusions in sql not easy, so we skip that
			// part for now, better put in cache.
			// invalid => query =
			// entityManager.createQuery("select p from Category p, Taxonomy t, not in(t.excludedCategorys) teg where p.parent = :parent and t = :taxonomy and teg = p");
			query = entityManager
					.createQuery("select distinct p from Category p, in(p.parents) parent where parent.parent = :parent");

			// query.setParameter("taxonomy", taxonomy);
			query.setParameter("parent", parent);
		}
		final List<Category> result = query.getResultList();
		if (result != null) {
			for (Category productGroup : result) {
				// check, because db containsProduct table may be null
				if (productGroup.getContainsProducts() == null) {
					productGroup.setContainsProducts(Boolean.FALSE);
				}
				productGroup.setCatalog(productGroup.getCatalog());
				for (Property property : productGroup.getProperties()) {
					property.setLabels(property.getLabels());
					for (Label lbl : property.getLabels()) {
						lbl.setEnumValue(lbl.getEnumValue());
					}
					property.setEnumValues(property.getEnumValues());
					property.setItem(property.getItem());
				}
				for (PropertyValue value : productGroup.getPropertyValues()) {
					value.setItem(value.getItem());
					value.setProperty(value.getProperty());
				}
			}
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Collection<Category> findAllItemParents(Shop shop, Item item) {
		return findAllItemParents(shop, item, new ArrayList<Category>());
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private Collection<Category> findAllItemParents(Shop shop, Item item, Collection<Category> parents) {
		Query query;
		if (item == null) {
			// query =
			// entityManager.createQuery("select p from Category p where p.view = :view");
			query = entityManager
					.createQuery("select p from Category p where p.parents is empty");

			// query.setParameter("view", view);
		} else {
			// FIXME: trying to do exclusions in sql not easy, so we skip that
			// part for now, better put in cache.
			// invalid => query =
			// entityManager.createQuery("select p from Category p, Taxonomy t, not in(t.excludedCategorys) teg where p.parent = :parent and t = :taxonomy and teg = p");
			query = entityManager
					.createQuery("select distinct p from Category p, in(p.children) child where child.child = :child");

			// query.setParameter("taxonomy", taxonomy);
			query.setParameter("child", item);
		}
		final List<Category> result = query.getResultList();

		for (Category productGroup : result) {
			if (!parents.contains(productGroup)) {
				parents.add(productGroup);
			}
		}
		if (result != null) {
			for (Category productGroup : result) {
				// check, because db containsProduct table may be null
				if (productGroup.getContainsProducts() == null) {
					productGroup.setContainsProducts(Boolean.FALSE);
				}
				productGroup.setCatalog(productGroup.getCatalog());
				for (Property property : productGroup.getProperties()) {
					property.setLabels(property.getLabels());
					for (Label lbl : property.getLabels()) {
						lbl.setEnumValue(lbl.getEnumValue());
					}
					property.setEnumValues(property.getEnumValues());
					property.setItem(property.getItem());
				}
				for (PropertyValue value : productGroup.getPropertyValues()) {
					value.setItem(value.getItem());
					value.setProperty(value.getProperty());
				}
				if (productGroup.getParents() != null) {
					for (ParentChild parentChild : productGroup.getParents()) {
						if (parentChild != null && !parents.contains(parentChild.getParent())) {
							findAllItemParents(shop, parentChild.getParent(), parents);
						}
					}
				}
			}
		}
		return parents;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String publish() {
		// TODO make more generic
		final int port = 80;
		try {
			String urlString = "http://demo.catalogxs.com:" + port + "/flushcache";
			callUrl(urlString);
			urlString = "http://tetterode.catalogxs.com:" + port + "/flushcache";
			callUrl(urlString);
		} catch (MalformedURLException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void callUrl(String urlString) throws MalformedURLException, IOException {
		final URL url = new URL(urlString);
		BufferedReader in = null;

		try {
			in = new BufferedReader(new InputStreamReader(url.openStream()));
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
}

package claro.catalog.impl.importing;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Strings.isNullOrEmpty;
import static easyenterprise.lib.command.CommandValidationException.validate;
import static easyenterprise.lib.util.CollectionUtil.concat;
import static easyenterprise.lib.util.CollectionUtil.notNull;
import static java.util.Collections.singleton;

import javax.persistence.EntityManager;

import claro.catalog.CatalogDao;
import claro.catalog.CatalogDaoService;
import claro.catalog.command.importing.StoreImportSource;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportProducts;
import claro.jpa.importing.ImportProperty;
import claro.jpa.importing.ImportRules;
import claro.jpa.importing.ImportSource;
import claro.jpa.jobs.Frequency;
import claro.jpa.jobs.Job;
import easyenterprise.lib.cloner.Cloner;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.util.CollectionUtil;

public class StoreImportSourceImpl extends StoreImportSource implements CommandImpl<StoreImportSource.Result> {

	private static final long serialVersionUID = 1L;
	
	@Override
	public Result execute() throws CommandException {
		CatalogDao dao = CatalogDaoService.getCatalogDao();
		EntityManager em = dao.getEntityManager();
		validateCommand(em);
		Result result = new Result();
		if (removeImportSource) {
			if (importSource.getId() != null) {
				importSource = em.find(ImportSource.class, importSource.getId());
				em.remove(importSource);
			}
		} else {

			// store changes to import source
			result.importSource = em.merge(importSource);
			
			// make sure there is job instance attached
			fillinJob(result.importSource, em);
			
			// set parent pointers
			for (ImportRules rules : result.importSource.getRules()) {
				rules.setImportSource(result.importSource); 
				if (rules.getImportProducts() != null) {
					rules.getImportProducts().setRules(rules);
					if (rules.getImportProducts().getMatchProperty() != null)
					rules.getImportProducts().setRules(rules);
					for (ImportCategory cat : rules.getImportProducts().getCategories()) {
						cat.setImportProducts(rules.getImportProducts());
					}
					for (ImportProperty prop : rules.getImportProducts().getProperties()) {
						prop.setImportProducts(rules.getImportProducts());
					}
				}
			}
			
			// remove import rules
			for (ImportRules rules : notNull(importRulesToBeRemoved)) {
				ImportProducts importProducts = rules.getImportProducts();
				if (importProducts != null) {
					rules.setImportProducts(null);
					em.remove(em.find(ImportProducts.class, importProducts.getId()));
				}
				result.importSource.getRules().remove(rules);
				em.remove(em.find(ImportRules.class, rules.getId()));
			}
			
			// remove import products
			for (ImportProducts products : notNull(importProductsToBeRemoved)) {
				for (ImportRules rules : result.importSource.getRules()) {
					if (equal(rules.getImportProducts(), products)) {
						rules.setImportProducts(null);
						em.remove(em.find(ImportProducts.class, products.getId()));
					}
				}
			}
			
			// remove categories
			for (ImportCategory cat : notNull(importCategoriesToBeRemoved)) {
				for (ImportRules rules : result.importSource.getRules()) {
					for (ImportProducts products : singleton(rules.getImportProducts())) {
						if (products.getCategories().remove(cat)) {
							em.remove(em.find(ImportCategory.class, cat.getId()));
						}
					}
				}
			}
			
			// remove properties
			for (ImportProperty prop : CollectionUtil.notNull(importPropertiesToBeRemoved)) {
				for (ImportRules rules : result.importSource.getRules()) {
					for (ImportProducts products : singleton(rules.getImportProducts())) {
						if (products.getProperties().remove(prop)) {
							em.remove(em.find(ImportProperty.class, prop.getId()));
						}
					}
				}
			}
			
			// clone result
			result.importSource = Cloner.clone(result.importSource, GetImportSourcesImpl.view);
		}
		return result;
	}
	
	private static void fillinJob(ImportSource importSource, EntityManager em) {
		Job job = importSource.getJob();
		if (job == null) {
			job = new Job();
			importSource.setJob(job);
		}
		if (isNullOrEmpty(job.getName())) {
			job.setName(importSource.getName());
		}
		if (job.getRunFrequency() == null) {
			job.setRunFrequency(Frequency.never);
		}
		if (!em.contains(job)) {
			em.persist(job);
		}
	}
	
	private void validateCommand(EntityManager em) throws CommandValidationException {
		checkValid();
		// rules to be removed should exist
		for (ImportRules rules : notNull(importRulesToBeRemoved)) {
			validate(rules.getId() != null);
		}
		// categories to be removed should exist
		for (ImportCategory cat : notNull(importCategoriesToBeRemoved)) {
			validate(cat.getId() != null);
		}
		// properties to be removed should exist
		for (ImportProperty prop : notNull(importPropertiesToBeRemoved)) {
			validate(prop.getId() != null);
		}
		// all rules should belong to the current import definition
		for (ImportRules rules : concat(importSource.getRules(), importRulesToBeRemoved)) {
			if (rules.getId() != null) {
				ImportRules existing = em.find(ImportRules.class, rules.getId());
				if (existing != null) {
					validate(existing.getImportSource().equals(importSource));
				}
			}
			// all categories should belong to the current rules
			if (rules.getImportProducts() != null) {
				for (ImportCategory cat : rules.getImportProducts().getCategories()) {
					if (cat.getId() != null) {
						ImportCategory existing = em.find(ImportCategory.class, cat.getId());
						if (existing != null) {
							validate(existing.getImportProducts().equals(rules.getImportProducts()));
						}
					}
				}
				// all properties should belong to the current import definition
				for (ImportProperty prop : rules.getImportProducts().getProperties()) {
					if (prop.getId() != null) {
						ImportProperty existing = em.find(ImportProperty.class, prop.getId());
						if (existing != null) {
							validate(existing.getImportProducts().equals(rules.getImportProducts()));
						}
					}
				}
			}
		}
	}
}

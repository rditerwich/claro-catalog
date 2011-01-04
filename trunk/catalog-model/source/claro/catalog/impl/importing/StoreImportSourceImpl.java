package claro.catalog.impl.importing;

import static easyenterprise.lib.command.CommandValidationException.validate;
import static easyenterprise.lib.util.CollectionUtil.notNull;

import javax.persistence.EntityManager;

import claro.catalog.command.importing.StoreImportSource;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportProperty;
import claro.jpa.importing.ImportSource;
import claro.jpa.jobs.Frequency;
import claro.jpa.jobs.Job;
import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.Cloner;
import easyenterprise.lib.cloner.View;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.command.jpa.JpaService;
import easyenterprise.lib.util.CollectionUtil;

public class StoreImportSourceImpl extends StoreImportSource implements CommandImpl<StoreImportSource.Result> {

	private static final long serialVersionUID = 1L;
	private static View view = new BasicView("matchProperty", "categories/category", "properties/property", "job");
	
	@Override
	public Result execute() throws CommandException {
		EntityManager em = JpaService.getEntityManager();
		validateCommand(em);
		Result result = new Result();
		if (remove) {
			em.remove(importSource);
		} else {
			if (skipImportSource) {
				for (ImportCategory cat : CollectionUtil.notNull(importSource.getCategories())) {
						em.merge(cat);
				}
				for (ImportProperty prop : notNull(importSource.getProperties())) {
						em.merge(prop);
				}
				result.importSource = em.find(ImportSource.class, importSource.getId());
			} else {
				result.importSource = em.merge(importSource);
				fillinJob(result.importSource, em);
			}
			for (ImportCategory cat : CollectionUtil.notNull(result.importSource.getCategories())) {
				cat.setImportSource(result.importSource);
			}
			for (ImportProperty prop : CollectionUtil.notNull(result.importSource.getProperties())) {
				prop.setImportSource(result.importSource);
			}
			for (ImportCategory cat : CollectionUtil.notNull(importCategoriesToBeRemoved)) {
				if (result.importSource.getCategories().remove(cat)) {
					em.remove(em.find(ImportCategory.class, cat.getId()));
				}
			}
			for (ImportProperty prop : CollectionUtil.notNull(importPropertiesToBeRemoved)) {
				if (result.importSource.getProperties().remove(prop)) {
					em.remove(em.find(ImportProperty.class, prop.getId()));
				}
			}
		}
		result.importSource = Cloner.clone(result.importSource, view);
		return result;
	}
	
	private static void fillinJob(ImportSource importSource, EntityManager em) {
		Job job = importSource.getJob();
		if (job == null) {
			job = new Job();
			importSource.setJob(job);
		}
		if (job.getName() == null) {
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
		// categories to be removed should exist
		for (ImportCategory cat : CollectionUtil.notNull(importCategoriesToBeRemoved)) {
			validate(cat.getId() != null);
		}
		// all categories should belong to the current import definition
		for (ImportCategory cat : CollectionUtil.concat(importSource.getCategories(), importCategoriesToBeRemoved)) {
			if (cat.getId() != null) {
				ImportCategory existing = em.find(ImportCategory.class, cat.getId());
				if (existing != null) {
					validate(existing.getImportSource().equals(importSource));
				}
			}
		}
		// properties to be removed should exist
		for (ImportProperty prop : CollectionUtil.notNull(importPropertiesToBeRemoved)) {
			validate(prop.getId() != null);
		}
		// all properties should belong to the current import definition
		for (ImportProperty prop : CollectionUtil.concat(importSource.getProperties(), importPropertiesToBeRemoved)) {
			if (prop.getId() != null) {
				ImportProperty existing = em.find(ImportProperty.class, prop.getId());
				if (existing != null) {
					validate(existing.getImportSource().equals(importSource));
				}
			}
		}
		
	}
}

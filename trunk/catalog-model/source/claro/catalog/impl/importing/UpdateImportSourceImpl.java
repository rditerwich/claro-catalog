package claro.catalog.impl.importing;

import static easyenterprise.lib.command.CommandValidationException.validate;
import claro.catalog.CatalogDao;
import claro.catalog.command.importing.UpdateImportSource;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportSource;
import claro.jpa.importing.ImportProperty;
import claro.jpa.jobs.Frequency;
import claro.jpa.jobs.Job;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.command.jpa.JpaService;
import easyenterprise.lib.util.CollectionUtil;

public class UpdateImportSourceImpl extends UpdateImportSource implements CommandImpl<UpdateImportSource.Result> {

	private static final long serialVersionUID = 1L;

	@Override
	public Result execute() throws CommandException {
		CatalogDao dao = new CatalogDao(JpaService.getEntityManager());
		validateCommand(dao);
		Result result = new Result();
		if (remove) {
			dao.getEntityManager().remove(importSource);
		} else {
			if (skipImportSource) {
				result.importSource = new ImportSource();
				result.importSource.setId(importSource.getId());
			} else {
				if (importSource.getId() == null) {
//					dao.getEntityManager().persist(ImportSource);
//					dao.getEntityManager().merge(ImportSource);
				}  
				result.importSource = 
					dao.getEntityManager().merge(importSource);
//				dao.getEntityManager().flush();
			}
			for (ImportCategory cat : CollectionUtil.notNull(importSource.getCategories())) {
				cat.setImportSource(importSource);
				result.importSource.getCategories().add(
					dao.getEntityManager().merge(cat));
			}
			for (ImportProperty prop : CollectionUtil.notNull(importSource.getProperties())) {
				prop.setImportSource(importSource);
				result.importSource.getProperties().add(
					dao.getEntityManager().merge(prop));
			}
			if (result.importSource.getJob() == null) {
				Job job = new Job();
				job.setName(result.importSource.getName());
				job.setRunFrequency(Frequency.never);
				dao.getEntityManager().persist(job);
				result.importSource.setJob(job);
			}
			for (ImportCategory cat : CollectionUtil.notNull(importCategoriesToBeRemoved)) {
				dao.getEntityManager().remove(cat);
			}
			for (ImportProperty prop : CollectionUtil.notNull(importPropertiesToBeRemoved)) {
				dao.getEntityManager().remove(prop);
			}
		}
		return result;
	}
	
	private void validateCommand(CatalogDao dao) throws CommandValidationException {
		checkValid();
		// categories to be removed should exist
		for (ImportCategory cat : CollectionUtil.notNull(importCategoriesToBeRemoved)) {
			validate(cat.getId() != null);
		}
		// all categories should belong to the current import definition
		for (ImportCategory cat : CollectionUtil.concat(importSource.getCategories(), importCategoriesToBeRemoved)) {
			if (cat.getId() != null) {
				ImportCategory existing = dao.getEntityManager().find(ImportCategory.class, cat.getId());
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
				ImportProperty existing = dao.getEntityManager().find(ImportProperty.class, prop.getId());
				if (existing != null) {
					validate(existing.getImportSource().equals(importSource));
				}
			}
		}
		
	}
}

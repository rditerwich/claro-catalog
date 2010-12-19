package claro.catalog.impl.importing;

import static easyenterprise.lib.command.CommandValidationException.validate;
import claro.catalog.CatalogDao;
import claro.catalog.command.importing.UpdateImportDefinition;
import claro.jpa.importing.ImportCategory;
import claro.jpa.importing.ImportDefinition;
import claro.jpa.importing.ImportProperty;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.CommandValidationException;
import easyenterprise.lib.command.jpa.JpaService;
import easyenterprise.lib.util.CollectionUtil;

public class UpdateImportDefinitionImpl extends UpdateImportDefinition implements CommandImpl<UpdateImportDefinition.Result> {

	@Override
	public Result execute() throws CommandException {
		CatalogDao dao = new CatalogDao(JpaService.getEntityManager());
		validateCommand(dao);
		Result result = new Result();
		if (remove) {
			dao.getEntityManager().remove(importDefinition);
		} else {
			if (skipImportDefinition) {
				result.importDefinition = new ImportDefinition();
				result.importDefinition.setId(importDefinition.getId());
			} else {
				if (importDefinition.getId() == null) {
//					dao.getEntityManager().persist(importDefinition);
//					dao.getEntityManager().merge(importDefinition);
				}  
				result.importDefinition = 
					dao.getEntityManager().merge(importDefinition);
//				dao.getEntityManager().flush();
			}
			for (ImportCategory cat : CollectionUtil.notNull(importDefinition.getCategories())) {
				cat.setImportDefinition(importDefinition);
				result.importDefinition.getCategories().add(
					dao.getEntityManager().merge(cat));
			}
			for (ImportProperty prop : CollectionUtil.notNull(importDefinition.getProperties())) {
				prop.setImportDefinition(importDefinition);
				result.importDefinition.getProperties().add(
					dao.getEntityManager().merge(prop));
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
		for (ImportCategory cat : CollectionUtil.concat(importDefinition.getCategories(), importCategoriesToBeRemoved)) {
			if (cat.getId() != null) {
				ImportCategory existing = dao.getEntityManager().find(ImportCategory.class, cat.getId());
				if (existing != null) {
					validate(existing.getImportDefinition().equals(importDefinition));
				}
			}
		}
		// properties to be removed should exist
		for (ImportProperty prop : CollectionUtil.notNull(importPropertiesToBeRemoved)) {
			validate(prop.getId() != null);
		}
		// all properties should belong to the current import definition
		for (ImportProperty prop : CollectionUtil.concat(importDefinition.getProperties(), importPropertiesToBeRemoved)) {
			if (prop.getId() != null) {
				ImportProperty existing = dao.getEntityManager().find(ImportProperty.class, prop.getId());
				if (existing != null) {
					validate(existing.getImportDefinition().equals(importDefinition));
				}
			}
		}
		
	}
}

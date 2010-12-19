package claro.catalog.impl.importing;

import static easyenterprise.lib.command.CommandValidationException.validate;
import claro.catalog.command.importing.UpdateImportDefinition;
import claro.catalog.model.CatalogDao;
import claro.jpa.importing.ImportCategory;
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
		if (remove) {
			dao.getEntityManager().remove(importDefinition);
		} else {
			for (ImportCategory cat : CollectionUtil.notNull(importDefinition.getCategories())) {
				cat.setImportDefinition(importDefinition);
				dao.getEntityManager().merge(cat);
			}
			for (ImportProperty prop : CollectionUtil.notNull(importDefinition.getProperties())) {
				prop.setImportDefinition(importDefinition);
				dao.getEntityManager().merge(prop);
			}
			if (!skipImportDefinition) {
				dao.getEntityManager().merge(importDefinition);
			}
			for (ImportCategory cat : CollectionUtil.notNull(importCategoriesToBeRemoved)) {
				dao.getEntityManager().remove(cat);
			}
			for (ImportProperty prop : CollectionUtil.notNull(importPropertiesToBeRemoved)) {
				dao.getEntityManager().remove(prop);
			}
		}
		return new Result();
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

package claro.catalog.impl.importing;

import claro.catalog.command.importing.UpdateImportDefinition;
import claro.catalog.model.CatalogDao;
import claro.jpa.importing.ImportDefinition;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.jpa.JpaService;

public class UpdateImportDefinitionImpl extends UpdateImportDefinition implements CommandImpl<UpdateImportDefinition.Result> {

	CatalogDao dao = new CatalogDao(JpaService.getEntityManager());
	
	@Override
	public Result execute() throws CommandException {
//		checkValid();
//		if (remove) {
//		} else {
//			if (skipImportDefinition) {
//				for (ImportCategory cat : CollectionUtil.notNull(importDefinition.))
//			}
//		}
//		else 
//		if (!remove && !ski) {
//			
//		}
//		if (importDefinition != null) {
//		}
//		if (importCategory != null) {
//			ImportDefinition definition = dao.findOrCreate(ImportDefinition.class, importCategory.getFirst());
//			
//		}
		return new Result();
	}
	
	public void updateImportDefinition() {
		ImportDefinition definition = dao.merge(importDefinition);
	}
	

}

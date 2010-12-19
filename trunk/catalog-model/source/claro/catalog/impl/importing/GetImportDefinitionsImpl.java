package claro.catalog.impl.importing;

import java.util.Collections;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import claro.catalog.command.importing.GetImportDefinitions;
import claro.catalog.command.importing.GetImportDefinitionsResult;
import claro.catalog.model.CatalogDao;
import claro.jpa.importing.ImportDefinition;
import claro.jpa.importing.ImportDefinition_;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.jpa.JpaService;

public class GetImportDefinitionsImpl extends GetImportDefinitions implements CommandImpl<GetImportDefinitionsResult>{

	@Override
	public GetImportDefinitionsResult execute() throws CommandException {
		checkValid();
		
		Result result = new Result();
		CatalogDao dao = new CatalogDao(JpaService.getEntityManager());
		if (importDefinitionId != null) {
			result.importDefinitions = Collections.singletonList(dao.getImportDefinitionById(importDefinitionId));
		} else if (importDefinitionName != null) {
			result.importDefinitions = dao.getImportDefinitionsByName(importDefinitionName, paging);
		} else {
			dao.getImportDefinitions(paging);
		}
		
		CriteriaBuilder builder = dao.getEntityManager().getCriteriaBuilder();
		
		CriteriaQuery<ImportDefinition> crit = builder.createQuery(ImportDefinition.class);
		ParameterExpression<String> nameParam = builder.parameter(String.class);
		Root<ImportDefinition> importDefinition = crit.from(ImportDefinition.class);
		Path<String> name = importDefinition.get(ImportDefinition_.name);
		crit.select(importDefinition).where(builder.like(name, nameParam)).where(builder.equal(name, nameParam));
		
		
		result.importDefinitions = dao.getEntityManager().createQuery(crit).setParameter(nameParam, "%" + importDefinitionName + "%").getResultList();
		System.out.println(result.importDefinitions);
//		dao.getImportDefinitions(paging);
		
		return new GetImportDefinitionsResult();
	}

}

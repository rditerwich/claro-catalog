package claro.catalog.impl.importing;

import java.util.Collections;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import claro.catalog.CatalogDao;
import claro.catalog.command.importing.GetImportSources;
import claro.jpa.importing.ImportSource;
import claro.jpa.importing.ImportSource_;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandImpl;
import easyenterprise.lib.command.jpa.JpaService;

public class GetImportSourcesImpl extends GetImportSources implements CommandImpl<GetImportSources.Result>{

	private static final long serialVersionUID = 1L;

	@Override
	public Result execute() throws CommandException {
		checkValid();
		
		Result result = new Result();
		CatalogDao dao = new CatalogDao(JpaService.getEntityManager());
		if (ImportSourceId != null) {
			result.ImportSources = Collections.singletonList(dao.getImportSourceById(ImportSourceId));
		} else if (ImportSourceName != null) {
			result.ImportSources = dao.getImportSourcesByName(ImportSourceName, paging);
		} else {
			dao.getImportSources(paging);
		}
		
		CriteriaBuilder builder = dao.getEntityManager().getCriteriaBuilder();
		
		CriteriaQuery<ImportSource> crit = builder.createQuery(ImportSource.class);
		ParameterExpression<String> nameParam = builder.parameter(String.class);
		Root<ImportSource> ImportSource = crit.from(ImportSource.class);
		Path<String> name = ImportSource.get(ImportSource_.name);
		crit.select(ImportSource).where(builder.like(name, nameParam)).where(builder.equal(name, nameParam));
		
		
		result.ImportSources = dao.getEntityManager().createQuery(crit).setParameter(nameParam, "%" + ImportSourceName + "%").getResultList();
		System.out.println(result.ImportSources);
//		dao.getImportSources(paging);
		
		return result;
	}

}

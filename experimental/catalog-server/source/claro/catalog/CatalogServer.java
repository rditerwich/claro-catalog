package claro.catalog;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import claro.jpa.catalog.Catalog;
import easyenterprise.lib.cloner.BasicView;
import easyenterprise.lib.cloner.Printer;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandExecutor;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandServer;
import easyenterprise.lib.command.RegisteredCommands;
import easyenterprise.lib.server.HttpRequestService;
import easyenterprise.lib.util.DBScript;
import gwtupload.server.UploadServlet;

public class CatalogServer implements CommandExecutor {

	private final RegisteredCommands registeredCommands = RegisteredCommands.create(
			new RegisteredCatalogServerCommands());

	private final CatalogDao dao;
	private final CommandExecutor executor;

	private final CatalogDaoService catalogDaoService;
	private final CatalogModelService catalogModelService;
	
	public CatalogServer(ServletConfig config) throws SQLException {
		this(collectProperties(config));
	}

	public CatalogServer(Map<String, String> properties) throws SQLException {
		this.dao = new CatalogDao(properties);
		CommandServer server = new CommandServer(registeredCommands);
		this.catalogDaoService = new CatalogDaoService(dao, server);
		this.catalogModelService = new CatalogModelService(this, catalogDaoService);
		executor = catalogModelService;
		createDatabase();
	}
	
	public static FileItem getUploadedFile(String fieldName) {
		HttpServletRequest request = HttpRequestService.getRequest();
		if (request != null) {
			return UploadServlet.findItemByFieldName(UploadServlet.getSessionFileItems(request), fieldName);
		}
		else 
			return null;
	}
	
	@Override
	public <T extends CommandResult, C extends Command<T>> T execute(C command) throws CommandException {
		System.out.println("Command: " + Printer.print(command, BasicView.DEFAULT));
		return executor.execute(command);
	}
	
	public CatalogDao getCatalogDao() {
		return dao;
	}
	
	protected void createDatabase() throws SQLException {
		EntityManager em = dao.getEntityManagerFactory().createEntityManager();
		try {
			DBScript script = new DBScript("DROP SCHEMA catalog CASCADE");
			script = new DBScript(Catalog.class.getResourceAsStream("/UpdateSchema.sql"));
			script.execute(em);
			script = new DBScript(
				"CREATE INDEX propertyvalue_by_id on catalog.propertyvalue (id);" +
				"CREATE INDEX propertyvalue_by_item_id on catalog.propertyvalue (item_id);");
			script.execute(em);
		} catch (Throwable e) {
			throw new SQLException("Couldn't create catalog schema. Did you forget to create the catalog database?", e);
		} finally {
			em.close();
		}
	}
	
	private static Map<String, String> collectProperties(ServletConfig config) {
		Map<String, String> properties = new HashMap<String, String>();
		
		for (@SuppressWarnings("unchecked")
		Enumeration<String> e = config.getInitParameterNames(); e.hasMoreElements(); ) {
			String name = e.nextElement();
			properties.put(name, config.getInitParameter(name));
		}
		properties.putAll(System.getenv());
		for (String name : System.getProperties().stringPropertyNames()) {
			properties.put(name, System.getProperty(name));
		}
		return properties;
	}
}

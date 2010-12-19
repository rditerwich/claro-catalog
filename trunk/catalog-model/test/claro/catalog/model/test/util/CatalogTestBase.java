package claro.catalog.model.test.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import claro.catalog.CatalogDao;
import claro.catalog.CatalogServer;
import claro.catalog.model.CatalogModel;
import claro.jpa.catalog.Catalog;
import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.util.DBScript;

public class CatalogTestBase {

	public static final String TEST_DATABASE_NAME = "catalog-test";
	public static final Long TEST_CATALOG_ID = -99l;
	
	private static Connection connection;
	private static CatalogServer server;
	private static EntityManagerFactory entityManagerFactory;
	private static boolean databaseCreated;

	private EntityManager entityManager;

	private CatalogModel catalogModel;

	protected static Map<String, String> getProperties() {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
		properties.put("javax.persistence.jdbc.url", "jdbc:postgresql:" + TEST_DATABASE_NAME);
		properties.put("javax.persistence.jdbc.user", "postgres");;
		properties.put("javax.persistence.jdbc.password", "postgres");
		properties.put("eclipselink.ddl-generation", "none");
		properties.put("eclipselink.ddl-generation.output-mode", "none");
		properties.put("eclipselink.logging.level", "FINE");
		properties.putAll(System.getenv());
		for (String name : System.getProperties().stringPropertyNames()) {
			properties.put(name, System.getProperty(name));
		}
		return properties;
	}
	
	protected static void ensureDatabaseCreated() throws SQLException {
		if (!databaseCreated) {
			try {
				DBScript script = new DBScript("DROP SCHEMA catalog CASCADE");
				script.execute(getConnection());
				script = new DBScript(Catalog.class.getResourceAsStream("/CreateSchema.sql"));
				script.execute(getConnection()).assertSuccess();
				databaseCreated = true;
			} catch (Throwable e) {
				throw new SQLException("Couldn't create catalog schema. Did you forget to create the empty test database '" + TEST_DATABASE_NAME + "'?", e);
			}
		}
	}
	
  protected static Connection getConnection() throws SQLException {
  	if (connection == null) {
	  	Map<String, String> properties = getProperties();
	    try {
	       Class.forName(properties.get("javax.persistence.jdbc.driver")); 
	       connection = DriverManager.getConnection(
	      		 properties.get("javax.persistence.jdbc.url"), 
	      		 properties.get("javax.persistence.jdbc.user"), 
	      		 properties.get("javax.persistence.jdbc.password"));
	    }
	    catch(ClassNotFoundException e) {
	    	throw new RuntimeException(e);
	    }
  	}
    return connection;
 }
	
	protected static CatalogServer getServer() throws SQLException {
		if (server == null) {
			ensureDatabaseCreated();
			server = new CatalogServer(getProperties());
		}
		return server;
  }
	
	protected static <T extends CommandResult> T executeCommand(Command<T> command) throws CommandException, SQLException {
		return getServer().execute(command);
	}
	
	private static EntityManagerFactory getEntityManagerFactory() throws SQLException {
		if (entityManagerFactory == null) {
			entityManagerFactory = Persistence.createEntityManagerFactory("claro.jpa.catalog", getProperties());
		}
		return entityManagerFactory;
	}
	
	protected EntityManager getEntityManager() throws SQLException {
		if (entityManager == null) {
			ensureDatabaseCreated();
			entityManager = getEntityManagerFactory().createEntityManager();
			entityManager.getTransaction().begin();
		}
		return entityManager;
	}
	
	protected CatalogDao getCatalogDao() throws SQLException {
		return new CatalogDao(getEntityManager());
	}
	
	protected CatalogModel getCatalogModel() throws SQLException {
		if (catalogModel == null) {
			CatalogModel.startOperation(getCatalogDao());
			catalogModel = new CatalogModel(TEST_CATALOG_ID);
		}
		return catalogModel;
	}
	
	@BeforeClass
	public static void initializeClass() {
		server = null;
		entityManagerFactory = null;
	}
	
	@Before
	public void initializeTest() {
		entityManager = null;
		catalogModel = null;
	}
	
	@After
	public void finish() {
		if (catalogModel != null) { 
			CatalogModel.endOperation();
		}
		if (entityManager != null) {
			if (entityManager.getTransaction().isActive()) {
				entityManager.getTransaction().commit();
			}
			entityManager.close();
		}
	}
}

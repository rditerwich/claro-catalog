package claro.catalog;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletConfig;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.CommandServer;
import easyenterprise.lib.command.CommandExecutor;
import easyenterprise.lib.command.RegisteredCommands;
import easyenterprise.lib.command.jpa.JpaService;

public class CatalogServer implements CommandExecutor {

	private final RegisteredCommands registeredCommands = RegisteredCommands.create(
			new RegisteredModelCommands());

	private final EntityManagerFactory entityManagerFactory;
	private final CommandExecutor executor;
	
	public CatalogServer(ServletConfig config) {
		this(collectProperties(config));
	}

	public CatalogServer(Map<String, String> properties) {
		entityManagerFactory = Persistence.createEntityManagerFactory("catalog", properties);
		CommandServer server = new CommandServer(registeredCommands);
		executor = new JpaService(new CatalogModelService(server), entityManagerFactory);
	}
	
	@Override
	public <T extends CommandResult, C extends Command<T>> T execute(C command) throws CommandException {
		return executor.execute(command);
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

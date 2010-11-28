package claro.catalog.manager.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import claro.catalog.CatalogServer;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.gwt.GwtCommandService;

public class CatalogManagerServlet extends RemoteServiceServlet implements GwtCommandService {

	private static final long serialVersionUID = 1L;

	private CatalogServer server;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		server = new CatalogServer(config);
	}
	
	@Override
	public <T extends CommandResult> T execute(Command<T> command) throws CommandException {
		try {
			return server.execute(command);
		} catch (CommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
	
}

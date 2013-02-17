package claro.catalog.manager.server;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import claro.catalog.CatalogServer;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.CommandException;
import easyenterprise.lib.command.CommandResult;
import easyenterprise.lib.command.gwt.GwtCommandService;
import easyenterprise.lib.server.HttpRequestService;

public class CatalogManagerServlet extends RemoteServiceServlet implements GwtCommandService {

	private static final long serialVersionUID = 1L;

	private CatalogServer server;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		org.eclipse.persistence.Version.getVersion();
		try {
			server = new CatalogServer(config);
		} catch (SQLException e) {
			throw new ServletException(e);
		}
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpRequestService.setRequest(request);
		super.service(request, response);
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

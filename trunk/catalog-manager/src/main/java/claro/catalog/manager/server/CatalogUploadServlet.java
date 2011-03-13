package claro.catalog.manager.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gwtupload.server.UploadServlet;

public class CatalogUploadServlet extends UploadServlet {

	private static final long serialVersionUID = 1L;

	
	@Override
	protected synchronized void service(HttpServletRequest request, HttpServletResponse arg1)
			throws ServletException, IOException {
		super.service(request, arg1);
	}
}

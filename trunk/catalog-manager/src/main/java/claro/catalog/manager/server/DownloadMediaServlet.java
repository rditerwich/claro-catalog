package claro.catalog.manager.server;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import claro.catalog.CatalogDao;
import claro.jpa.catalog.PropertyValue;

public class DownloadMediaServlet extends HttpServlet {
    private static final long serialVersionUID = 0L;

    private CatalogDao dao;


	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		org.eclipse.persistence.Version.getVersion();
		dao = new CatalogDao(collectProperties(config));
	}

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String queryString = req.getQueryString();

        if (queryString == null) {
            return;
        }
        // Build response
        resp.setHeader("Expires", "now plus 1 year");  // TODO is this right?  Check whether updates are visible...
        final FileData fileData = retrieveFile(req, resp);

        if (fileData == null) {
            return;
        }
        if (fileData.contentType != null) {
            resp.setContentType(fileData.contentType);
        }
        
        if (fileData.content != null) {
	        resp.setContentLength(fileData.content.length);
	        resp.getOutputStream().write(fileData.content);
        }
    }

    private FileData retrieveFile(HttpServletRequest req, HttpServletResponse resp) {
        final FileData fd = new FileData();

        try {
            final Long pvId = Long.valueOf(req.getParameter("pvId"));
            final PropertyValue pv = dao.getEntityManager().find(PropertyValue.class, pvId);

            if (pv != null) {
                resp.addHeader("Content-disposition", "attachment; filename=" + pv.getStringValue());
                fd.content = pv.getMediaValue();
                fd.contentType = pv.getMimeType();
            } else {
                fd.content = ("Could not find media file in database (id:" + pvId +")").getBytes();
                fd.contentType = "image/png";
            }
        } catch (Exception e) {
            fd.content = "Error finding media file in database ".getBytes();
            fd.contentType = "image/png";
        }
        return fd;
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

    private static class FileData {
        byte[] content;
        String contentType;
    }
}
package jetty.start;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;


public class StartJetty {

	public static void main(String[] args) throws Exception {

		String webroot = args[0];
		String port = "8080";
		if (args.length > 1) {
			port = args[1];
		}
//		String persistenceUnit = args[1];

		// set default entitymanager properties
		System.setProperty("entitymanager.eclipselink.jdbc.driver", "org.postgresql.Driver");
		System.setProperty("entitymanager.eclipselink.jdbc.url", "jdbc:postgresql:postgres");
		System.setProperty("entitymanager.eclipselink.jdbc.user", "postgres");
		System.setProperty("entitymanager.eclipselink.jdbc.password", "Tpacoh18");

		// warm up the entity manager (optional)
//		Persistence.createEntityManagerFactory(persistenceUnit, properties).createEntityManager();
		
		Server server = new Server(Integer.parseInt(port));
        WebAppContext context = new WebAppContext();
//        context.setDescriptor(webroot + "/WEB-INF/web.xml");
        context.setResourceBase(webroot);
        context.setContextPath("/");
        context.setParentLoaderPriority(true);
        server.setHandler(context);
 
        server.start();
        server.join();
	}
}

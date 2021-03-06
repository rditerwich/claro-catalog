package easyenterprise.lib.server;

import com.google.gwt.user.client.rpc.RemoteService;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.openid4java.util.HttpClientFactory;
import org.openid4java.util.ProxyProperties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: aviadbendov
 * Date: Apr 18, 2008
 * Time: 9:27:03 PM
 *
 * A servlet incharge of the OpenID authentication process, from authentication to verification.
 */
@SuppressWarnings({"GwtInconsistentSerializableClass"})
public final class OpenIdServlet extends HttpServlet implements RemoteService {

    public static interface Callback {
        String getOpenIdServletURL();
        String getLoginURL();

        String createUniqueIdForUser(String user);
        void saveIdentifierForUniqueId(String uniqueId, Identifier identifier);
    }

    public static Callback callback;

    public static final String authParameter = "app-openid-auth";
    public static final String nameParameter = "app-openid-name";
    public static final String openIdCookieName = "app-openid-identifier";
    public static final String uniqueIdCookieName = "app-openid-uniqueid";

    private final ConsumerManager manager;

    public static Callback getCallback() {
    	if (callback == null) {
    		callback = new Callback() {

				@Override
				public String getOpenIdServletURL() {
					return "http://localhost:8888/catalogmanager/authentication";
				}

				@Override
				public String getLoginURL() {
					return "http://127.0.0.1:8888/CatalogManager.html?gwt.codesvr=127.0.0.1:9997";
				}

				@Override
				public String createUniqueIdForUser(String user) {
					System.out.println("createUniqueId for " + user);
					return "reinier.bos@gmail.com";
				}

				@Override
				public void saveIdentifierForUniqueId(String uniqueId,
						Identifier identifier) {
					System.out.println("save identifier for UniqueId  " + uniqueId + " identifier: " + identifier);
					// TODO Auto-generated method stub
					
				}
    			
    		};
    	}
    	return callback;
    }
    public OpenIdServlet() {
        try {
            manager = new ConsumerManager();
        } catch (ConsumerException e) {
            throw new RuntimeException("Error creating consumer manager", e);
        }
    }

    /**
     * <b>Note</b>: In a normal servlet environment, this method would probably
     * redirect the response itself. However, since GWT servlets do not allow for
     * such behavior, a path to this servlet is returned and the redirection is done
     * on the client side.
     * @param openIdName The OpenID identifier the user provided.
     * @return The URL the browser should be redirected to.
     */
    public static String getAuthenticationURL(String openIdName) {
        // This is where a redirect for the response was supposed to occur; however, since GWT doesn't allow that
        // on responses coming from a GWT servlet, only a redirect via the web page is made.

        return MessageFormat.format("{3}?{1}=true&{2}={0}", openIdName, authParameter, nameParameter, getCallback().getOpenIdServletURL());
    }

    /**
     * Returns the unique cookie and the OpenID identifier saved on the user's
     * browser.
     *
     * The servlet should be the only entity accessing and manipulating these cookies,
     * so it is also in-charge of fetching them when needed.
     * @param request The user's request to extract the cookies from.
     * @return Array containing { UniqueId, OpenID-Identifier }
     */
    public static String[] getRequestUserInfo(HttpServletRequest request) {
        return new String[] {
                HttpCookies.getCookieValue(request, openIdCookieName),
                HttpCookies.getCookieValue(request, uniqueIdCookieName)
        };
    }

    /**
     * Implements the GET method by either sending it to an OpenID authentication or verification
     * mechanism.
     *
     * Checks the parameters of the GET method; if they contain the "authParameter" set to true,
     * the authentication process is performed. If not, the verification process is performed
     * (the parameters in the verification process are controlled by the OpenID provider).
     *
     * @param request The request sent to the servlet. Might come from the GWT application or
     * the OpenID provider.
     *
     * @param response The response sent to the user. Generally used to redirect the user to the next
     * step in the OpenID process.
     *
     * @throws ServletException Usually wrapping an OpenID process exception.
     * @throws IOException Usually when redirection could not be performed.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	if (true) {
    		ProxyProperties proxyProps = new ProxyProperties() {
    			@Override
    			public String getUserName() {
    				return "";
    			}
    			@Override
    			public String getPassword() {
    				return "";
    			}
    		};
			proxyProps.setProxyHostName("nl-proxy.groupinfra.com");
			proxyProps.setProxyPort(3128);
			HttpClientFactory.setProxyProperties(proxyProps);
    	}
        if (Boolean.valueOf(request.getParameter(authParameter))) {
            authenticate(request, response);
        } else {
            verify(request, response);

        }
    }

    /**
     * Discovers the OpenID provider from the provided user string, and starts an authentication process
     * against it.
     *
     * This is done in three steps:
     * <ol>
     * <li>Discover the OpenID provider URL</li>
     * <li>Create a unique cookie and send it to the user, so that after
     * the provider redirects the user back we'll know what to do with him.</li>
     * <li>Redirect the user to the provider URL, supplying the verification URL as a return point.</li>
     * </ol>
     *
     * @param request The request for the OpenID authentication.
     * @param response The response, used to redirect the user.
     *
     * @throws IOException Occurs when a redirection is not successful.
     * @throws ServletException Wrapping an OpenID exception.
     */
    private void authenticate(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        final String loginString = request.getParameter(nameParameter);

        System.out.println("loginString: " + loginString);
        try {
            HttpCookies.resetCookie(request, response, uniqueIdCookieName);
            HttpCookies.resetCookie(request, response, openIdCookieName);

            String uuid = getCallback().createUniqueIdForUser(loginString);
            HttpCookies.setCookie(request, response, uniqueIdCookieName, uuid);

            // perform discovery on the user-supplied identifier
            List<DiscoveryInformation> discoveries = manager.discover(loginString);
            
            for (DiscoveryInformation info : discoveries) {
            	System.out.println("discovery: " + info);
            }

            // attempt to associate with the OpenID provider
            // and retrieve one service endpoint for authentication
            DiscoveryInformation discovered = manager.associate(discoveries);

            // obtain a AuthRequest message to be sent to the OpenID provider
            AuthRequest authReq = manager.authenticate(discovered, getCallback().getOpenIdServletURL(), null);
            authReq.setIdentity("http://specs.openid.net/auth/2.0/identifier_select");
            
            addParameters(authReq, loginString);

            // redirect to OpenID for authentication
            String destinationUrl = authReq.getDestinationUrl(true);
            System.out.println("redirecting to " + destinationUrl);
			response.sendRedirect(destinationUrl);
        }
        catch (OpenIDException e) {
            throw new ServletException("Login string probably caused an error. loginString = " + loginString, e);
        }
    }

    private void addParameters(AuthRequest authReq, String loginString) throws MessageException {
    	String yahooEndpoint = "https://me.yahoo.com";
    	String googleEndpoint = "https://www.google.com/accounts/o8/id";

		FetchRequest fetch = FetchRequest.createFetchRequest();
		if (loginString.equals(googleEndpoint)) {
			fetch.addAttribute("email", "http://axschema.org/contact/email", true);
			fetch.addAttribute("firstName", "http://axschema.org/namePerson/first", true);
			fetch.addAttribute("lastName", "http://axschema.org/namePerson/last", true);
			fetch.addAttribute("language",  "http://axschema.org/pref/language", true);
		} else if (loginString.equals(yahooEndpoint)) {
			fetch.addAttribute("email", "http://axschema.org/contact/email", true);
			fetch.addAttribute("fullname", "http://axschema.org/namePerson", true);
		} else { // works for myOpenID
			fetch.addAttribute("fullname", "http://schema.openid.net/namePerson", true);
			fetch.addAttribute("email", "http://schema.openid.net/contact/email", true);
		}

		if (!fetch.getAttributes().isEmpty()) {
			authReq.addExtension(fetch);
		}
		
	}
	/**
     * Checks the response received by the OpenID provider, and saves the user identifier for later use
     * if the authentication was sucesssful.
     *
     * <b>Note</b>: While confusing, the OpenID provider's response is in fact encapsulated within
     * the request; this is because it is the provider who requested the page, and sent the response
     * as parameters.
     *
     * This is done in three steps:
     * <ol>
     * <li>Verify the OpenID resposne.</li>
     * <li>If verification was successful, retrieve the OpenID identifier of the user
     * and save it for later use.</li>
     * <li>Redirect the user back to the main page of the application, together with a cookie containing
     * his OpneID identifier.</li>
     * </ol>

     * @param request The request, containing the OpenID provider's response as parameters.
     * @param response The response, used to redirect the user back to the application page.
     * @throws IOException Occurs when redirection is not successful.
     * @throws ServletException Wrapping an OpenID exception.
     */
    private void verify(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {

            // extract the parameters from the authentication response
            // (which comes in as a HTTP request from the OpenID provider)
            ParameterList responseParams =
                    new ParameterList(request.getParameterMap());

            // extract the receiving URL from the HTTP request
            StringBuffer receivingURL = request.getRequestURL();
            String queryString = request.getQueryString();
            if (queryString != null && queryString.length() > 0)
                receivingURL.append("?").append(request.getQueryString());

            // verify the response; ConsumerManager needs to be the same
            // (static) instance used to place the authentication request
            VerificationResult verification = manager.verify(
                    receivingURL.toString(),
                    responseParams, null);

            // examine the verification result and extract the verified identifier
            Identifier id = verification.getVerifiedId();
            if (id != null) {
                HttpCookies.setCookie(request, response, openIdCookieName, id.getIdentifier());

                getCallback().saveIdentifierForUniqueId(HttpCookies.getCookieValue(request, uniqueIdCookieName), id);
                
                // Get some extra info:
                AuthSuccess authSuccess =
                    (AuthSuccess) verification.getAuthResponse();

            if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX))
            {
                FetchResponse fetchResp = (FetchResponse) authSuccess
                        .getExtension(AxMessage.OPENID_NS_AX);

                List emails = fetchResp.getAttributeValues("email");
                for (Iterator it = emails.iterator(); it.hasNext();) {
					String email = (String) it.next();
					System.out.println("email " + email);
				}
            }

            }

            response.sendRedirect(getCallback().getLoginURL());
        }
        catch (OpenIDException e) {
            throw new ServletException("Could not verify identity", e);
        }
    }

}
package com.ibm.websphere.xs.sample.gettingstarted;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.websphere.objectgrid.CatalogDomainInfo;
import com.ibm.websphere.objectgrid.CatalogDomainManager;
import com.ibm.websphere.objectgrid.ClientClusterContext;
import com.ibm.websphere.objectgrid.DuplicateKeyException;
import com.ibm.websphere.objectgrid.KeyNotFoundException;
import com.ibm.websphere.objectgrid.ObjectGrid;
import com.ibm.websphere.objectgrid.ObjectGridException;
import com.ibm.websphere.objectgrid.ObjectGridManagerFactory;
import com.ibm.websphere.objectgrid.ObjectGridRuntimeException;
import com.ibm.websphere.objectgrid.ObjectMap;
import com.ibm.websphere.objectgrid.Session;
import com.ibm.websphere.objectgrid.server.ServerFactory;

/**
 * COPYRIGHT LICENSE:  This information contains sample code provided in source code form. 
 * You may copy, modify, and distribute these sample programs in any form without payment to I
 * BM for the purposes of developing, using, marketing or distributing application programs conforming 
 * to the application programming interface for the operating platform for which the sample code is written. 
 * Notwithstanding anything to the contrary,  IBM PROVIDES THE SAMPLE SOURCE CODE ON AN "AS IS" BASIS AND IBM 
 * DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, ANY IMPLIED WARRANTIES OR 
 * CONDITIONS OF MERCHANTABILITY, SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND ANY WARRANTY 
 * OR CONDITION OF NON-INFRINGEMENT.  IBM SHALL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL OR 
 * CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR OPERATION OF THE SAMPLE SOURCE CODE.  IBM HAS NO OBLIGATION TO 
 * PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS OR MODIFICATIONS TO THE SAMPLE SOURCE CODE.  
 *
 * This sample program is provided AS IS and may be used, executed, copied and
 * modified without royalty payment by customer (a) for its own instruction and
 * study, (b) in order to develop applications designed to run with an IBM
 * WebSphere product, either for customer's own internal use or for redistribution
 * by customer, as part of such an application, in customer's own products. 
 * 
 * 5724-X67, 5655-V66 (C) COPYRIGHT International Business Machines Corp. 2012
 * All Rights Reserved * Licensed Materials - Property of IBM
 */

/**
 * This servlet is a simple ObjectGrid client that connects to a remote
 * or embedded ObjectGrid and allows CRUD operations.
 */
public class WXSClientServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * The client ObjectGrid.  If not null, this servlet is connected to
	 * a grid.
	 */
	private ObjectGrid grid;
	private CatalogDomainManager cdm;
	
	private static final String EOL = "<BR>";

	/**
	 * Default constructor.
	 */
	public WXSClientServlet() {
		super();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {
		super.init();

	}

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	public void destroy() {
		super.destroy();
		// When the servlet is destroyed, also destroy the grid connection
		disconnectClient();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (!resp.isCommitted()) {
			resp.setHeader("Pragma", "No-cache");
			resp.setHeader("Cache-Control", "no-cache");
			resp.setDateHeader("Expires", 0);
			resp.setContentType("text/html");
		}

		OutputStream os = resp.getOutputStream();
		PrintWriter pw = new PrintWriter(os);

		String connect = req.getParameter("c");
		if (connect == null || connect.trim().length()==0){
			connect = "panelEndpoints";
		}
		
		printHeader(pw, connect);

		try {

			// Get the catalog service end points.
			// The format is <host>:<port> when connecting to a remote catalog service.
			// The end points can be null when running integrated WebSphere Application Server.
			// The catalog service is either running in the application server process for a non-federated
			// server, or the deployment manager.  This can be overridden by specifying a cell property
			// or server property named:  catalog.services.cluster=<end points>

			String gridName = null;
			String gridNameInput = req.getParameter("g");
			gridName = gridNameInput;
			if (gridNameInput == null || gridNameInput.trim().length()==0) {
				// For getting the started sample the grid name is Grid
				gridName = "Grid";
			}
			
			String mapName = null;
			String mapNameInput = req.getParameter("m");	
			mapName = mapNameInput;	
			if (mapNameInput == null || mapNameInput.trim().length()==0){
				// For getting the started sample the map name is Map1
				mapName = "Map1";
			}
			
			// grab the catalog domain ids that are available
			cdm = ObjectGridManagerFactory.getObjectGridManager().getCatalogDomainManager();
			Collection<CatalogDomainInfo> domainIds = null;
			CatalogDomainInfo defaultDomain = null;
			
			if (cdm != null) {
				domainIds = cdm.getDomainInfos();
				defaultDomain = cdm.getDefaultDomainInfo();
			}
			 			
			String c = req.getParameter("c");
			String cep = null; 
			String domainId = null;
			String cepInput = req.getParameter("cep");
			if (cepInput != null) {
				cep = cepInput;
			}
			
			if ("panelDomain".equals(c)) {
				// use the domainId to connect
				String domainIdInput = req.getParameter("d");
				domainId = domainIdInput;
			}
			else {
				// use the cep to connect
				if(cepInput == null || cepInput.trim().length()==0) {
					// Use the embedded catalog service if running the getting started server sample
					// Get the catalog service end points from ObjectGrid
					cep = getCatalogServiceEndpoints();
				}
			}
			
			// The operation, key and value to execute
			String crudOperation = req.getParameter("CMD_CRUD");
			if (crudOperation!=null) {
				crudOperation = crudOperation.toLowerCase();
			}
			String key = req.getParameter("k");
			String val = req.getParameter("v");

			String action = req.getParameter("CMD_ACTION");
			if(action != null) {
				action = action.toLowerCase();
			}

			try {
				// Connect to the grid if explicitly asked to... or if any
				// CRUD operation is selected.
				if(crudOperation != null || "connect".equals(action)) {
					cep = connectClient(cep, gridName, domainId);
				} 
				else if("disconnect".equals(action)) {
					disconnectClient();
				}
			} 
			finally {
				// Display the main form in all cases
				printForm(pw, cep, gridName, mapName, domainId, connect, defaultDomain, domainIds, crudOperation,  key, val);
			}

			// Invoke the CRUD operation
			if(crudOperation != null) {

				// Get a session
				Session sess = grid.getSession();

				// Get the ObjectMap
				ObjectMap map1 = sess.getMap(mapName);

				if (crudOperation.equals("insert")) {
					map1.insert(key, val);
					pw.println("SUCCESS: Inserted " + val + " with key " + key + EOL);
				} else if (crudOperation.equals("update")) {
					map1.update(key, val);
					pw.println("SUCCESS: Updated key " +key + " with value " + val + EOL);
				} else if (crudOperation.equals("delete")) {
					String oldValue = (String) map1.remove(key);
					pw.println("SUCCESS: Deleted value with key " + key + " and old value " + oldValue + EOL);
				} else if (crudOperation.equals("get")) {
					String value = (String) map1.get(key);
					if (value != null)
						pw.println("SUCCESS: Value is: " + value + EOL);
					else
						pw.println("SUCCESS: Value is: Not found" + EOL);
				}
			}
		} 
		catch (DuplicateKeyException e) {
			System.out.println("Caught exception: " + e);
			printException(pw, e);
		}
		catch (KeyNotFoundException e) {
			System.out.println("Caught exception: " + e);
			printException(pw, e);
		}
		catch (ObjectGridException e) {
			System.out.println("Caught exception: " + e);
			printException(pw, e);
		}
		catch (Exception e) {
			System.out.println("Caught exception: " + e);
			printException(pw, e);
		} 
		finally {
			try {
				pw.write("<p></p>");
				pw.write("</body></html>");
				pw.flush();
			} catch (Exception e) {
				System.out.println("Caught exception: " + e);
				e.printStackTrace();
			}
		}
	}
	
	protected void printHeader(PrintWriter pw, String selConnect) {
		pw.println("<html><head>");
		pw.println("<title>WebSphere eXtreme Scale Getting Started - CRUD Client</title>");
		pw.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">");
		pw.println("<style type=\"text/css\">");
		pw.println("@import \"css/document.css\";");
		pw.println("@import \"css/oneui.css\";");
		
		pw.println("</style>");
		pw.println("</head>");
		pw.println("<body onLoad=\"displayDiv('"+selConnect+"')\">");
		pw.println("<body class=\"claro oneui\" style=\"margin: 0; padding: 0;\">");
		pw.println("<div class=\"headerLogo\">logo</div>");
		pw.println("<div class=\"headerBackground\">");
		pw.println("<div class=\"headerTitle\">IBM WebSphere eXtreme Scale Samples</div>");
		pw.println("<div class=\"headerSubtitle\">WebSphere eXtreme Scale Getting Started Sample</div>");
		pw.println("</div>");
		pw.println("<div class=\"bodyContent\">");
		pw.println("<h2>WebSphere eXtreme Scale Getting Started Client</h2>");
		pw.println("<p>This sample servlet provides simple CRUD operations against an eXtreme Scale grid.</p>");
		pw.println("<p>This sample servlet can also be used for simple CRUD operations against an XC10 DataPower appliance Simple Grid.</p>");
	}

	protected void printForm(PrintWriter pw, String cep, String selGrid, String selMap, String selDomain, String selConnect, CatalogDomainInfo defaultDomain, Collection<CatalogDomainInfo> domainIds, String selOperation, String selKey, String selValue) {
        pw.println("<form name=\"ActionForm\">");
        
        // radio button that toggles between catalog service endpoints and catalog service domain ids
        // Catalog service end points
        pw.println("<fieldset>");
        pw.println("<legend>Catalog Service Endpoints</legend>");
        
        pw.println("<input type=\"radio\" id=\"panelEndpoints2\" name=\"c\" onChange='displayDiv(\"panelEndpoints\")' value=\"panelEndpoints\"> Endpoints<br /><br />");
        pw.println("<input type=\"radio\" id=\"panelDomain2\" name=\"c\" onChange='displayDiv(\"panelDomain\")' value=\"panelDomain\"> Catalog Service Domain ID");        
        
        pw.println("<div id=\"panelEndpoints1\"><p><label for=\"d\">Endpoints</label></p>");
        pw.println("<input type=\"text\" name=\"cep\" value=\"" + (cep==null?"":cep) + "\">");
        pw.println("<p><label for=\"panelEndpoints2\">Remote:</label> &lt;host&gt;:&lt;port&gt;<br /><label for=\"panelEndpoints2\">Embedded:</label> &lt;blank&gt;</p></div>");
        
        pw.println("<div id=\"panelDomain1\"><p><label for=\"domain\">Domain ID</label></p>");
        
        if (domainIds != null && domainIds.size() > 0) { // don't show the drop down
        	pw.println("<select name=\"d\">");
        	// have an "embedded option" when running the server sample
        	String checked = "";
        	String defaultD = "";
        	
    		for (CatalogDomainInfo id : domainIds) {
    			checked = "";
    			if (defaultDomain.getDomainId().equals(id.getDomainId())) {
    				// search to find a default domain id, and have it checked
    				defaultD = " (default)";
    				if (selDomain == null) {
    					checked = " selected=\"selected\"";
    				}
    			}
    			else {
    				defaultD = "";
    			}
    			if (id.getDomainId().equals(selDomain)) {
    				// search to see if the user already checked something
    				checked = " selected=\"selected\"";
    			}
    			pw.println("<option value=\""+id.getDomainId()+"\"" + checked + ">"+id.getDomainId() + defaultD+"</option>");
    		}
    		pw.println("</select>");
    	}
        else {
        	pw.println("<p class=\"error\">No Domain IDs to display</p>");
        }
        pw.println("</div>");
        
        
        pw.println("</fieldset>");             
        pw.println("<p></p>");
    
        // Grid name and map name
        pw.println("<fieldset>");
        pw.println("<legend>Grid</legend>");
        pw.println("<p><label for=\"grid\">Grid</label>");
        pw.println("<input type=\"text\" name=\"g\" value=\"" + (selGrid==null?"":selGrid) + "\"> ");
        pw.println("(For XC10 use Simple Grid Name)</p>");
        
        pw.println("<p><label for=\"map\">Map</label>");
        pw.println("<input type=\"text\" name=\"m\" value=\"" + (selMap==null?"":selMap) + "\"> ");
        pw.println("</p>");
        pw.println("</fieldset>");
        pw.println("<p></p>");
        
        if (grid == null) {
            pw.println("<input type=\"submit\" name=\"CMD_ACTION\" value=\"Connect\"> ");
            pw.println("no connection");
        } else {
            pw.println("<input type=\"submit\" name=\"CMD_ACTION\" value=\"Disconnect\"> ");
        	pw.println("connected to " +  grid.getName() + " on " + cep + "");
        }
        pw.println("<p></p>");
        
        // CRUD Operation
        pw.println("<fieldset>");
        pw.println("<legend>CRUD Operation</legend>");

         // Key and value
        pw.println("<p><label for=\"key\">Key</label>");
        pw.println("<input type=\"text\" name=\"k\" value=\"" + (selKey==null?"":selKey) + "\">");
        pw.println("</p>");

        pw.println("<p><label for=\"value\">Value</label>");
        pw.println("<input type=\"text\" name=\"v\" value=\"" + (selValue==null?"":selValue) + "\">");
        pw.println("(Used for Insert and Update only)</p>");
        
        // Submit button
        pw.println("<input type=\"submit\" name=\"CMD_CRUD\" style=\"width :5em\" value=\"Insert\">");
        pw.println("<input type=\"submit\" name=\"CMD_CRUD\" style=\"width :5em\" value=\"Update\">");
        pw.println("<input type=\"submit\" name=\"CMD_CRUD\" style=\"width :5em\" value=\"Delete\">");
        pw.println("<input type=\"submit\" name=\"CMD_CRUD\" style=\"width :5em\" value=\"Get\">");
        pw.println("</fieldset>");
        pw.println("</form>");
        pw.println("<p></p>");
        pw.println("<script type=\"text/javascript\" src=\"css/swap.js\"></script>");
        pw.flush();
	}

	protected void printException(PrintWriter pw, Throwable e) {
		pw.println("<pre>");
		e.printStackTrace(pw);
		pw.println("</pre>");
		pw.flush();
	}

	/**
	 * Get the catalog service end points from ObjectGrid, if defined.  They
	 * are only defined if the catalog service is running in the WebSphere Application Service
	 * cell or if the catalog.services.cluster cell/server custom property is set.
	 * @return the catalog service endpoints
	 */
	protected String getCatalogServiceEndpoints() {
		return ServerFactory.getServerProperties().getCatalogServiceBootstrap();
	}
	
	/**
	 * Connect to a remote ObjectGrid, caching the grid instance in the servlet.
	 *
	 * @param cep
	 *            the catalog serviced end points in the form: <host>:<port>
	 * @param gridName
	 *            the name of the ObjectGrid to connect to that is managed by
	 *            the Catalog Service
	 * @param domainId
	 * 			  the id of the catalog service domain if there is one
	 * @return a client ObjectGrid connection.
	 */
	protected synchronized String connectClient(String cep, String gridName, String domainId) {

		if (grid == null) {
			// Connect to the Catalog Service. The security and client override
			// XML are not specified.
			
			// use catalog domain manager instead of cep if defined   
			cdm = ObjectGridManagerFactory.getObjectGridManager().getCatalogDomainManager();
			
			if (cdm != null) {
				if(cdm.getDomainInfo(domainId) != null) {
					try {
						String endpoints = cdm.getDomainInfo(domainId).getClientCatalogServerEndpoints();
						cep = endpoints;
					}
					catch (Exception e) {
						System.out.println(e);
						e.printStackTrace();
					}
				}
			}
			try {
				// If the catalog service end points are null, then call the connect
				// method without the catalog service end points parameter to connect to
				// the catalog service configured for this process.
				ClientClusterContext ccc;
				if (cep != null) {
					ccc = ObjectGridManagerFactory.getObjectGridManager().connect(cep, null, null);
				} else {
					ccc = ObjectGridManagerFactory.getObjectGridManager().connect(null, null);
					// for display purposes
					cep = getCatalogServiceEndpoints();
				}
				// Set a custom client properties file 
				URL clientPropsURL = Thread.currentThread().getContextClassLoader().getResource("properties/objectGridClient.properties");
			    ccc.setClientProperties(gridName, clientPropsURL);
		    	// Retrieve the ObjectGrid client connection and cache it.
			    grid = ObjectGridManagerFactory.getObjectGridManager().getObjectGrid(ccc, gridName);
			} catch (Exception e) {
				throw new ObjectGridRuntimeException("Unable to connect to catalog service at endpoints:" + cep, e);
			}
		}
		return cep;
	}

	/**
	 * Disconnect from the grid, releasing any resources and connections in use.
	 */
	protected synchronized void disconnectClient() {
		if(grid != null) {
			grid.destroy();
			grid = null;
		}
	}
}
package com.ibm.websphere.xs.sample.gettingstarted;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.websphere.objectgrid.ObjectGridRuntimeException;
import com.ibm.websphere.objectgrid.server.Container;

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
 * Servlet implementation class 
 */
public class WXSServerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	Container container;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WXSServerServlet() {
        super();
    }  

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
    		if (!response.isCommitted()) {
    			response.setHeader("Pragma", "No-cache");
    			response.setHeader("Cache-Control", "no-cache");
    			response.setDateHeader("Expires", 0);
    			response.setContentType("text/html");
    		}
    		OutputStream os = response.getOutputStream();
    		PrintWriter pw = new PrintWriter(os);
    		printHeader(pw);
			
				// Display the main form 
				printForm(pw);

        } catch (Exception e) {
            throw new ObjectGridRuntimeException("Cannot start OG container", e);
        }

	}


	protected void printHeader(PrintWriter pw) {
		
		pw.println("<html><head>");
		pw.println("<title>WebSphere eXtreme Scale Getting Started Sample Server</title>");
		pw.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">");
		pw.println("<style type=\"text/css\">");
		pw.println("@import \"css/document.css\";");
		pw.println("@import \"css/oneui.css\";");
		
		pw.println("</style>");
		pw.println("</head>");
		pw.println("<body>");
		pw.println("<body class=\"claro oneui\" style=\"margin: 0; padding: 0;\">");
		pw.println("<div class=\"headerLogo\">logo</div>");
		pw.println("<div class=\"headerBackground\">");
		pw.println("<div class=\"headerTitle\">IBM WebSphere eXtreme Scale Samples</div>");
		pw.println("<div class=\"headerSubtitle\">WebSphere eXtreme Scale Getting Started Sample</div>");
		pw.println("</div>");
		pw.println("<div class=\"bodyContent\">");
		pw.println("<h2>WebSphere eXtreme Scale Getting Started Server</h2>");
		pw.println("<p>This sample application starts an ObjectGrid container.</p>");
	}

	protected void printForm(PrintWriter pw) {
        pw.println("<form name=\"ActionForm\">");

        pw.println("<h3>The ObjectGrid Container is active</h3>");
    
        pw.println(" </form>");
        pw.flush();
	}

	protected void printException(PrintWriter pw, Throwable e) {
		pw.println("<pre>");
		e.printStackTrace(pw);
		pw.println("</pre>");
		pw.flush();
	}	
}

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package org.glassfish.grizzly.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.Processor;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.servlet.utils.Utils;

/**
 * {@link GrizzlyWebServer} tests.
 *
 * @author Sebastien Dionne
 * @since 2009/04/15
 */
public class HelloHttpServerTest extends TestCase {

    public static final int PORT = 18890 + 11;
    private static final Logger logger = Grizzly.logger(HelloHttpServerTest.class);
    private HttpServer httpServer;

    public void testNPERegression() throws IOException {
        Utils.dumpOut("testNPERegression");
        try {
            createHttpServer(PORT);
            String[] aliases = new String[] { "*.php" };

            String context = "/";
            String servletPath = "war_autodeploy/php_test";
//            String rootFolder = ".";
           
            ServletService adapter = new ServletService();
            adapter.setServletInstance(new HelloServlet());

            adapter.setContextPath(context);
            adapter.setServletPath(servletPath);
//            adapter.addDocRoot(rootFolder);

            httpServer.getServerConfiguration().addHttpService(adapter, aliases);

            httpServer.start();
           
            String url = context + servletPath + "/index.php";
            HttpURLConnection conn = getConnection(url);
            assertEquals(HttpServletResponse.SC_OK, getResponseCodeFromAlias(conn));
           
            String response = readResponse(conn).toString();
            assertEquals("Hello, world!", response.trim());
           
        } finally {
            stopHttpServer();
        }
    }
    
    public void testMultiPath() throws IOException {
        Utils.dumpOut("testMultiPath");
        try {
            createHttpServer(PORT);
            String[] aliases = new String[] { "*.php" };

            String context = "/";
            String servletPath = "notvalid/php_test";
//            String rootFolder = ".";
           
            ServletService adapter = new ServletService();
            adapter.setServletInstance(new HelloServlet());

            adapter.setContextPath(context);
            adapter.setServletPath(servletPath);
//            adapter.addDocRoot(rootFolder);

            httpServer.getServerConfiguration().addHttpService(adapter, aliases);

            httpServer.start();
           
            String url;
            HttpURLConnection conn;
            String response;
            
            url = context + servletPath + "/index.php";
            conn = getConnection(url);
            assertEquals(HttpServletResponse.SC_OK, getResponseCodeFromAlias(conn));
           
            response = readResponse(conn).toString();
            assertEquals("Hello, world!", response.trim());
            
            // should failed
            url = context + servletPath + "/hello.1";
            conn = getConnection(url);
            assertEquals(HttpServletResponse.SC_NOT_FOUND, getResponseCodeFromAlias(conn));
           
           
        } finally {
            stopHttpServer();
        }
    }
   
   
    private StringBuffer readResponse(HttpURLConnection conn) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
       
        StringBuffer sb = new StringBuffer();
        String line;
       
        while((line = reader.readLine())!=null){
            logger.log(Level.INFO, "received line {0}", line);
            sb.append(line).append("\n");
        }
       
        return sb;
    }

    public void testProtocolFilter() throws IOException {
        Utils.dumpOut("testProtocolFilter");
        try {
            String[] aliases = new String[] { "*.foo" };

            ServletService adapter = new ServletService();
            adapter.setServletInstance(new HelloServlet());
            httpServer = HttpServer.createSimpleServer(".", PORT);
            httpServer.getServerConfiguration().addHttpService(adapter, aliases);
            httpServer.start();

            Processor pc = httpServer.getListener("grizzly").getTransport().getProcessor();
            Utils.dumpOut("ProtcolChain: " + pc);
            assertNotNull(pc);
        } finally {
            stopHttpServer();
        }
    }

    private HttpURLConnection getConnection(String path) throws IOException {
        logger.log(Level.INFO, "sending request to {0}", path);
        URL url = new URL("http", "localhost", PORT, path);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.connect();
        return urlConn;
    }

    private int getResponseCodeFromAlias(HttpURLConnection urlConn) throws IOException {
        return urlConn.getResponseCode();
    }

   
    private void createHttpServer(int port) {
        httpServer = HttpServer.createSimpleServer(".", port);

    }

    private void stopHttpServer() {
        httpServer.stop();
    }
    
    /**
     * Hello world servlet.  Most servlets will extend
     * javax.servlet.http.HttpServlet as this one does.
     */
    public class HelloServlet extends HttpServlet {
      /**
       * Implements the HTTP GET method.  The GET method is the standard
       * browser method.
       *
       * @param request the request object, containing data from the browser
       * @param response the response object to send data to the browser
       */
        @Override
      public void doGet (HttpServletRequest request,
                         HttpServletResponse response)
        throws ServletException, IOException
      {

        // Returns a writer to write to the browser
        PrintWriter out = response.getWriter();

        // Writes the string to the browser.
        out.println("Hello, world!");
        out.close();
      }
    }
}

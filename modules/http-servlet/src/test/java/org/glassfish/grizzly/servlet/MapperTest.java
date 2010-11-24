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

import java.util.logging.Level;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Logger;
import org.glassfish.grizzly.Grizzly;
import org.glassfish.grizzly.servlet.utils.Utils;

/**
 * Test {@link HttpServiceChain} use of the {@link MapperTest}
 *
 * @author Jeanfrancois Arcand
 */
public class MapperTest extends HttpServerAbstractTest {

    public static final int PORT = 18080;
    private static final Logger logger = Grizzly.logger(MapperTest.class);

    public void testOverlappingMapping() throws IOException {
        Utils.dumpOut("testOverlappingMapping");
        try {
            startHttpServer(PORT);
            String[] aliases = new String[]{"/aaa/bbb", "/aaa/ccc"};
            for (String alias : aliases) {
                addHttpService(alias);
            }

            for (String alias : aliases) {
                HttpURLConnection conn = getConnection(alias, PORT);
                assertEquals(HttpServletResponse.SC_OK,
                        getResponseCodeFromAlias(conn));
                assertEquals(alias, readResponse(conn));
            }
        } finally {
            stopHttpServer();
        }
   }

    
    public void testOverlappingMapping2() throws IOException {
        Utils.dumpOut("testOverlappingMapping2");
        try {
            startHttpServer(PORT);
            
            String[] alias = new String[]{"/*.jsp", "/jsp/.xyz"};
            
            ServletService s1 = getServletService(alias[0]);
            s1.setContextPath("/");
            s1.setServletPath("");
//            s1.addDocRoot(".");
                
            httpServer.getServerConfiguration().addHttpService(s1, new String[]{alias[0]});

            ServletService s2 = getServletService(alias[1]);
            s2.setContextPath("/jsp");
            s2.setServletPath("");
//            s2.addDocRoot(".");
                
            httpServer.getServerConfiguration().addHttpService(s2, new String[]{alias[1]});
            
            HttpURLConnection conn = null;
            
            
            conn = getConnection("/jsp/index.jsp", PORT);
            assertEquals(HttpServletResponse.SC_OK, getResponseCodeFromAlias(conn));
            assertEquals(alias[1], readResponse(conn));
            
           
        } finally {
            stopHttpServer();
        }
   }
    
    
    public void testRootMapping() throws IOException {
        Utils.dumpOut("testRootMapping");
        try {
            startHttpServer(PORT);
            String alias = "/";
            addHttpService(alias);
            HttpURLConnection conn = getConnection("/index.html", PORT);
            assertEquals(HttpServletResponse.SC_OK,
                    getResponseCodeFromAlias(conn));
            assertEquals(alias, readResponse(conn));
        } finally {
            stopHttpServer();
        }
    }

    public void testWrongMapping() throws IOException {
        Utils.dumpOut("testWrongMapping");
        try {
            startHttpServer(PORT);
            String alias = "/a/b/c";
            addHttpService(alias);
            HttpURLConnection conn = getConnection("/aaa.html", PORT);
            assertEquals(HttpServletResponse.SC_NOT_FOUND,
                    getResponseCodeFromAlias(conn));
        } finally {
            stopHttpServer();
        }
    }

    public void testComplexMapping() throws IOException {
        Utils.dumpOut("testComplexMapping");
        try {
            startHttpServer(PORT);
            String alias = "/a/b/c/*.html";
            addHttpService(alias);
            HttpURLConnection conn = getConnection("/a/b/c/index.html", PORT);
            assertEquals(HttpServletResponse.SC_OK,
                    getResponseCodeFromAlias(conn));
            assertEquals(alias, readResponse(conn));
        } finally {
            stopHttpServer();
        }
    }

    public void testWildcardMapping() throws IOException {
        Utils.dumpOut("testWildcardMapping");
        try {
            startHttpServer(PORT);
            String alias = "/*.html";
            addHttpService(alias);
            HttpURLConnection conn = getConnection("/index.html", PORT);
            assertEquals(HttpServletResponse.SC_OK,
                    getResponseCodeFromAlias(conn));
            assertEquals(alias, readResponse(conn));
        } finally {
            stopHttpServer();
        }
    }

     public void testWrongMappingRootContext() throws IOException {
        Utils.dumpOut("testWrongMappingRootContext");
        try {
            startHttpServer(PORT);
            String alias = "/*.a";
            addHttpService(alias);
            HttpURLConnection conn = getConnection("/aaa.html", PORT);
            assertEquals(HttpServletResponse.SC_NOT_FOUND, getResponseCodeFromAlias(conn));
        } finally {
            stopHttpServer();
        }
    }


    private ServletService addHttpService(final String alias) {
        ServletService httpService = new ServletService(new HttpServlet() {

            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                logger.log(Level.INFO, "Servlet : {0} received request {1}", new Object[]{alias, req.getRequestURI()});
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(alias);
            }
        });
        httpServer.getServerConfiguration().addHttpService(httpService, new String[]{alias});
        return httpService;
    }
    
    private ServletService getServletService(final String alias) {
        ServletService servletService = new ServletService(new HttpServlet() {

            @Override
            protected void doGet(
                    HttpServletRequest req, HttpServletResponse resp)
                    throws IOException {
                logger.log(Level.INFO, "Servlet : {0} received request {1}", new Object[]{alias, req.getRequestURI()});
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(alias);
            }
        });
        return servletService;
    }

}

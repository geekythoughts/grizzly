/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
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

package org.glassfish.grizzly;

/**
 * <p>
 * This class represents a call-back mechanism that will notify implementations
 * as more input data becomes available to read without blocking.
 * </p>
 *
 * @since 2.2
 */
public interface ReadHandler {

    /**
     * <p>
     * Invoked when data is available to be read without blocking.  Data must
     * be consumed by the handler implementation before re-registering.
     * </p>
     *
     * @throws Exception, {@link Exception} might be thrown by the custom
     *  handler code. This exception will be delegated for processing to
     *  {@link #onError(java.lang.Throwable)}.
     */
    void onDataAvailable() throws Exception;

    
    /**
     * <p>
     * Invoked when an error occurs processing the request asynchronously.
     * </p>
     * @param t the error
     */
    void onError(final Throwable t);


    /**
     * <p>
     * Invoked when all data for the current request has been read.
     * </p>
     * 
     * @throws Exception, {@link Exception} might be thrown by the custom
     *  handler code. This exception will be delegated for processing to
     *  {@link #onError(java.lang.Throwable)}.
     */
    void onAllDataRead() throws Exception;

}

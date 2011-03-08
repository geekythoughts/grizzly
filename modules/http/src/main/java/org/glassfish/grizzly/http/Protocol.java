/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2011 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.grizzly.http;

import java.io.UnsupportedEncodingException;
import org.glassfish.grizzly.http.util.DataChunk;

/**
 * Predefined HTTP protocol versions
 * 
 * @author Alexey Stashok
 */
public enum Protocol {
    HTTP_0_9 ("HTTP/0.9"),
    HTTP_1_0 ("HTTP/1.0"),
    HTTP_1_1 ("HTTP/1.1");

    public static Protocol parseDataChunk(final DataChunk protocolC) {
        if (protocolC.isNull() || protocolC.getLength() == 0
            || protocolC.equals(Protocol.HTTP_0_9.getProtocolString())) {
            return Protocol.HTTP_0_9;
        } else if (protocolC.equals(Protocol.HTTP_1_1.getProtocolString())) {
            return Protocol.HTTP_1_1;
        } else if (protocolC.equals(Protocol.HTTP_1_0.getProtocolString())) {
            return Protocol.HTTP_1_0;
        } else {
            throw new IllegalStateException("Unknown protocol " + protocolC.toString());
        }
    }

    private final String protocolString;
    private byte[] protocolBytes;

    Protocol(String protocolString) {
        this.protocolString = protocolString;
        try {
            protocolBytes = protocolString.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException ignored) {
            protocolBytes = protocolString.getBytes();
        }
    }

    public String getProtocolString() {
        return protocolString;
    }

    public byte[] getProtocolBytes() {
        return protocolBytes;
    }
}

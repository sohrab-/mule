/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.http.ntlm;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.protocol.HttpContext;

public class JCIFSNTLMSchemeFactory implements AuthSchemeProvider
{

    public AuthScheme create(final HttpContext context)
    {
        return new org.apache.http.impl.auth.NTLMScheme(new JCIFSEngine());
    }
}
/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.http;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public class PatchMethod extends HttpEntityEnclosingRequestBase
{

    public PatchMethod()
    {
        super();
    }

    public PatchMethod(final URI uri)
    {
        super();
        setURI(uri);
    }

    @Override
    public String getMethod()
    {
        return HttpConstants.METHOD_PATCH;
    }
}

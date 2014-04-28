/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.http;

import java.io.IOException;
import java.net.SocketException;

import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;

public class MuleHttpMethodRetryHandler extends DefaultHttpRequestRetryHandler
{

    @Override
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context)
    {
        if ((executionCount < this.getRetryCount()) && (exception instanceof SocketException))
        {
            return true;
        }

        return super.retryRequest(exception, executionCount, context);
    }
}

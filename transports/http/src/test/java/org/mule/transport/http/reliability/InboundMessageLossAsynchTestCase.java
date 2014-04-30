/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.http.reliability;

import static org.junit.Assert.assertEquals;
import org.mule.transport.http.HttpConstants;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;

/**
 * Verify that no inbound messages are lost when exceptions occur.
 * The message must either make it all the way to the SEDA queue (in the case of
 * an asynchronous inbound endpoint), or be restored/rolled back at the source.
 * 
 * In the case of the HTTP transport, there is no way to restore the source message
 * so an exception is simply returned to the client.
 */
public class InboundMessageLossAsynchTestCase extends InboundMessageLossTestCase
{
    @Override
    protected String getConfigFile()
    {
        return "reliability/inbound-message-loss-asynch.xml";
    }

    @Test
    @Override
    public void testNoException() throws Exception
    {
        HttpUriRequest request = createRequest(getBaseUri() + "/noException");
        HttpResponse response = httpClient.execute(request);
        assertEquals(HttpConstants.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Test
    @Override
    public void testHandledTransformerException() throws Exception
    {
        HttpUriRequest request = createRequest(getBaseUri() + "/handledTransformerException");
        HttpResponse response = httpClient.execute(request);
        assertEquals(HttpConstants.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Test
    @Override
    public void testComponentException() throws Exception
    {
        HttpUriRequest request = createRequest(getBaseUri() + "/componentException");
        HttpResponse response = httpClient.execute(request);
        // Component exception occurs after the SEDA queue for an asynchronous request, so from the client's
        // perspective, the message has been delivered successfully.
        assertEquals(HttpConstants.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Override
    protected HttpUriRequest createRequest(String uri)
    {
        return new HttpPost(uri);
    }
}

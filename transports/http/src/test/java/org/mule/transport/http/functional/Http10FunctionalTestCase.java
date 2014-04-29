/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.http.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.tck.AbstractServiceAndFlowTestCase;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.transport.http.HttpConstants;

import java.util.Arrays;
import java.util.Collection;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests as per http://www.io.com/~maus/HttpKeepAlive.html
 */
public class Http10FunctionalTestCase extends AbstractServiceAndFlowTestCase
{
    @Rule
    public DynamicPort dynamicPort = new DynamicPort("port1");

    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][]{
            {ConfigVariant.SERVICE, "http-10-config-service.xml"},
            {ConfigVariant.FLOW, "http-10-config-flow.xml"}
        });
    }

    public Http10FunctionalTestCase(ConfigVariant variant, String configResources)
    {
        super(variant, configResources);
    }


    private HttpClient setupHttpClient()
    {
        CloseableHttpClient minimal = HttpClients.createMinimal();
        minimal.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_0);
        return minimal;
    }

    @Test
    public void testHttp10EnforceNonChunking() throws Exception
    {
        HttpClient client = setupHttpClient();
        HttpGet request = new HttpGet(((InboundEndpoint) muleContext.getRegistry().lookupObject("inStreaming")).getAddress());
        RequestConfig config = request.getConfig();
        request.setConfig(config);
        HttpResponse response = client.execute(request);

        String entity = new String(EntityUtils.toByteArray(response.getEntity()));
        assertEquals("hello", entity);

        assertNull(response.getFirstHeader(HttpConstants.HEADER_TRANSFER_ENCODING));
        assertNotNull(request.getFirstHeader(HttpConstants.HEADER_CONTENT_LENGTH));
    }
}

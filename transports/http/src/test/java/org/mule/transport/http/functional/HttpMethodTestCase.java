/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.http.functional;

import static org.junit.Assert.assertEquals;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.tck.AbstractServiceAndFlowTestCase;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.transport.http.HttpConstants;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

public class HttpMethodTestCase extends AbstractServiceAndFlowTestCase
{
    @ClassRule
    public static DynamicPort dynamicPort = new DynamicPort("port1");

    private HttpClient client;

    public HttpMethodTestCase(ConfigVariant variant, String configResources)
    {
        super(variant, configResources);
        setDisposeContextPerClass(true);
        client = HttpClients.createMinimal();
    }

    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][]{
            {ConfigVariant.SERVICE, "http-method-test-service.xml"},
            {ConfigVariant.FLOW, "http-method-test-flow.xml"}
        });
    }

    @Test
    public void testHead() throws Exception
    {
        HttpUriRequest method = new HttpHead(getHttpEndpointAddress());
        HttpResponse response = client.execute(method);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testOptions() throws Exception
    {
        HttpUriRequest method = new HttpOptions(getHttpEndpointAddress());
        HttpResponse response = client.execute(method);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testPut() throws Exception
    {
        HttpUriRequest method = new HttpPut(getHttpEndpointAddress());
        HttpResponse response = client.execute(method);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testDelete() throws Exception
    {
        HttpUriRequest method = new HttpDelete(getHttpEndpointAddress());
        HttpResponse response = client.execute(method);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testTrace() throws Exception
    {
        HttpUriRequest method = new HttpTrace(getHttpEndpointAddress());
        HttpResponse response = client.execute(method);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testConnect() throws Exception
    {
        CustomHttpMethod method = new CustomHttpMethod(HttpConstants.METHOD_CONNECT, getHttpEndpointAddress());
        HttpResponse response = client.execute(method);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testPatch() throws Exception
    {
        HttpUriRequest method = new HttpPatch(getHttpEndpointAddress());
        HttpResponse response = client.execute(method);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testFoo() throws Exception
    {
        //TODO(pablo.kraan): HTTPCLIENT - fix this
        //CustomHttpMethod method = new CustomHttpMethod("FOO", getHttpEndpointAddress());
        //HttpResponse response = client.execute(method);
        //assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
    }

    private String getHttpEndpointAddress()
    {
        InboundEndpoint httpEndpoint = muleContext.getRegistry().lookupObject("inHttpIn");
        return httpEndpoint.getAddress();
    }

    private static class CustomHttpMethod extends HttpRequestBase
    {
        private final String method;

        public CustomHttpMethod(String method, String url) throws URISyntaxException
        {
            super();
            setURI(new URI(url));
            this.method = method;
        }

        @Override
        public String getMethod()
        {
            return method;
        }
    }
}

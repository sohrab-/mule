/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.http.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.tck.AbstractServiceAndFlowTestCase;
import org.mule.tck.functional.EventCallback;
import org.mule.tck.functional.FunctionalTestComponent;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.transport.http.HttpConstants;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

public class HttpPersistentQueueTestCase extends AbstractServiceAndFlowTestCase
{
    private CountDownLatch messageDidArrive = new CountDownLatch(1);
    private int port = -1;

    @Rule
    public DynamicPort dynamicPort = new DynamicPort("port1");
    
    public HttpPersistentQueueTestCase(ConfigVariant variant, String configResources)
    {
        super(variant, configResources);
    }

    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][]{
            {ConfigVariant.SERVICE, "http-persistent-queue-service.xml"},
            {ConfigVariant.FLOW, "http-persistent-queue-flow.xml"}
        });
    }      
    
    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();

        FunctionalTestComponent testComponent = (FunctionalTestComponent) getComponent("PersistentQueueAsync");
        assertNotNull(testComponent);
        testComponent.setEventCallback(new Callback(messageDidArrive));
        port = dynamicPort.getNumber();
    }

    @Test
    public void testPersistentMessageDeliveryWithGet() throws Exception
    {
        HttpGet method = new HttpGet("http://localhost:" + port + "/services/Echo?foo=bar");
        method.addHeader(HttpConstants.HEADER_CONNECTION, "close");
        doTestPersistentMessageDelivery(method);
    }

    @Test
    public void testPersistentMessageDeliveryWithPost() throws Exception
    {
        HttpPost method = new HttpPost("http://localhost:" + port + "/services/Echo");
        method.addHeader(HttpConstants.HEADER_CONNECTION, "close");
        method.setEntity(new UrlEncodedFormEntity(Collections.singletonList(new BasicNameValuePair("foo",
            "bar"))));;
        doTestPersistentMessageDelivery(method);
    }
    private void doTestPersistentMessageDelivery(HttpUriRequest httpMethod) throws Exception
    {
        HttpClient client = HttpClients.createMinimal();
        HttpResponse response = client.execute(httpMethod);

        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        assertTrue(messageDidArrive.await(RECEIVE_TIMEOUT, TimeUnit.MILLISECONDS));
    }
    
    private static class Callback implements EventCallback
    {
        private CountDownLatch messageDidArrive;
        
        public Callback(CountDownLatch latch)
        {
            super();
            messageDidArrive = latch;
        }
        
        public void eventReceived(MuleEventContext context, Object component) throws Exception
        {
            MuleMessage message = context.getMessage();

            Object httpMethod = message.getInboundProperty("http.method");
            if (HttpConstants.METHOD_GET.equals(httpMethod))
            {
                assertEquals("/services/Echo?foo=bar", message.getPayloadAsString());
            }
            else if (HttpConstants.METHOD_POST.equals(httpMethod))
            {
                assertEquals("foo=bar", message.getPayloadAsString());
            }
            else
            {
                fail("invalid HTTP method : " + httpMethod);
            }
            
            assertEquals("true", message.getInboundProperty(HttpConstants.HEADER_CONNECTION));
            assertEquals("true", message.getInboundProperty(HttpConstants.HEADER_KEEP_ALIVE));
            
            messageDidArrive.countDown();            
        }
    }

}

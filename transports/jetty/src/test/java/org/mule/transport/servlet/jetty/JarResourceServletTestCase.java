/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.servlet.jetty;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.mule.tck.junit4.AbstractMuleContextTestCase;
import org.mule.tck.junit4.rule.DynamicPort;
import org.mule.transport.servlet.JarResourceServlet;
import org.mule.transport.servlet.jetty.util.EmbeddedJettyServer;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class JarResourceServletTestCase extends AbstractMuleContextTestCase
{
    @Rule
    public DynamicPort port1 = new DynamicPort("port1");

    private EmbeddedJettyServer server;

    @Before
    public void startEmbeddedJettyServer() throws Exception
    {
        server = new EmbeddedJettyServer(port1.getNumber(), "/", "/mule-resource/*",
            new JarResourceServlet(), muleContext);
        server.start();
    }

    @After
    public void shutdownEmbeddedJettyServer() throws Exception
    {
        if (server != null)
        {
            server.stop();
            server.destroy();
        }
    }

    @Test
    public void retriveHtmlFromClasspath() throws Exception
    {
        muleContext.start();

        String result = getContentsOfResource("foo.html");
        assertTrue(result.contains("${title}"));

        String replacement = "hello foo";
        muleContext.getRegistry().registerObject("title", replacement);

        result = getContentsOfResource("foo.html");
        assertTrue(result.contains(replacement));
    }

    @Test
    public void retriveXmlFromClasspath() throws Exception
    {
        muleContext.start();

        String result = getContentsOfResource("foo.xml");
        assertTrue(result.contains("${bar}"));

        String replacement = "hello bar";
        muleContext.getRegistry().registerObject("bar", replacement);

        result = getContentsOfResource("foo.xml");
        assertTrue(result.contains(replacement));
    }

    private String getContentsOfResource(String resource) throws IOException
    {
        String url = String.format("http://localhost:%d/mule-resource/files/%s", port1.getNumber(),
            resource);
        HttpGet method = new HttpGet(url);
        HttpResponse response = HttpClients.createMinimal().execute(method);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        return response.getEntity().toString();
    }
}

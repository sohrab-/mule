/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.http;

import org.mule.api.MuleException;
import org.mule.construct.Flow;
import org.mule.tck.junit4.AbstractMuleTestCase;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.nio.BlockingChannelConnector;
import org.eclipse.jetty.util.ByteArrayOutputStream2;
import org.eclipse.jetty.util.IO;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

public class HttpClientPerformanceTestCase extends AbstractMuleTestCase
{
    @Rule
    public ContiPerfRule rule = new ContiPerfRule();

    @Mock
    private Flow flow;

    protected Server jetty;
    protected byte[] payload;
    protected HttpClient httpClient;
    protected String target;

    @Override
    public int getTestTimeoutSecs()
    {
        return 40000;
    }

    @Before
    public void before() throws Exception
    {
        payload = createPayload(2048);

        final BlockingChannelConnector connector = new BlockingChannelConnector();
        jetty = new Server();
        jetty.addConnector(connector);
        jetty.setHandler(new EchoHandler());
        jetty.setThreadPool(new org.eclipse.jetty.util.thread.QueuedThreadPool(550));
        jetty.start();

        final ConnectionConfig connectionConfig = ConnectionConfig.custom().setBufferSize(8 * 1024).build();
        final SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(15000).build();
        PoolingHttpClientConnectionManager mgr = new PoolingHttpClientConnectionManager();
        mgr.setDefaultSocketConfig(socketConfig);
        mgr.setDefaultConnectionConfig(connectionConfig);
        mgr.setMaxTotal(2000);
        mgr.setDefaultMaxPerRoute(2000);

        httpClient = HttpClients.createMinimal(mgr);

        target = "http://localhost:" + connector.getLocalPort() + "/echo";
    }

    @After
    public void after() throws Exception
    {
        jetty.stop();
    }

    @Test
    @PerfTest(duration = 40000, threads = 1, warmUp = 10000)
    public void send1Thread() throws MuleException, Exception
    {
        doSend();
    }

    @Test
    @PerfTest(duration = 40000, threads = 10, warmUp = 10000)
    public void send10Threads() throws MuleException, Exception
    {
        doSend();
    }

    @Test
    @PerfTest(duration = 40000, threads = 20, warmUp = 10000)
    public void send20Threads() throws MuleException, Exception
    {
        doSend();
    }

    @Test
    @PerfTest(duration = 40000, threads = 50, warmUp = 10000)
    public void send50Threads() throws MuleException, Exception
    {
        doSend();
    }

    @Test
    @PerfTest(duration = 40000, threads = 100, warmUp = 10000)
    public void send100Threads() throws MuleException, Exception
    {
        doSend();
    }

    @Test
    @PerfTest(duration = 40000, threads = 200, warmUp = 10000)
    public void send200Threads() throws MuleException, Exception
    {
        doSend();
    }

    @Test
    @PerfTest(duration = 40000, threads = 500, warmUp = 10000)
    public void send500Threads() throws MuleException, Exception
    {
        doSend();
    }

    protected void doSend() throws ClientProtocolException, IOException
    {
        final byte[] buffer = new byte[4096];
        final HttpPost httppost = new HttpPost(target);
        httppost.setEntity(new ByteArrayEntity(payload));
        org.apache.http.HttpResponse response = httpClient.execute(httppost);
        final HttpEntity entity = response.getEntity();
        if (entity != null)
        {
            final InputStream instream = entity.getContent();
            int contentLen = 0;
            if (instream != null)
            {
                try
                {
                    int l = 0;
                    while ((l = instream.read(buffer)) != -1)
                    {
                        contentLen += l;
                    }
                }
                finally
                {
                    instream.close();
                }
            }
        }

    }

    protected byte[] createPayload(int length)
    {
        final byte[] content = new byte[length];
        final int r = Math.abs(content.hashCode());
        for (int i = 0; i < content.length; i++)
        {
            content[i] = (byte) ((r + i) % 96 + 32);
        }
        return content;
    }

    static class EchoHandler extends AbstractHandler
    {

        @Override
        public void handle(String target,
                           org.eclipse.jetty.server.Request baseRequest,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException, ServletException
        {
            final ByteArrayOutputStream2 buffer = new ByteArrayOutputStream2();
            final InputStream instream = request.getInputStream();
            if (instream != null)
            {
                IO.copy(instream, buffer);
                buffer.flush();
            }
            final byte[] content = buffer.getBuf();
            final int len = buffer.getCount();

            response.setStatus(200);
            response.setContentLength(len);

            final OutputStream outstream = response.getOutputStream();
            outstream.write(content, 0, len);
            outstream.flush();

        }
    }

}

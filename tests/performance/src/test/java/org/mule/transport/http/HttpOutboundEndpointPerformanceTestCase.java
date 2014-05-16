/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.http;

import org.mule.DefaultMuleEvent;
import org.mule.DefaultMuleMessage;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.config.ThreadingProfile;
import org.mule.api.endpoint.EndpointBuilder;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.Startable;
import org.mule.api.lifecycle.Stoppable;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.transport.MessageDispatcher;
import org.mule.config.ChainedThreadingProfile;
import org.mule.construct.Flow;
import org.mule.context.DefaultMuleContextFactory;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.transport.http.ntlm.JCIFSNTLMSchemeFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.config.SocketConfig.Builder;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;

@RunWith(Parameterized.class)
public class HttpOutboundEndpointPerformanceTestCase extends AbstractMuleTestCase
{
    @Rule
    public ContiPerfRule rule = new ContiPerfRule();

    @Mock
    private Flow flow;

    protected Server jetty;
    protected OutboundEndpoint endpoint;
    protected byte[] payload;
    protected MuleContext muleContext;
    protected boolean staleCheckEnabled;
    protected boolean singletonDispatcher;

    public HttpOutboundEndpointPerformanceTestCase(boolean staleCheckEnabled, boolean singletonDispatcher)
    {
        this.staleCheckEnabled = staleCheckEnabled;
        this.singletonDispatcher = singletonDispatcher;
    }

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

        muleContext = new DefaultMuleContextFactory().createMuleContext();

        HttpConnector httpConnector = new TestHttpConnector(muleContext, staleCheckEnabled,
            singletonDispatcher);
        ThreadingProfile tp = new ChainedThreadingProfile();
        tp.setMaxThreadsActive(550);
        httpConnector.setDispatcherThreadingProfile(tp);
        httpConnector.setSendTcpNoDelay(true);
        muleContext.getRegistry().registerConnector(httpConnector);

        EndpointBuilder builder = muleContext.getEndpointFactory().getEndpointBuilder(
            "http://localhost:" + connector.getLocalPort() + "/echo");
        builder.setConnector(httpConnector);
        endpoint = builder.buildOutboundEndpoint();;
        muleContext.start();

        System.out.println("Stale Connection Check Enabled: " + staleCheckEnabled);
        System.out.println("Singleton Dispatcher: " + singletonDispatcher);
    }

    @After
    public void after() throws Exception
    {
        jetty.stop();
        endpoint.getConnector().stop();
        muleContext.dispose();
    }

    @Test
    @PerfTest(duration = 40000, threads = 1, warmUp = 10000)
    public void send1Thread() throws MuleException, Exception
    {
        endpoint.process(createMuleEvent()).getMessageAsBytes();
    }

    @Test
    @PerfTest(duration = 40000, threads = 10, warmUp = 10000)
    public void send10Threads() throws MuleException, Exception
    {
        endpoint.process(createMuleEvent()).getMessageAsBytes();
    }

    @Test
    @PerfTest(duration = 40000, threads = 20, warmUp = 10000)
    public void send20Threads() throws MuleException, Exception
    {
        endpoint.process(createMuleEvent()).getMessageAsBytes();
    }

    @Test
    @PerfTest(duration = 40000, threads = 50, warmUp = 10000)
    public void send50Threads() throws MuleException, Exception
    {
        endpoint.process(createMuleEvent()).getMessageAsBytes();
    }

    @Test
    @PerfTest(duration = 40000, threads = 100, warmUp = 10000)
    public void send100Threads() throws MuleException, Exception
    {
        endpoint.process(createMuleEvent()).getMessageAsBytes();
    }

    @Test
    @PerfTest(duration = 40000, threads = 200, warmUp = 10000)
    public void send200Threads() throws MuleException, Exception
    {
        endpoint.process(createMuleEvent()).getMessageAsBytes();
    }

    @Test
    @PerfTest(duration = 40000, threads = 500, warmUp = 10000)
    public void send500Threads() throws MuleException, Exception
    {
        endpoint.process(createMuleEvent()).getMessageAsBytes();
    }

    protected MuleEvent createMuleEvent() throws Exception
    {
        return new DefaultMuleEvent(new DefaultMuleMessage(payload, muleContext),
            MessageExchangePattern.REQUEST_RESPONSE, flow);
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

    static class TestHttpConnector extends HttpConnector
    {
        private boolean staleCheckingEnabled;
        private boolean singletonDispatcher;
        protected MessageDispatcher singletonDispatcherInstance;

        public TestHttpConnector(MuleContext context,
                                 boolean staleCheckingEnabled,
                                 boolean singletonDispatcher)
        {
            super(context);
            this.staleCheckingEnabled = staleCheckingEnabled;
            this.singletonDispatcher = singletonDispatcher;
        }

        @Override
        protected void doInitialise() throws InitialisationException
        {
            super.doInitialise();
            if (clientConnectionManager == null)
            {
                try
                {
                    Builder socketConfigBuilder = SocketConfig.custom();
                    if (getSocketSoLinger() != INT_VALUE_NOT_SET)
                    {
                        socketConfigBuilder.setSoLinger(getSocketSoLinger());
                    }
                    if (getClientSoTimeout() != INT_VALUE_NOT_SET)
                    {
                        socketConfigBuilder.setSoTimeout(getClientSoTimeout());
                    }
                    socketConfigBuilder.setTcpNoDelay(isSendTcpNoDelay());

                    ConnectionConfig.Builder connectionConfigBuilder = ConnectionConfig.custom();
                    if (getSendBufferSize() != INT_VALUE_NOT_SET)
                    {
                        connectionConfigBuilder.setBufferSize(getSendBufferSize());
                    }
                    // TODO(dfeist): HTTPCLIENT - Receive buffer size is not used, only one buffer size is
                    // specified.
                    // if (getSendBufferSize() != INT_VALUE_NOT_SET)
                    // {
                    // params.setSendBufferSize(getSendBufferSize());
                    // }

                    PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
                        getConnectionSocketFactoryRegistry());
                    connManager.setDefaultConnectionConfig(connectionConfigBuilder.build());
                    connManager.setDefaultSocketConfig(socketConfigBuilder.build());

                    if (singletonDispatcher)
                    {
                        connManager.setMaxTotal(550);
                        connManager.setDefaultMaxPerRoute(550);
                    }
                    else
                    {
                        connManager.setMaxTotal(dispatchers.getMaxTotal());
                        connManager.setDefaultMaxPerRoute(dispatchers.getMaxTotal());

                    }

                    clientConnectionManager = connManager;
                }
                catch (Exception e)
                {
                    throw new InitialisationException(e, this);
                }
            }
        }

        protected HttpClient doClientConnect() throws Exception
        {
            CredentialsProvider credsProvider = new BasicCredentialsProvider();

            if (getProxyUsername() != null)
            {

                if (isProxyNtlmAuthentication())
                {
                    credsProvider.setCredentials(new AuthScope(getProxyHostname(), getProxyPort()),
                        new NTCredentials(getProxyUsername() + "/" + getProxyPassword()));
                }
                else
                {
                    credsProvider.setCredentials(new AuthScope(getProxyHostname(), getProxyPort()),
                        new UsernamePasswordCredentials(getProxyUsername(), getProxyPassword()));
                }
            }

            Registry<AuthSchemeProvider> authSchemeRegistry = RegistryBuilder.<AuthSchemeProvider> create()
                .register(AuthSchemes.NTLM, new JCIFSNTLMSchemeFactory())
                .register(AuthSchemes.BASIC, new BasicSchemeFactory())
                .register(AuthSchemes.DIGEST, new DigestSchemeFactory())
                .register(AuthSchemes.SPNEGO, new SPNegoSchemeFactory())
                .register(AuthSchemes.KERBEROS, new KerberosSchemeFactory())
                .build();

            HttpClient client = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .setConnectionManager(clientConnectionManager)
                .setDefaultAuthSchemeRegistry(authSchemeRegistry)
                .setDefaultRequestConfig(
                    RequestConfig.custom()
                        .setStaleConnectionCheckEnabled(staleCheckingEnabled)
                        .setConnectTimeout(10000)
                        .build())
                .build();

            return client;
        }

        @Override
        protected MessageDispatcher borrowDispatcher(OutboundEndpoint endpoint) throws MuleException
        {
            if (singletonDispatcher)
            {
                return singletonDispatcherInstance;
            }
            else
            {
                return super.borrowDispatcher(endpoint);
            }
        }

        @Override
        protected void returnDispatcher(OutboundEndpoint endpoint, MessageDispatcher dispatcher)
        {
            if (!singletonDispatcher)
            {
                super.returnDispatcher(endpoint, dispatcher);
            }
        }

        @Override
        public MessageProcessor createDispatcherMessageProcessor(OutboundEndpoint endpoint)
            throws MuleException
        {
            if (singletonDispatcher)
            {
                singletonDispatcherInstance = getDispatcherFactory().create(endpoint);
                applyLifecycle(singletonDispatcherInstance);
            }
            return super.createDispatcherMessageProcessor(endpoint);
        }

        protected void applyLifecycle(MessageDispatcher dispatcher) throws MuleException
        {
            String phase = lifecycleManager.getCurrentPhase();
            if (phase.equals(Startable.PHASE_NAME) && !dispatcher.getLifecycleState().isStarted())
            {
                if (!dispatcher.getLifecycleState().isInitialised())
                {
                    dispatcher.initialise();
                }
                dispatcher.start();
            }
            else if (phase.equals(Stoppable.PHASE_NAME) && dispatcher.getLifecycleState().isStarted())
            {
                dispatcher.stop();
            }
            else if (Disposable.PHASE_NAME.equals(phase))
            {
                dispatcher.dispose();
            }
        }
    }

    @Parameters
    public static Collection<Object[]> parameters()
    {
        return Arrays.asList(new Object[][]{{Boolean.TRUE, Boolean.FALSE}, {Boolean.FALSE, Boolean.FALSE},
            {Boolean.TRUE, Boolean.TRUE}, {Boolean.FALSE, Boolean.TRUE}});
    }

}

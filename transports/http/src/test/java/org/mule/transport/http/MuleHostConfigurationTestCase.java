/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.http;

import org.mule.tck.junit4.AbstractMuleTestCase;

import org.junit.Test;

public class MuleHostConfigurationTestCase extends AbstractMuleTestCase
{
    
    private static final String HTTPX = "httpx";
    
    @Test
    public void testSetHostViaUri() throws Exception
    {
        //TODO(pablo.kraan): HTTPCLIENT - fix this
        //HostConfiguration hostConfig = createHostConfiguration();
        //
        //URI uri = new URI("http://www.mulesoft.org:8080", false);
        //hostConfig.setHost(uri);
        //
        //assertMockSocketFactory(hostConfig);
        //assertEquals("www.mulesoft.org", hostConfig.getHost());
        //assertEquals(8080, hostConfig.getPort());
    }

    @Test
    public void testSetHostViaUriWithDifferentProtocol() throws Exception
    {
        //TODO(pablo.kraan): HTTPCLIENT - fix this
        //new DifferentProtocolTemplate()
        //{
        //    protected void doTest() throws Exception
        //    {
        //        HostConfiguration hostConfig = createHostConfiguration();
        //
        //        URI uri = new URI("httpx://www.mulesoft.org:8080", false);
        //        hostConfig.setHost(uri);
        //
        //        assertTrue(hostConfig.getProtocol().getSocketFactory() instanceof DefaultProtocolSocketFactory);
        //        assertEquals("www.mulesoft.org", hostConfig.getHost());
        //        assertEquals(8080, hostConfig.getPort());
        //    }
        //}.test();
    }

    @Test
    public void testSetHostViaHttpHost()
    {
        //TODO(pablo.kraan): HTTPCLIENT - fix this
        //HostConfiguration hostConfig = createHostConfiguration();
        //
        //HttpHost host = new HttpHost("www.mulesoft.org", 8080);
        //hostConfig.setHost(host);
        //
        //assertMockSocketFactory(hostConfig);
        //assertEquals("www.mulesoft.org", hostConfig.getHost());
        //assertEquals(8080, hostConfig.getPort());
    }

    @Test
    public void testSetHostViaHostAndPortAndProtocolName()
    {
        //TODO(pablo.kraan): HTTPCLIENT - fix this
        //HostConfiguration hostConfig = createHostConfiguration();
        //
        //hostConfig.setHost("www.mulesoft.org", 8080, "http");
        //
        //assertMockSocketFactory(hostConfig);
        //assertEquals("www.mulesoft.org", hostConfig.getHost());
        //assertEquals(8080, hostConfig.getPort());
    }

    @Test
    public void testSetHostViaHostAndPortAndProtocolNameWithDifferentProtocol() throws Exception
    {
        //TODO(pablo.kraan): HTTPCLIENT - fix this
        //new DifferentProtocolTemplate()
        //{
        //    protected void doTest() throws Exception
        //    {
        //        HostConfiguration hostConfig = createHostConfiguration();
        //
        //        hostConfig.setHost("www.mulesoft.org", 8080, "httpx");
        //
        //        assertDefaultSocketFactory(hostConfig);
        //        assertEquals("www.mulesoft.org", hostConfig.getHost());
        //        assertEquals(8080, hostConfig.getPort());
        //    }
        //}.test();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSetHostViaHostAndVirtualHostAndPortAndProtocol()
    {
        //TODO(pablo.kraan): HTTPCLIENT - fix this
        //HostConfiguration hostConfig = createHostConfiguration();
        //
        //Protocol protocol = Protocol.getProtocol("http");
        //hostConfig.setHost("www.mulesoft.org", "www.mulesoft.com", 8080, protocol);
        //
        //assertMockSocketFactory(hostConfig);
        //assertEquals("www.mulesoft.org", hostConfig.getHost());
        //assertEquals(8080, hostConfig.getPort());
        //assertEquals("www.mulesoft.com", hostConfig.getVirtualHost());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSetHostViaHostAndVirtualHostAndPortAndProtocolWithDifferentProtocol() throws Exception
    {
        //TODO(pablo.kraan): HTTPCLIENT - fix this
        //new DifferentProtocolTemplate()
        //{
        //    protected void doTest() throws Exception
        //    {
        //        HostConfiguration hostConfig = createHostConfiguration();
        //
        //        Protocol protocol = Protocol.getProtocol("httpx");
        //        hostConfig.setHost("www.mulesoft.org", "www.mulesoft.com", 8080, protocol);
        //
        //        assertDefaultSocketFactory(hostConfig);
        //        assertEquals("www.mulesoft.org", hostConfig.getHost());
        //        assertEquals(8080, hostConfig.getPort());
        //        assertEquals("www.mulesoft.com", hostConfig.getVirtualHost());
        //    }
        //}.test();
    }

    @Test
    public void testSetHostViaHostAndPort()
    {
        //TODO(pablo.kraan): HTTPCLIENT - fix this
        //HostConfiguration hostConfig = createHostConfiguration();
        //
        //hostConfig.setHost("www.mulesoft.org", 8080);
        //
        //assertMockSocketFactory(hostConfig);
        //assertEquals("www.mulesoft.org", hostConfig.getHost());
        //assertEquals(8080, hostConfig.getPort());
    }

    @Test
    public void testSetHostViaHost()
    {
        //TODO(pablo.kraan): HTTPCLIENT - fix this
        //HostConfiguration hostConfig = createHostConfiguration();
        //
        //hostConfig.setHost("www.mulesoft.org");
        //
        //assertEquals("www.mulesoft.org", hostConfig.getHost());
        //assertMockSocketFactory(hostConfig);
    }

    @Test
    public void testClone()
    {
        //TODO(pablo.kraan): HTTPCLIENT - fix this
        //HostConfiguration hostConfig = createHostConfiguration();
        //HostConfiguration clone = (HostConfiguration) hostConfig.clone();
        //assertMockSocketFactory(clone);
    }

    //TODO(pablo.kraan): HTTPCLIENT - fix this
    //private MuleHostConfiguration createHostConfiguration()
    //{
    //    MuleHostConfiguration hostConfig = new MuleHostConfiguration();
    //    ProtocolSocketFactory socketFactory = new MockSecureProtocolFactory();
    //    Protocol protocol = new Protocol("http", socketFactory, 80);
    //    hostConfig.setHost("localhost", 80, protocol);
    //
    //    // since we're using a setHost variant here, too let's assert that it actually worked
    //    assertMockSocketFactory(hostConfig);
    //
    //    return hostConfig;
    //}
    //
    //private void assertMockSocketFactory(HostConfiguration hostConfig)
    //{
    //    assertTrue(hostConfig.getProtocol().getSocketFactory() instanceof MockSecureProtocolFactory);
    //}
    //
    //private void assertDefaultSocketFactory(HostConfiguration hostConfig)
    //{
    //    assertTrue(hostConfig.getProtocol().getSocketFactory() instanceof DefaultProtocolSocketFactory);
    //}
    
    private static abstract class DifferentProtocolTemplate
    {
        public DifferentProtocolTemplate()
        {
            super();
        }
        
        @Test
    public void test() throws Exception
        {
            //TODO(pablo.kraan): HTTPCLIENT - fix this
            //try
            //{
            //    Protocol httpxProtocol = new Protocol(HTTPX, new DefaultProtocolSocketFactory(), 81);
            //    Protocol.registerProtocol(HTTPX, httpxProtocol);
            //
            //    doTest();
            //}
            //finally
            //{
            //    Protocol.unregisterProtocol(HTTPX);
            //}
        }
        
        protected abstract void doTest() throws Exception;
    }

    //TODO(pablo.kraan): HTTPCLIENT - fix this
    //private static class MockSecureProtocolFactory implements SecureProtocolSocketFactory
    //{
    //    public MockSecureProtocolFactory()
    //    {
    //        super();
    //    }
    //
    //    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
    //        throws IOException, UnknownHostException
    //    {
    //        throw new UnsupportedOperationException();
    //    }
    //
    //    public Socket createSocket(String host, int port) throws IOException, UnknownHostException
    //    {
    //        throw new UnsupportedOperationException();
    //    }
    //
    //    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort)
    //        throws IOException, UnknownHostException
    //    {
    //        throw new UnsupportedOperationException();
    //    }
    //
    //    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort,
    //        HttpConnectionParams params) throws IOException, UnknownHostException, ConnectTimeoutException
    //    {
    //        throw new UnsupportedOperationException();
    //    }
    //}
    
}



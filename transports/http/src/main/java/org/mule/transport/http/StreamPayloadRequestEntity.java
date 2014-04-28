/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.http;

import org.mule.api.MuleEvent;
import org.mule.api.transport.OutputHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

public class StreamPayloadRequestEntity implements HttpEntity
{

    private OutputHandler outputHandler;
    private MuleEvent event;


    public StreamPayloadRequestEntity(OutputHandler outputHandler, MuleEvent event)
    {
        this.outputHandler = outputHandler;
        this.event = event;
    }


    public boolean isRepeatable()
    {
        return false;
    }

    public boolean isChunked()
    {
        //TODO(pablo.kraan): HTTPCLIENT - implement this
        return false;
    }

    public long getContentLength()
    {
        return -1L;
    }

    public Header getContentType()
    {
        return new BasicHeader(HttpConstants.HEADER_CONTENT_TYPE, event.getMessage().getOutboundProperty(HttpConstants.HEADER_CONTENT_TYPE, HttpConstants.DEFAULT_CONTENT_TYPE));
    }

    public Header getContentEncoding()
    {
        //TODO(pablo.kraan): HTTPCLIENT - implement this
        return null;
    }

    public InputStream getContent() throws IOException, IllegalStateException
    {
        //TODO(pablo.kraan): HTTPCLIENT - implement this
        return null;
    }

    public void writeTo(OutputStream outputStream) throws IOException
    {
        outputHandler.write(event, outputStream);
        outputStream.flush();
    }

    public boolean isStreaming()
    {
        //TODO(pablo.kraan): HTTPCLIENT - implement this
        return false;
    }

    public void consumeContent() throws IOException
    {
        //TODO(pablo.kraan): HTTPCLIENT - implement this
    }
}

/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.http.transformers;

import org.mule.api.MuleMessage;
import org.mule.api.config.MuleProperties;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.OutputHandler;
import org.mule.transformer.AbstractMessageTransformer;
import org.mule.transformer.types.DataTypeFactory;
import org.mule.transport.NullPayload;
import org.mule.transport.http.HttpConnector;
import org.mule.transport.http.HttpConstants;
import org.mule.transport.http.i18n.HttpMessages;
import org.mule.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

/**
 * <code>ObjectToHttpClientMethodRequest</code> transforms a MuleMessage into a
 * HttpClient HttpMethod that represents an HttpRequest.
 */
public class ObjectToHttpClientMethodRequest extends AbstractMessageTransformer
{
    public ObjectToHttpClientMethodRequest()
    {
        setReturnDataType(DataTypeFactory.create(org.apache.http.HttpRequest.class));
        registerSourceType(DataTypeFactory.MULE_MESSAGE);
        registerSourceType(DataTypeFactory.BYTE_ARRAY);
        registerSourceType(DataTypeFactory.STRING);
        registerSourceType(DataTypeFactory.INPUT_STREAM);
        registerSourceType(DataTypeFactory.create(OutputHandler.class));
        registerSourceType(DataTypeFactory.create(NullPayload.class));
        registerSourceType(DataTypeFactory.create(Map.class));
    }

    @Override
    public Object transformMessage(MuleMessage msg, String outputEncoding) throws TransformerException
    {
        //TODO(pablo.kraan): HTTPCLIENT - fix this
        String method = detectHttpMethod(msg);
        try
        {
            org.apache.http.HttpRequest httpMethod = null;

            if (HttpConstants.METHOD_GET.equals(method))
            {
                httpMethod = createGetMethod(msg, outputEncoding);
            }
            else if (HttpConstants.METHOD_POST.equalsIgnoreCase(method))
            {
                httpMethod = createPostMethod(msg, outputEncoding);
            }
            //else if (HttpConstants.METHOD_PUT.equalsIgnoreCase(method))
            //{
            //    httpMethod = createPutMethod(msg, outputEncoding);
            //}
            //else if (HttpConstants.METHOD_DELETE.equalsIgnoreCase(method))
            //{
            //    httpMethod = createDeleteMethod(msg);
            //}
            //else if (HttpConstants.METHOD_HEAD.equalsIgnoreCase(method))
            //{
            //    httpMethod = createHeadMethod(msg);
            //}
            //else if (HttpConstants.METHOD_OPTIONS.equalsIgnoreCase(method))
            //{
            //    httpMethod = createOptionsMethod(msg);
            //}
            //else if (HttpConstants.METHOD_TRACE.equalsIgnoreCase(method))
            //{
            //    httpMethod = createTraceMethod(msg);
            //}
            //else if (HttpConstants.METHOD_PATCH.equalsIgnoreCase(method))
            //{
            //    httpMethod = createPatchMethod(msg, outputEncoding);
            //}
            //else
            //{
            //    throw new TransformerException(HttpMessages.unsupportedMethod(method));
            //}
            //
            //// Allow the user to set HttpMethodParams as an object on the message
            //final HttpMethodParams params = (HttpMethodParams) msg.removeProperty(
            //    HttpConnector.HTTP_PARAMS_PROPERTY, PropertyScope.OUTBOUND);
            //if (params != null)
            //{
            //    httpMethod.setParams(params);
            //}
            //else
            //{
            //    // TODO we should probably set other properties here
            //    final String httpVersion = msg.getOutboundProperty(HttpConnector.HTTP_VERSION_PROPERTY,
            //        HttpConstants.HTTP11);
            //    if (HttpConstants.HTTP10.equals(httpVersion))
            //    {
            //        httpMethod.getParams().setVersion(HttpVersion.HTTP_1_0);
            //    }
            //    else
            //    {
            //        httpMethod.getParams().setVersion(HttpVersion.HTTP_1_1);
            //    }
            //}
            //
            //setHeaders(httpMethod, msg);
            //
            return httpMethod;
        }
        catch (final Exception e)
        {
            throw new TransformerException(this, e);
        }
    }

    protected String detectHttpMethod(MuleMessage msg)
    {
        String method = msg.getOutboundProperty(HttpConnector.HTTP_METHOD_PROPERTY, null);
        if (method == null)
        {
            method = msg.getInvocationProperty(HttpConnector.HTTP_METHOD_PROPERTY, HttpConstants.METHOD_POST);
        }
        return method;
    }

    protected org.apache.http.HttpRequest createGetMethod(MuleMessage msg, String outputEncoding) throws Exception
    {
        final Object src = msg.getPayload();
        // TODO It makes testing much harder if we use the endpoint on the
        // transformer since we need to create correct message types and endpoints
        // URI uri = getEndpoint().getEndpointURI().getUri();
        final URI uri = getURI(msg);
        org.apache.http.HttpRequest httpMethod;
        String query = uri.getRawQuery();

        String paramName = msg.getOutboundProperty(HttpConnector.HTTP_GET_BODY_PARAM_PROPERTY, null);
        if (paramName != null)
        {
            paramName = URLEncoder.encode(paramName, outputEncoding);

            String paramValue;
            Boolean encode = msg.getInvocationProperty(HttpConnector.HTTP_ENCODE_PARAMVALUE);
            if (encode == null)
            {
                encode = msg.getOutboundProperty(HttpConnector.HTTP_ENCODE_PARAMVALUE, true);
            }

            if (encode)
            {
                paramValue = URLEncoder.encode(src.toString(), outputEncoding);
            }
            else
            {
                paramValue = src.toString();
            }

            if (!(src instanceof NullPayload) && !StringUtils.EMPTY.equals(src))
            {
                if (query == null)
                {
                    query = paramName + "=" + paramValue;
                }
                else
                {
                    query += "&" + paramName + "=" + paramValue;
                }
            }
        }

        //TODO(pablo.kraan): HTTPCLIENT - check that this way of creating the uri is correct
        String baseUri = uri.toString();
        if (!StringUtils.isEmpty(query))
        {
            baseUri = baseUri + "?" + query;
        }
        httpMethod = new HttpGet(baseUri);
        return httpMethod;
    }

    protected org.apache.http.HttpRequest createPostMethod(MuleMessage msg, String outputEncoding) throws Exception
    {
        URI uri = getURI(msg);
        HttpPost postMethod = new HttpPost(uri.toString());

        //TODO(pablo.kraan): HTTPCLIENT - fix this
        String bodyParameterName = getBodyParameterName(msg);
        //Object src = msg.getPayload();
        //if (src instanceof Map)
        //{
        //    for (Map.Entry<?, ?> entry : ((Map<?, ?>) src).entrySet())
        //    {
        //        postMethod.addParameter(entry.getKey().toString(), entry.getValue().toString());
        //    }
        //}
        //else if (bodyParameterName != null)
        //{
        //    postMethod.addParameter(bodyParameterName, src.toString());
        //
        //}
        //else
        //{
        //    setupEntityMethod(src, outputEncoding, msg, postMethod);
        //}
        //checkForContentType(msg, postMethod);

        return postMethod;
    }

    //private void checkForContentType(MuleMessage msg, EntityEnclosingMethod method)
    //{
    //    // if a content type was specified on the endpoint, use it
    //    String outgoingContentType = msg.getInvocationProperty(HttpConstants.HEADER_CONTENT_TYPE);
    //    if (outgoingContentType != null)
    //    {
    //        method.setRequestHeader(HttpConstants.HEADER_CONTENT_TYPE, outgoingContentType);
    //    }
    //}

    protected String getBodyParameterName(MuleMessage message)
    {
        String bodyParameter = message.getOutboundProperty(HttpConnector.HTTP_POST_BODY_PARAM_PROPERTY);
        if (bodyParameter == null)
        {
            bodyParameter = message.getInvocationProperty(HttpConnector.HTTP_POST_BODY_PARAM_PROPERTY);
        }
        return bodyParameter;
    }

    //TODO(pablo.kraan): HTTPCLIENT - fix this
    //protected HttpMethod createPutMethod(MuleMessage msg, String outputEncoding) throws Exception
    //{
    //    URI uri = getURI(msg);
    //    PutMethod putMethod = new PutMethod(uri.toString());
    //
    //    Object payload = msg.getPayload();
    //    setupEntityMethod(payload, outputEncoding, msg, putMethod);
    //    checkForContentType(msg, putMethod);
    //    return putMethod;
    //}
    //
    //protected HttpMethod createDeleteMethod(MuleMessage message) throws Exception
    //{
    //    URI uri = getURI(message);
    //    return new DeleteMethod(uri.toString());
    //}
    //
    //protected HttpMethod createHeadMethod(MuleMessage message) throws Exception
    //{
    //    URI uri = getURI(message);
    //    return new HeadMethod(uri.toString());
    //}
    //
    //protected HttpMethod createOptionsMethod(MuleMessage message) throws Exception
    //{
    //    URI uri = getURI(message);
    //    return new OptionsMethod(uri.toString());
    //}
    //
    //protected HttpMethod createTraceMethod(MuleMessage message) throws Exception
    //{
    //    URI uri = getURI(message);
    //    return new TraceMethod(uri.toString());
    //}
    //
    //protected HttpMethod createPatchMethod(MuleMessage message, String outputEncoding) throws Exception
    //{
    //    URI uri = getURI(message);
    //    PatchMethod patchMethod = new PatchMethod(uri.toString());
    //
    //    Object payload = message.getPayload();
    //    setupEntityMethod(payload, outputEncoding, message, patchMethod);
    //    checkForContentType(message, patchMethod);
    //    return patchMethod;
    //}

    protected URI getURI(MuleMessage message) throws URISyntaxException, TransformerException
    {
        String endpointAddress = message.getOutboundProperty(MuleProperties.MULE_ENDPOINT_PROPERTY, null);
        if (endpointAddress == null)
        {
            throw new TransformerException(
                HttpMessages.eventPropertyNotSetCannotProcessRequest(MuleProperties.MULE_ENDPOINT_PROPERTY),
                this);
        }
        return new URI(endpointAddress);
    }

    //TODO(pablo.kraan): HTTPCLIENT - fix this
    //protected void setupEntityMethod(Object src,
    //                                 String encoding,
    //                                 MuleMessage msg,
    //                                 EntityEnclosingMethod postMethod)
    //    throws UnsupportedEncodingException, TransformerException
    //{
    //    // Dont set a POST payload if the body is a Null Payload.
    //    // This way client calls can control if a POST body is posted explicitly
    //    if (!(msg.getPayload() instanceof NullPayload))
    //    {
    //        String outboundMimeType = (String) msg.getProperty(HttpConstants.HEADER_CONTENT_TYPE,
    //            PropertyScope.OUTBOUND);
    //        if (outboundMimeType == null)
    //        {
    //            outboundMimeType = (getEndpoint() != null ? getEndpoint().getMimeType() : null);
    //        }
    //        if (outboundMimeType == null)
    //        {
    //            outboundMimeType = HttpConstants.DEFAULT_CONTENT_TYPE;
    //            logger.info("Content-Type not set on outgoing request, defaulting to: " + outboundMimeType);
    //        }
    //
    //        if (encoding != null && !"UTF-8".equals(encoding.toUpperCase())
    //            && outboundMimeType.indexOf("charset") == -1)
    //        {
    //            outboundMimeType += "; charset=" + encoding;
    //        }
    //
    //        // Ensure that we have a cached representation of the message if we're
    //        // using HTTP 1.0
    //        final String httpVersion = msg.getOutboundProperty(HttpConnector.HTTP_VERSION_PROPERTY,
    //            HttpConstants.HTTP11);
    //        if (HttpConstants.HTTP10.equals(httpVersion))
    //        {
    //            try
    //            {
    //                if (msg instanceof MuleMessageCollection)
    //                {
    //                    src = msg.getPayload(DataType.BYTE_ARRAY_DATA_TYPE);
    //                }
    //                else
    //                {
    //                    src = msg.getPayloadAsBytes();
    //                }
    //            }
    //            catch (final Exception e)
    //            {
    //                throw new TransformerException(this, e);
    //            }
    //        }
    //
    //        if (msg.getOutboundAttachmentNames() != null && msg.getOutboundAttachmentNames().size() > 0)
    //        {
    //            try
    //            {
    //                postMethod.setRequestEntity(createMultiPart(msg, postMethod));
    //                return;
    //            }
    //            catch (final Exception e)
    //            {
    //                throw new TransformerException(this, e);
    //            }
    //        }
    //        if (src instanceof String)
    //        {
    //            postMethod.setRequestEntity(new StringRequestEntity(src.toString(), outboundMimeType,
    //                encoding));
    //            return;
    //        }
    //
    //        if (src instanceof InputStream)
    //        {
    //            postMethod.setRequestEntity(new InputStreamRequestEntity((InputStream) src, outboundMimeType));
    //        }
    //        else if (src instanceof byte[])
    //        {
    //            postMethod.setRequestEntity(new ByteArrayRequestEntity((byte[]) src, outboundMimeType));
    //        }
    //        else if (src instanceof OutputHandler)
    //        {
    //            final MuleEvent event = RequestContext.getEvent();
    //            postMethod.setRequestEntity(new StreamPayloadRequestEntity((OutputHandler) src, event));
    //        }
    //        else
    //        {
    //            final byte[] buffer = SerializationUtils.serialize((Serializable) src);
    //            postMethod.setRequestEntity(new ByteArrayRequestEntity(buffer, outboundMimeType));
    //        }
    //    }
    //    else if (msg.getOutboundAttachmentNames() != null && msg.getOutboundAttachmentNames().size() > 0)
    //    {
    //        try
    //        {
    //            postMethod.setRequestEntity(createMultiPart(msg, postMethod));
    //        }
    //        catch (Exception e)
    //        {
    //            throw new TransformerException(this, e);
    //        }
    //    }
    //}
    //
    //protected void setHeaders(HttpMethod httpMethod, MuleMessage msg) throws TransformerException
    //{
    //    for (String headerName : msg.getOutboundPropertyNames())
    //    {
    //        String headerValue = ObjectUtils.getString(msg.getOutboundProperty(headerName), null);
    //
    //        if (headerName.startsWith(MuleProperties.PROPERTY_PREFIX))
    //        {
    //            // Define Mule headers a custom headers
    //            headerName = new StringBuilder(30).append("X-").append(headerName).toString();
    //            httpMethod.addRequestHeader(headerName, headerValue);
    //
    //        }
    //
    //        else if (!HttpConstants.RESPONSE_HEADER_NAMES.containsKey(headerName)
    //                 && !HttpConnector.HTTP_INBOUND_PROPERTIES.contains(headerName)
    //                 && !HttpConnector.HTTP_COOKIES_PROPERTY.equals(headerName))
    //        {
    //
    //            httpMethod.addRequestHeader(headerName, headerValue);
    //        }
    //    }
    //}
    //
    //protected MultipartRequestEntity createMultiPart(MuleMessage msg, EntityEnclosingMethod method)
    //    throws Exception
    //{
    //    Part[] parts;
    //    int i = 0;
    //    if (msg.getPayload() instanceof NullPayload)
    //    {
    //        parts = new Part[msg.getOutboundAttachmentNames().size()];
    //    }
    //    else
    //    {
    //        parts = new Part[msg.getOutboundAttachmentNames().size() + 1];
    //        parts[i++] = new FilePart("payload", new ByteArrayPartSource("payload", msg.getPayloadAsBytes()));
    //    }
    //
    //    for (final Iterator<String> iterator = msg.getOutboundAttachmentNames().iterator(); iterator.hasNext(); i++)
    //    {
    //        final String attachmentNames = iterator.next();
    //        String fileName = attachmentNames;
    //        final DataHandler dh = msg.getOutboundAttachment(attachmentNames);
    //        if (dh.getDataSource() instanceof StringDataSource)
    //        {
    //            final StringDataSource ds = (StringDataSource) dh.getDataSource();
    //            parts[i] = new StringPart(ds.getName(), IOUtils.toString(ds.getInputStream()));
    //        }
    //        else
    //        {
    //            if (dh.getDataSource() instanceof FileDataSource)
    //            {
    //                fileName = ((FileDataSource) dh.getDataSource()).getFile().getName();
    //            }
    //            else if (dh.getDataSource() instanceof URLDataSource)
    //            {
    //                fileName = ((URLDataSource) dh.getDataSource()).getURL().getFile();
    //                // Don't use the whole file path, just the file name
    //                final int x = fileName.lastIndexOf("/");
    //                if (x > -1)
    //                {
    //                    fileName = fileName.substring(x + 1);
    //                }
    //            }
    //            parts[i] = new FilePart(fileName, new ByteArrayPartSource(fileName,
    //                IOUtils.toByteArray(dh.getInputStream())), dh.getContentType(), null);
    //        }
    //    }
    //
    //    return new MultipartRequestEntity(parts, method.getParams());
    //}
}

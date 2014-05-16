/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.servlet.jetty;

import static org.junit.Assert.assertEquals;

import org.mule.tck.junit4.FunctionalTestCase;
import org.mule.tck.junit4.rule.SystemProperty;
import org.mule.util.ClassUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.junit.Rule;

public abstract class AbstractWebappsTestCase extends FunctionalTestCase
{

    protected static final String WEBAPP_TEST_URL = "test/hello";

    @Rule
    public SystemProperty baseDirProperty = new SystemProperty("baseDir", ClassUtils.getClassPathRoot(getClass()).getPath() + "../../");

    @Override
    protected boolean isStartContext()
    {
        // prepare the test webapp before starting Mule
        return false;
    }

    @Override
    protected void doSetUp() throws Exception
    {
        super.doSetUp();

        final URL url = ClassUtils.getClassPathRoot(getClass());
        File webapps = new File(url.getFile(), "../webapps");
        FileUtils.deleteDirectory(webapps);
        webapps.mkdir();

        FileUtils.copyFile(new File(url.getFile(), "../../src/test/resources/test.war"),
                           new File(webapps, "test.war"));

        muleContext.start();
    }

    protected void sendRequestAndAssertCorrectResponse(String url) throws IOException
    {
        HttpGet method = new HttpGet(url);
        HttpResponse response = HttpClients.createMinimal().execute(method);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
        assertEquals("Hello", response.getEntity().toString());
    }
}



/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.config.builders;

import org.mule.DefaultMuleContext;
import org.mule.api.MuleContext;
import org.mule.extensions.api.ExtensionsManager;
import org.mule.util.ClassUtils;

/**
 * Implementation of {@link org.mule.api.config.ConfigurationBuilder}
 * that register a {@link org.mule.extensions.api.ExtensionsManager} if
 * it's present in the classpath
 *
 * @since 3.6.0
 */
public class ExtensionsManagerConfigurationBuilder extends AbstractConfigurationBuilder
{

    private static final String EXTENSIONS_MANAGER_CLASS_NAME = "org.mule.module.extensions.internal.DefaultExtensionsManager";

    @Override
    protected void doConfigure(MuleContext muleContext) throws Exception
    {
        if (muleContext instanceof DefaultMuleContext &&
            ClassUtils.isClassOnPath(EXTENSIONS_MANAGER_CLASS_NAME, getClass()))
        {
            ((DefaultMuleContext) muleContext).setExtensionsManager(
                    (ExtensionsManager) ClassUtils.instanciateClass(EXTENSIONS_MANAGER_CLASS_NAME));
        }
    }
}
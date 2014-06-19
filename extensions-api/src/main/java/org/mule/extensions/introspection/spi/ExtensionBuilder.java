/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.introspection.spi;

import org.mule.extensions.introspection.api.Extension;

/**
 * Implementation of the Builder design pattern to construct instances of
 * {@link org.mule.extensions.introspection.api.Extension} without coupling
 * to implementations
 * <p/>
 * No user or spi component should ever create a {@link org.mule.extensions.introspection.api.Extension}
 * in a way other than through this builder
 *
 * @since 1.0
 */
public interface ExtensionBuilder extends Builder<Extension>
{

    ExtensionBuilder setName(String name);

    ExtensionBuilder setDescription(String description);

    ExtensionBuilder setVersion(String version);

    ExtensionBuilder setMinMuleVersion(String minMuleVersion);

    ExtensionBuilder addConfiguration(ExtensionConfigurationBuilder configuration);

    ExtensionBuilder addOperation(ExtensionOperationBuilder operation);

    <T extends Object> ExtensionBuilder addCapablity(T capability);

    ExtensionConfigurationBuilder newConfiguration();

    ExtensionOperationBuilder newOperation();

    ExtensionParameterBuilder newParameter();

}

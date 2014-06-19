/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.internal;

import org.mule.extensions.introspection.api.DataType;
import org.mule.extensions.introspection.api.ExtensionParameter;
import org.mule.extensions.introspection.spi.ExtensionParameterBuilder;

import java.util.LinkedList;
import java.util.List;

final class DefaultExtensionParameterBuilder implements ExtensionParameterBuilder
{

    private String name;
    private String description;
    private Class<?> type;
    private List<Class<?>> genericTypes = new LinkedList<>();
    private boolean required = false;
    private boolean dynamic = true;
    private Object defaultValue;

    DefaultExtensionParameterBuilder()
    {
    }

    @Override
    public ExtensionParameterBuilder setName(String name)
    {
        this.name = name;
        return this;
    }

    @Override
    public ExtensionParameterBuilder setDescription(String description)
    {
        this.description = description;
        return this;
    }

    @Override
    public ExtensionParameterBuilder setType(Class<?> type)
    {
        this.type = type;
        return this;
    }

    @Override
    public ExtensionParameterBuilder addGenericType(Class<?> genericType)
    {
        genericTypes.add(genericType);
        return this;
    }

    @Override
    public ExtensionParameterBuilder setRequired(boolean required)
    {
        this.required = required;
        return this;
    }

    @Override
    public ExtensionParameterBuilder setDynamic(boolean dynamic)
    {
        this.dynamic = dynamic;
        return this;
    }

    @Override
    public ExtensionParameterBuilder setDefaultValue(Object defaultValue)
    {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public ExtensionParameter build()
    {
        if (required && defaultValue != null)
        {
            throw new IllegalStateException("If a parameter is required then it cannot have a default value");
        }

        return new ImmutableExtensionParameter(name,
                                               description,
                                               DataType.of(type, (Class<?>[]) genericTypes.toArray()),
                                               required,
                                               dynamic,
                                               defaultValue);
    }
}

/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.internal;

import org.mule.extensions.api.ExtensionsManager;
import org.mule.extensions.introspection.api.Extension;
import org.mule.util.Preconditions;

import com.google.common.collect.ForwardingIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

final class DefaultExtensionsManager implements ExtensionsManager
{

    private final List<Extension> extensions = new LinkedList<>();

    @Override
    public void register(Extension extension)
    {
        Preconditions.checkArgument(extension != null, "Cannot register a null extension");
        extensions.add(extension);
    }


    @Override
    public Iterator<Extension> getExtensions()
    {
        final Iterator<Extension> iterator = new ArrayList(extensions).iterator();

        return new ForwardingIterator<Extension>()
        {
            @Override
            protected Iterator<Extension> delegate()
            {
                return iterator;
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException("Extensions cannot be unregistered");
            }
        };
    }
}

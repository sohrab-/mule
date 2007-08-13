/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.config.spring.parsers.specific.security;

import org.mule.config.spring.parsers.generic.ParentDefinitionParser;

public class SecurityFilterDefinitionParser extends ParentDefinitionParser
{

    public SecurityFilterDefinitionParser()
    {
        addIgnored("type");
    }

}

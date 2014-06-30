/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extensions.api.annotation.capability;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signals that the mule extension should be configurable and
 * usable through a Mule XML config file
 *
 * @since 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Xml
{

    /**
     * The version of the module. Defaults to 1.0.
     */
    String schemaVersion();

    /**
     * Namespace of the module
     */
    String namespace() default "";

    /**
     * Location URI for the schema
     */
    String schemaLocation() default "";


}
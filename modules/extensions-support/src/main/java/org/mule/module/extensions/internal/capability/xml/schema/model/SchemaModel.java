/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.extensions.internal.capability.xml.schema.model;

import org.mule.devkit.model.code.CodeWriter;
import org.mule.util.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public final class SchemaModel
{

    private CodeWriter codeWriter;
    private List<SchemaLocation> schemaLocations;

    public SchemaModel(CodeWriter codeWriter)
    {
        this.codeWriter = codeWriter;
        this.schemaLocations = new ArrayList<SchemaLocation>();
    }

    public void addSchemaLocation(SchemaLocation schemaLocation)
    {
        schemaLocations.add(schemaLocation);
    }

    public void build() throws IOException
    {
        try
        {
            if (!schemaLocations.isEmpty())
            {
                for (SchemaLocation schemaLocation : schemaLocations)
                {
                    if (schemaLocation.getSchema() != null)
                    {
                        buildSchema(schemaLocation);
                    }
                }
                buildSpringHandlersFile();
                buildSpringSchemasFile();
            }
        }
        catch (JAXBException e)
        {
            throw new IOException(e);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new IOException(e);
        }
    }

    private void buildSchema(SchemaLocation schemaLocation) throws JAXBException, IOException
    {
        OutputStream schemaStream = null;
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(Schema.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            NamespaceFilter outFilter = new NamespaceFilter("mule", "http://www.mulesoft.org/schema/mule/core", true);
            OutputFormat format = new OutputFormat();
            format.setIndent(true);
            format.setNewlines(true);
            schemaStream = codeWriter.openBinary(null, schemaLocation.getFileName());

            XMLWriter writer = new XMLWriter(schemaStream, format);
            outFilter.setContentHandler(writer);
            marshaller.marshal(schemaLocation.getSchema(), outFilter);
        }
        finally
        {
            IOUtils.closeQuietly(schemaStream);
        }
    }

    private void buildSpringHandlersFile() throws IOException
    {
        OutputStreamWriter outputStreamWriter = null;
        try
        {

            Set<String> targetNamespaces = new HashSet<String>();
            OutputStream outputStream = codeWriter.openBinary(null, "META-INF/spring.handlers");
            outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");

            for (SchemaLocation schemaLocation : schemaLocations)
            {
                if (schemaLocation.getNamespaceHandler() != null)
                {
                    String targetNamespace = schemaLocation.getTargetNamespace().replace("://", "\\://");
                    if (!targetNamespaces.contains(targetNamespace))
                    {
                        outputStreamWriter.write(targetNamespace + "=" + schemaLocation.getNamespaceHandler() + "\n");
                        targetNamespaces.add(targetNamespace);
                    }
                }
            }

            outputStreamWriter.flush();
        }
        finally
        {
            IOUtils.closeQuietly(outputStreamWriter);
        }
    }

    private void buildSpringSchemasFile() throws IOException
    {
        OutputStreamWriter outputStreamWriter = null;
        try
        {
            OutputStream outputStream = codeWriter.openBinary(null, "META-INF/spring.schemas");
            outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");

            for (SchemaLocation schemaLocation : schemaLocations)
            {
                if (schemaLocation.getFileName() != null && schemaLocation.getLocation() != null)
                {
                    outputStreamWriter.write(schemaLocation.getLocation().replace("://", "\\://") + "=" + schemaLocation.getFileName() + "\n");
                }
            }

            outputStreamWriter.flush();
        }
        finally
        {
            IOUtils.closeQuietly(outputStreamWriter);
        }
    }

    public List<SchemaLocation> getSchemaLocations()
    {
        return schemaLocations;
    }

    public SchemaLocation getSchemaLocationByNamespace(String namespace)
    {
        for (SchemaLocation schemaLocation : schemaLocations)
        {
            if (schemaLocation.getTargetNamespace().equals(namespace))
            {
                return schemaLocation;
            }
        }
        return null;
    }
}

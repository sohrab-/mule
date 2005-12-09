/*
 * $Header$
 * $Revision$
 * $Date$
 */
package org.mule.ide.internal.core.model;

import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.mule.ide.core.MuleCorePlugin;
import org.mule.ide.core.model.IMuleConfiguration;
import org.mule.ide.core.model.IMuleModel;
import org.mule.schema.util.MuleResourceFactoryImpl;

/**
 * Default Mule configuration implementation.
 */
public class MuleConfiguration extends MuleModelElement implements IMuleConfiguration {

	/** The parent model */
	private IMuleModel parent;

	/** Unique id */
	private String id;

	/** Description */
	private String description;

	/** The relative path to the config file */
	private String relativePath;

	/** Project relative path to config file */
	private IPath filePath;

	/** The resource handle for the EMF config */
	private Resource resource;

	/** Error indicating that a config file was not found */
	private static final String ERROR_CONFIG_NOT_FOUND = "The Mule configuration file was not found: ";

	/** Error indicating that a config file was not able to be loaded */
	private static final String ERROR_LOADING_CONFIG = "Unable to load Mule configuration file: ";

	/**
	 * Create a new Mule configuration.
	 * 
	 * @param parent the parent model
	 * @param id the unique id
	 * @param description the description
	 * @param relativePath the project-relative path to the config file
	 */
	public MuleConfiguration(IMuleModel parent, String id, String description, String relativePath) {
		this.parent = parent;
		this.setId(id);
		this.setDescription(description);
		this.relativePath = relativePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.ide.core.model.IMuleConfiguration#refresh()
	 */
	public IStatus refresh() {
		setStatus(Status.OK_STATUS);
		IFile configFile = parent.getProject().getFile(relativePath);
		setFilePath(configFile.getFullPath());
		if (!configFile.exists()) {
			setResource(null);
			setStatus(MuleCorePlugin.getDefault().createErrorStatus(
					ERROR_CONFIG_NOT_FOUND + relativePath, null));
		} else {
			try {
				Resource.Factory factory = new MuleResourceFactoryImpl();
				setResource(factory.createResource(null));
				getResource().load(configFile.getContents(), Collections.EMPTY_MAP);
			} catch (Exception e) {
				setStatus(MuleCorePlugin.getDefault().createErrorStatus(
						ERROR_LOADING_CONFIG + relativePath, e));
			}
		}
		return getStatus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.ide.core.model.IMuleConfiguration#getLabel()
	 */
	public String getLabel() {
		return getRelativePath() + " [" + getDescription() + "]";
	}

	/**
	 * Sets the 'id' field.
	 * 
	 * @param id The 'id' value.
	 */
	protected void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.ide.core.model.IMuleConfiguration#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the 'description' field.
	 * 
	 * @param description The 'description' value.
	 */
	protected void setDescription(String description) {
		this.description = description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.ide.core.model.IMuleConfiguration#getDescription()
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the 'filePath' field.
	 * 
	 * @param filePath The 'filePath' value.
	 */
	protected void setFilePath(IPath filePath) {
		this.filePath = filePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.ide.core.model.IMuleConfiguration#getFilePath()
	 */
	public IPath getFilePath() {
		return filePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.ide.core.model.IMuleConfiguration#getRelativePath()
	 */
	public String getRelativePath() {
		return relativePath;
	}

	/**
	 * Sets the 'resource' field.
	 * 
	 * @param resource The 'resource' value.
	 */
	protected void setResource(Resource resource) {
		this.resource = resource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.ide.core.model.IMuleConfiguration#getResource()
	 */
	public Resource getResource() {
		return resource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mule.ide.core.model.IMuleModelElement#getMuleModel()
	 */
	public IMuleModel getMuleModel() {
		return this.parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		IMuleConfiguration other = (IMuleConfiguration) o;
		return getRelativePath().compareTo(other.getRelativePath());
	}
}
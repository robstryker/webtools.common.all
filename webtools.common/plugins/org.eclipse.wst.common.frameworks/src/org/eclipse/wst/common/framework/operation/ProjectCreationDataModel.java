/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Oct 27, 2003
 * 
 * To change the template for this generated file go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 * Comments
 */
package org.eclipse.wst.common.framework.operation;

import java.io.File;

import org.eclipse.core.internal.localstore.CoreFileSystemLibrary;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.internal.utils.Policy;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclispe.wst.common.framework.plugin.WTPCommonMessages;
import org.eclispe.wst.common.framework.plugin.WTPCommonPlugin;


/**
 * @author jsholl
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ProjectCreationDataModel extends WTPOperationDataModel {
	/**
	 * type String, project name to create, required
	 */
	public static final String PROJECT_NAME = "ProjectCreationDataModel.PROJECT_NAME"; //$NON-NLS-1$

	/**
	 * type String, the location on the file system to create the project; optional; default is
	 * default location
	 */
	public static final String PROJECT_LOCATION = "ProjectCreationDataModel.PROJECT_LOCATION"; //$NON-NLS-1$

	/**
	 * type String [], list of all natures to add, optional, no default
	 */
	public static final String PROJECT_NATURES = "ProjectCreationDataModel.PROJECT_NATURES"; //$NON-NLS-1$

	public static ProjectCreationDataModel createProjectCreationDataModel(String projectName) {
		ProjectCreationDataModel dataModel = new ProjectCreationDataModel();
		dataModel.setProperty(PROJECT_NAME, projectName);
		return dataModel;
	}

	public WTPOperation getDefaultOperation() {
		return new ProjectCreationOperation(this);
	}

	protected void initValidBaseProperties() {
		super.initValidBaseProperties();
		addValidBaseProperty(PROJECT_NAME);
		addValidBaseProperty(PROJECT_LOCATION);
		addValidBaseProperty(PROJECT_NATURES);
	}

	protected Object getDefaultProperty(String propertyName) {
		if (PROJECT_LOCATION.equals(propertyName)) {
			return getDefaultLocation();
		}
		return super.getDefaultProperty(propertyName);
	}

	protected boolean doSetProperty(String propertyName, Object propertyValue) {
		if (propertyValue != null && propertyName.equals(PROJECT_LOCATION)) {
			IPath path = getRootLocation();
			if (path.equals(new Path((String) propertyValue))) {
				setProperty(propertyName, null);
				return false;
			}
		}
		Object oldValue = null;
		if (propertyName.equals(PROJECT_NAME)) {
			oldValue = getProperty(PROJECT_NAME);
		}
		boolean notify = super.doSetProperty(propertyName, propertyValue);
		if (propertyName.equals(PROJECT_NAME) && !isSet(PROJECT_LOCATION)) {
			notifyListeners(PROJECT_NAME, oldValue, propertyValue);
			notifyListeners(PROJECT_LOCATION, null, null);
			return false;
		}
		return notify;
	}

	private String getDefaultLocation() {
		IPath path = getRootLocation();
		String projectName = (String) getProperty(PROJECT_NAME);
		if (projectName != null)
			path = path.append(projectName);
		return path.toOSString();
	}

	private IPath getRootLocation() {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation();
	}

	public IProjectDescription getProjectDescription() {
		String projectName = (String) getProperty(PROJECT_NAME);
		IProjectDescription desc = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
		if (isSet(PROJECT_LOCATION)) {
			String projectLocation = (String) getProperty(ProjectCreationDataModel.PROJECT_LOCATION);
			desc.setLocation(new Path(projectLocation));
		}
		return desc;
	}

	public IProject getProject() {
		String projectName = (String) getProperty(PROJECT_NAME);
		return (null != projectName && projectName.length() > 0) ? ResourcesPlugin.getWorkspace().getRoot().getProject(projectName) : null;
	}

	protected IStatus doValidateProperty(String propertyName) {

		if (propertyName.equals(PROJECT_NAME)) {
			IStatus status = validateName();
			if (!status.isOK())
				return status;
		}
		if (propertyName.equals(PROJECT_LOCATION)) {
			IStatus status = validateLocation();
			if (!status.isOK())
				return status;
		}
		if (propertyName.equals(PROJECT_LOCATION) || propertyName.equals(PROJECT_NAME)) {
			String projectName = getStringProperty(PROJECT_NAME);
			String projectLoc = getStringProperty(PROJECT_LOCATION);
			return validateExisting(projectName, projectLoc);
		}
		return super.doValidateProperty(propertyName);
	}

	/**
	 * @param projectName
	 * @param projectLoc
	 * @todo Generated comment
	 */
	private IStatus validateExisting(String projectName, String projectLoc) {
		if (projectName != null && !projectName.equals("")) {//$NON-NLS-1$
			File file = new File(projectLoc);
			if (file.exists()) {
				if (file.isDirectory()) {
					File dotProject = new File(file, ".project");//$NON-NLS-1$
					if (dotProject.exists()) {
						return WTPCommonPlugin.createErrorStatus(WTPCommonPlugin.getResourceString(WTPCommonMessages.PROJECT_EXISTS_AT_LOCATION_ERROR, new Object[]{file.toString()}));
					}
				}
			}
		}
		return OK_STATUS;
	}

	private IStatus validateName() {
		String name = getStringProperty(PROJECT_NAME);
		IStatus status = validateProjectName(name);
		if (!status.isOK())
			return status;
		if (getProject().exists())
			return WTPCommonPlugin.createErrorStatus(WTPCommonPlugin.getResourceString(WTPCommonMessages.PROJECT_EXISTS_ERROR, new Object[]{name}));

		if (!CoreFileSystemLibrary.isCaseSensitive()) {
			//now look for a matching case variant in the tree
			IResource variant = ((Project) getProject()).findExistingResourceVariant(getProject().getFullPath());
			if (variant != null)
				return WTPCommonPlugin.createErrorStatus(Policy.bind("resources.existsDifferentCase", variant.getFullPath().toString())); //$NON-NLS-1$
		}

		return OK_STATUS;
	}

	private IStatus validateLocation() {
		if (isSet(PROJECT_LOCATION)) {
			String loc = (String) getProperty(PROJECT_LOCATION);
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IPath path = new Path(loc);
			return workspace.validateProjectLocation(getProject(), path);
		}
		return OK_STATUS;
	}
}
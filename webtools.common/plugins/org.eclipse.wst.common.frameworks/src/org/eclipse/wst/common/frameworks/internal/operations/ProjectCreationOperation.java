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
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.eclipse.wst.common.frameworks.internal.operations;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;

/**
 * @deprecated
 * @see org.eclipse.wst.common.frameworks.internal.operations.IProjectCreationProperties
 */
public class ProjectCreationOperation extends WTPOperation {

	public ProjectCreationOperation(ProjectCreationDataModel dataModel) {
		super(dataModel);
	}

	protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
		try {
			ProjectCreationDataModel dataModel = (ProjectCreationDataModel) operationDataModel;

			IProjectDescription desc = dataModel.getProjectDescription();
			IProject project = dataModel.getProject();
			if (!project.exists()) {
				project.create(desc, new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));
			}

			if (monitor.isCanceled())
				throw new OperationCanceledException();
			project.open(new SubProgressMonitor(monitor, IProgressMonitor.UNKNOWN));

			String[] natureIds = (String[]) dataModel.getProperty(ProjectCreationDataModel.PROJECT_NATURES);
			if (null != natureIds) {
				desc = project.getDescription();
				desc.setNatureIds(natureIds);
				project.setDescription(desc, monitor);
			}
		} finally {
			monitor.done();
		}
		if (monitor.isCanceled())
			throw new OperationCanceledException();
	}
}
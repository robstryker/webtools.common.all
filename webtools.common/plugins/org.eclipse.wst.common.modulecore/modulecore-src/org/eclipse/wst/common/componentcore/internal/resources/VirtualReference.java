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
package org.eclipse.wst.common.componentcore.internal.resources;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;

public class VirtualReference implements IVirtualReference {
	
	private IVirtualComponent referencedComponent;
	private IVirtualComponent enclosingComponent;
	private IPath runtimePath;
	private int dependencyType;

	public VirtualReference() {
		
	}
	
	
	public VirtualReference(IVirtualComponent anEnclosingComponent, IVirtualComponent aReferencedComponent) {
		this(anEnclosingComponent, aReferencedComponent, new Path(String.valueOf(IPath.SEPARATOR)), DEPENDENCY_TYPE_USES); 
	}
	
	public VirtualReference(IVirtualComponent anEnclosingComponent, IVirtualComponent aReferencedComponent, IPath aRuntimePath) {
		this(anEnclosingComponent, aReferencedComponent, aRuntimePath, DEPENDENCY_TYPE_USES);
	}

	public VirtualReference(IVirtualComponent anEnclosingComponent, IVirtualComponent aReferencedComponent, IPath aRuntimePath, int aDependencyType) {
		enclosingComponent = anEnclosingComponent;
		referencedComponent = aReferencedComponent;
		runtimePath = aRuntimePath;
		dependencyType = aDependencyType;
	}

	public void create(int updateFlags, IProgressMonitor aMonitor) { 

	}

	public void setRuntimePath(IPath aRuntimePath) { 
		runtimePath = aRuntimePath;
	}

	public IPath getRuntimePath() { 
		return runtimePath;
	}

	public void setDependencyType(int aDependencyType) {
		dependencyType = aDependencyType;
	}

	public int getDependencyType() { 
		return dependencyType;
	}

	public boolean exists() { 
		return false;
	}

	public IVirtualComponent getEnclosingComponent() { 
		return enclosingComponent;
	}

	public IVirtualComponent getReferencedComponent() { 
		return referencedComponent;
	}

}

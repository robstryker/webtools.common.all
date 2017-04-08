/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik
 ******************************************************************************/

package org.eclipse.jst.common.project.facet.core.internal;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.common.project.facet.core.IClasspathProvider;
import org.eclipse.jst.common.project.facet.core.StandardJreRuntimeComponent;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class StandardJreClasspathProvider 

    implements IClasspathProvider 
    
{
	private static final IProjectFacet JAVA_FACET = ProjectFacetsManager.getProjectFacet("jst.java");

	private IRuntimeComponent rc;

	public StandardJreClasspathProvider( final IRuntimeComponent rc ) 
	{
		this.rc = rc;
	}

	public List<IClasspathEntry> getClasspathEntries( final IProjectFacetVersion fv ) 
	{
		if( fv.getProjectFacet() == JAVA_FACET ) 
		{
		    final IVMInstall vmInstall = getVMInstall();
		    
		    if( vmInstall != null )
		    {
		        final IPath cpEntryPath = JavaRuntime.newJREContainerPath( vmInstall );
		        final IClasspathEntry cpEntry = JavaCore.newContainerEntry( cpEntryPath );
		        
		        return Collections.singletonList( cpEntry );
		    }
		}
		
		return null;
	}
	
	private IVMInstall getVMInstall()
	{
	    final String vmInstallTypeId 
	        = this.rc.getProperty( StandardJreRuntimeComponent.PROP_VM_INSTALL_TYPE );
	    
	    final String vmInstallId
	        = this.rc.getProperty( StandardJreRuntimeComponent.PROP_VM_INSTALL_ID );
	    
	    if( vmInstallTypeId == null || vmInstallId == null )
	    {
	        return null;
	    }
	    
	    final IVMInstallType vmInstallType = JavaRuntime.getVMInstallType( vmInstallTypeId );
	    
	    if( vmInstallType == null )
	    {
	        return null;
	    }
	    
	    return vmInstallType.findVMInstall( vmInstallId );
	}

	public static final class Factory
	
	    implements IAdapterFactory 
	
	{
		private static final Class[] ADAPTER_TYPES = { IClasspathProvider.class };

		public Class[] getAdapterList() 
		{
            return ADAPTER_TYPES;
        }

		public Object getAdapter( final Object adaptable, 
		                          final Class adapterType ) 
		{
			IRuntimeComponent rc = (IRuntimeComponent) adaptable;
			return new StandardJreClasspathProvider(rc);
		}
	}
	
}
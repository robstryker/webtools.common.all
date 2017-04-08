/******************************************************************************
 * Copyright (c) 2008 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public interface IFacetedProjectWorkingCopy

    extends IFacetedProjectBase
    
{
    String getProjectName();
    
    // Only works if the model wasn't created based on an existing project.
    void setProjectName( String name );
    
    IPath getProjectLocation();
    void setProjectLocation( IPath location );
    
    IFacetedProject getFacetedProject();
    
    Map<IProjectFacet,SortedSet<IProjectFacetVersion>> getAvailableFacets();

    boolean isFacetAvailable( IProjectFacet f );
    
    boolean isFacetAvailable( IProjectFacetVersion fv );
    
    SortedSet<IProjectFacetVersion> getAvailableVersions( IProjectFacet f );
    
    IProjectFacetVersion getHighestAvailableVersion( IProjectFacet f );
    
    void setFixedProjectFacets( Set<IProjectFacet> fixed );
    
    void setProjectFacets( Set<IProjectFacetVersion> facets );
    
    void addProjectFacet( IProjectFacetVersion fv );
    
    void removeProjectFacet( IProjectFacet f );
    
    void removeProjectFacet( IProjectFacetVersion fv );
    
    void changeProjectFacetVersion( IProjectFacetVersion fv );
    
    Set<IPreset> getAvailablePresets();
    
    IPreset getSelectedPreset();
    
    void setSelectedPreset( String presetId );
    
    IPreset getDefaultConfiguration();
    
    IPreset getMinimalConfiguration();

    Set<IRuntime> getTargetableRuntimes();
    
    void refreshTargetableRuntimes();
    
    void setTargetedRuntimes( Set<IRuntime> runtimes );
    
    void addTargetedRuntime( IRuntime runtime );
    
    void removeTargetedRuntime( IRuntime runtime );
    
    void setPrimaryRuntime( IRuntime runtime );
    
    Set<Action> getProjectFacetActions();
    
    Action getProjectFacetAction( IProjectFacet facet );
    
    void setProjectFacetActionConfig( IProjectFacet facet,
                                      Object newActionConfig );    
    
    void commitChanges( IProgressMonitor monitor )
    
        throws CoreException;
    
    void mergeChanges( IFacetedProjectWorkingCopy fpjwc );
    
    void revertChanges();
    
    IFacetedProjectWorkingCopy clone();
    
    void dispose();

}

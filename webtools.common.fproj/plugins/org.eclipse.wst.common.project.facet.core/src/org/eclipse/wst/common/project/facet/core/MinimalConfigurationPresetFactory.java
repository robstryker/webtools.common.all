/******************************************************************************
 * Copyright (c) 2008 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.internal.DefaultFacetsExtensionPoint;

/**
 * Preset factory for the <code>minimal.configuration</code> preset. This preset only contains
 * fixed facets. The version of the facets are calculated as follows: 
 * 
 * <ol>
 *   <li>If a runtime is selected, the versions are looked up using 
 *     {@see IRuntime.getDefaultFacets(Set)}.</li>
 *   <li>If no runtime is selected, this versions are the default versions as specified 
 *     by {@see IProjectFacet.getDefaultVersion()}.
 * </ol>
 * 
 * @since 3.0
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public class MinimalConfigurationPresetFactory

    implements IPresetFactory
    
{
    public static final String PRESET_ID = "minimal.configuration"; //$NON-NLS-1$
    
    public PresetDefinition createPreset( final String presetId,
                                          final Map<String,Object> context ) 
    
        throws CoreException
        
    {
        final IFacetedProjectBase fproj 
            = (IFacetedProjectBase) context.get( IDynamicPreset.CONTEXT_KEY_FACETED_PROJECT );
    
        final Set<IProjectFacetVersion> facets = new HashSet<IProjectFacetVersion>();
        
        final Set<IProjectFacetVersion> defaultFacets 
            = DefaultFacetsExtensionPoint.getDefaultFacets( fproj );
        
        for( IProjectFacet f : fproj.getFixedProjectFacets() )
        {
            facets.add( findProjectFacetVersion( defaultFacets, f ) );
        }
        
        return new PresetDefinition( Resources.presetLabel, Resources.presetDescription, facets );
    }
    
    private static IProjectFacetVersion findProjectFacetVersion( final Set<IProjectFacetVersion> facets,
                                                                 final IProjectFacet facet )
    {
        for( IProjectFacetVersion fv : facets )
        {
            if( fv.getProjectFacet() == facet )
            {
                return fv;
            }
        }
        
        throw new IllegalStateException();
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String presetLabel;
        public static String presetDescription;
        
        static
        {
            initializeMessages( MinimalConfigurationPresetFactory.class.getName(), 
                                Resources.class );
        }
    }
    
}

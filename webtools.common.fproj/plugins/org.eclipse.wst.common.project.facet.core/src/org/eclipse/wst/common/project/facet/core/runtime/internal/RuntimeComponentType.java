/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/

package org.eclipse.wst.common.project.facet.core.runtime.internal;

import java.util.Collections;
import java.util.Comparator;

import org.eclipse.wst.common.project.facet.core.VersionFormatException;
import org.eclipse.wst.common.project.facet.core.internal.Versionable;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentType;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public final class RuntimeComponentType

    extends Versionable
    implements IRuntimeComponentType
    
{
    private static final IVersionAdapter VERSION_ADAPTER = new IVersionAdapter()
    {
        public String adapt( final Object obj )
        {
            return ( (IRuntimeComponentVersion) obj ).getVersionString();
        }
    };

    private String id;
    private String plugin;
    private String iconPath;
    
    public String getId()
    {
        return this.id;
    }
    
    void setId( final String id )
    {
        this.id = id;
    }
    
    public String getPluginId()
    {
        return this.plugin;
    }
    
    void setPluginId( final String plugin )
    {
        this.plugin = plugin;
    }
    
    void addVersion( final IRuntimeComponentVersion ver )
    {
        this.versions.add( ver.getVersionString(), ver );
    }

    public IRuntimeComponentVersion getVersion( final String version )
    {
        final IRuntimeComponentVersion rcv
            = (IRuntimeComponentVersion) this.versions.get( version );
        
        if( rcv == null )
        {
            final String msg 
                = "Could not find version " + version + " of runtime component " 
                  + this.id + ".";
            
            throw new IllegalArgumentException( msg );
        }
        
        return rcv;
    }

    public IRuntimeComponentVersion getLatestVersion()
    
        throws VersionFormatException
        
    {
        final Comparator comp = getVersionComparator( true, VERSION_ADAPTER );
        final Object max = Collections.max( this.versions, comp );
        
        return (IRuntimeComponentVersion) max;
    }
    
    public String getIconPath()
    {
        return this.iconPath;
    }
    
    void setIconPath( final String iconPath )
    {
        this.iconPath = iconPath;
    }
    
    protected IVersionAdapter getVersionAdapter()
    {
        return VERSION_ADAPTER;
    }
    
}
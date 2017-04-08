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

package org.eclipse.wst.common.project.facet.core.runtime.classpath;

import java.util.List;

import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;

/**
 * The iterface implemented by a runtime component adapter in order to provide
 * classpath entries for project facets. For convenience, the runtime can also
 * be adapted to this interface. That adapter will delegate to the runtime 
 * components in the order that they are listed in the runtime. The first one 
 * that can provide classpath entries for the specified project facet wins. 
 * 
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IClasspathProvider
{
    /**
     * Returns the classpath entries for the specified project facet.
     * 
     * @param fv the project facet version
     * @return returns the classpath entries for the specified project facet
     *   (element type: {@see org.eclipse.jdt.core.IClasspathEntry}), or
     *   <code>null</code> if this provider does not provide classpath entries
     *   for the given project facet
     */
    
    List getClasspathEntries( IProjectFacetVersion fv );
    
}

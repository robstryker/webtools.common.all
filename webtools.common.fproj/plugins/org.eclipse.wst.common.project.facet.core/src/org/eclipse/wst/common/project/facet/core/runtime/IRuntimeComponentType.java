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

package org.eclipse.wst.common.project.facet.core.runtime;

import java.util.Comparator;
import java.util.Set;

/**
 * @author <a href="mailto:kosta@bea.com">Konstantin Komissarchik</a>
 */

public interface IRuntimeComponentType
{
    String getId();
    String getPluginId();
    Set getVersions();
    boolean hasVersion( String version );
    IRuntimeComponentVersion getVersion( String version );
    IRuntimeComponentVersion getLatestVersion();
    Comparator getVersionComparator();
    String getIconPath();
}

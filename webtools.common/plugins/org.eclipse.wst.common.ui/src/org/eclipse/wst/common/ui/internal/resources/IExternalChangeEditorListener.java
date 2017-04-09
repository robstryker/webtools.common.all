/*
* Copyright (c) 2002 IBM Corporation and others.
* All rights reserved.   This program and the accompanying materials
* are made available under the terms of the Common Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/cpl-v10.html
* 
* Contributors:
*   IBM - Initial API and implementation
*   Jens Lukowski/Innoopract - initial renaming/restructuring
* 
*/
package org.eclipse.wst.common.ui.internal.resources;

import org.eclipse.core.runtime.IPath;

public interface IExternalChangeEditorListener 
{
  public static final String copyright = "(c) Copyright IBM Corporation 2000, 2002.";
  public void handleEditorInputChanged();
  public void handleEditorPathChanged(IPath newPath);  
  public void reload();
}

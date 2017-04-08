/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.common.frameworks.internal.datamodel.ui;

import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.ui.WTPCommonUIResourceHandler;

/**
 * This class is EXPERIMENTAL and is subject to substantial changes.
 */
public abstract class DataModelExtendedWizardPage extends DataModelWizardPage implements IDMExtendedWizardPage {

	private String id;

	protected DataModelExtendedWizardPage(IDataModel model, String pageName) {
		super(model, pageName);
	}

	/**
	 * @return Returns the id.
	 */
	public final String getGroupID() {
		return id;
	}

	/**
	 * Will only set the id once. Further invocations will be ignored.
	 * 
	 * @param id
	 *            The id to set.
	 */
	public final void setGroupID(String id) {
		if (this.id != null) {
			new Exception(WTPCommonUIResourceHandler.getString("ExtendedWizardPage_ERROR_0")).printStackTrace(); //$NON-NLS-1$
			return;
		}
		else if (id == null) {
			new Exception(WTPCommonUIResourceHandler.getString("ExtendedWizardPage_ERROR_1")).printStackTrace(); //$NON-NLS-1$
			return;
		}
		this.id = id;
	}
}
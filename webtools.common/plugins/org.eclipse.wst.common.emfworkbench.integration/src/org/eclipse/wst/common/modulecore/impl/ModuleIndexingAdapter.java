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
package org.eclipse.wst.common.modulecore.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.common.modulecore.ModuleCorePackage;
import org.eclipse.wst.common.modulecore.WorkbenchModule;

/**
 * <p>
 * The following class is experimental until fully documented.
 * </p>
 */
public class ModuleIndexingAdapter extends AdapterImpl {

	private static final Class MODULE_INDEXING_ADAPTER_CLASS = ModuleIndexingAdapter.class;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.notify.impl.AdapterImpl#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
	public void notifyChanged(Notification aNotification) {
		if (aNotification.getNotifier() instanceof EObject) {
			EObject notifier = (EObject) aNotification.getNotifier();
			if (notifier.eClass().getClassifierID() == ModuleCorePackage.PROJECT_MODULES) {
				ProjectModulesImpl projectModules = (ProjectModulesImpl) notifier;
				synchronized (projectModules.getModulesIndex()) {
					switch (aNotification.getEventType()) {
						case Notification.ADD :
							WorkbenchModule module = (WorkbenchModule) aNotification.getNewValue();
							projectModules.getModulesIndex().put(module.getDeployedName(), module);
					}
				} 
			}
		} 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.common.notify.impl.AdapterImpl#isAdapterForType(java.lang.Object)
	 */
	public boolean isAdapterForType(Object type) {
		return type == MODULE_INDEXING_ADAPTER_CLASS;
	}
}
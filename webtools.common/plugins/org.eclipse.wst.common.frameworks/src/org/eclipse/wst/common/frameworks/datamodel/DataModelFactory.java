/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.common.frameworks.datamodel;

import org.eclipse.wst.common.frameworks.internal.datamodel.DataModelExtensionReader;
import org.eclipse.wst.common.frameworks.internal.datamodel.DataModelImpl;

public class DataModelFactory {

	private static DataModelExtensionReader reader;

	private DataModelFactory(){
	}
	
	/**
	 * Looks up the appropriate IDataModelProvider by the specified id and constructs a new
	 * IDataModel. If the IDataModelProvider is not found then a RuntimeException is logged and null
	 * is returned.
	 * 
	 * @param dataModelProviderID
	 *            the id of the IDataModelProvider
	 * @return a new IDataModel
	 */
	public static IDataModel createDataModel(String dataModelProviderID) {
		IDataModelProvider provider = loadProvider(dataModelProviderID);
		if (provider == null)
			return null;
		return createDataModel(provider);
	}

	private static IDataModelProvider loadProvider(String id) {
		if (null == reader) {
			reader = new DataModelExtensionReader();
		}
		return reader.getProvider(id);
	}

	/**
	 * Looks up the appropriate dataModelProviderIDs by the specified dataModelProviderKindID.
	 * 
	 * @param dataModelProviderKindID
	 *            the String id of the dataModelProviderKindID
	 * 
	 * @return the array of valid dataModelProviderIDs or an empty array if there are none.
	 */
	public static String[] getDataModelProviderIDsForKind(String dataModelProviderKindID) {
		String[] validProviderIDs = loadProviderForProviderKind(dataModelProviderKindID);
		return null != validProviderIDs ? validProviderIDs : new String[0];
	}


	private static String [] loadProviderForProviderKind(String providerKind) {
		if (null == reader) {
			reader = new DataModelExtensionReader();
		}
		return reader.getProviderDescriptorsForProviderKind(providerKind);
	}

	/**
	 * Looks up the appropriate IDataModelProvider using the name of the specified class. This
	 * method is equavalent to <code>createDataModel(dataModelProviderClassID.getName())</code>.
	 * 
	 * @param dataModelProviderClass
	 *            the class whose name is the id of the IDataModelProvider
	 * @return a new IDataModel
	 */
	public static IDataModel createDataModel(Class dataModelProviderID) {
		return createDataModel(dataModelProviderID.getName());
	}

	/**
	 * Creates a new IDataModel using the the specified instance of an IDataModelProvider
	 * 
	 * @param dataModelProviderInstance
	 * @return a new IDataModel
	 */
	public static IDataModel createDataModel(IDataModelProvider provider) {
		if (null == provider) {
			throw new NullPointerException();
		}
		DataModelImpl dataModel = new DataModelImpl(provider);
		return dataModel;
	}

}
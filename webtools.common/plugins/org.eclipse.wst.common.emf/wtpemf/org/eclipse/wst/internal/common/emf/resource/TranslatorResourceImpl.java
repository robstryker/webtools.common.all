/***************************************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 **************************************************************************************************/
package org.eclipse.wst.internal.common.emf.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.wst.internal.common.emf.utilities.StringUtil;
import org.xml.sax.EntityResolver;

public abstract class TranslatorResourceImpl extends ReferencedXMIResourceImpl implements TranslatorResource {
	static final public EStructuralFeature ID_FEATURE = EcorePackage.eINSTANCE.getEClass_EIDAttribute();
	protected static final String DEFAULT_ENCODING = "UTF-8"; //$NON-NLS-1$
	protected static final String DEFAULT_VERSION = "1.0"; //$NON-NLS-1$
	protected Renderer renderer;
	/**
	 * The public Id to use at the head of the document.
	 */
	protected String publicId;
	/**
	 * The system Id to use at the head of the document.
	 */
	protected String systemId;
	protected String xmlVersion;
	//	Default the resources to J2EE 1.4
	protected int versionID;

	/**
	 * @deprecated since 4/29/2003 - used for compatibility Subclasses should be using the Renderers
	 *             and translator framework
	 */
	public TranslatorResourceImpl() {
		super();
	}

	/**
	 * @deprecated since 4/29/2003 - used for compatibility Subclasses should be using the Renderers
	 *             and translator framework
	 */
	public TranslatorResourceImpl(URI uri) {
		super(uri);
	}

	public TranslatorResourceImpl(URI uri, Renderer aRenderer) {
		super(uri);
		setRenderer(aRenderer);
		versionID = getDefaultVersionID();
	}

	public TranslatorResourceImpl(Renderer aRenderer) {
		super();
		setRenderer(aRenderer);
	}

	public java.lang.String getEncoding() {
		if (super.getEncoding() == null)
			setEncoding(DEFAULT_ENCODING);
		return super.getEncoding();
	}

	public String getPublicId() {
		return publicId;
	}

	/**
	 * Return the first element in the EList.
	 */
	public EObject getRootObject() {
		if (contents == null || contents.isEmpty())
			return null;
		return (EObject) getContents().get(0);
	}

	public String getSystemId() {
		return systemId;
	}

	public void setDoctypeValues(String aPublicId, String aSystemId) {
		boolean changed = !(StringUtil.stringsEqual(publicId, aPublicId) && StringUtil.stringsEqual(systemId, aSystemId));
		publicId = aPublicId;
		systemId = aSystemId;
		if (changed) {
			eNotify(new NotificationImpl(Notification.SET, null, null) {
				public Object getFeature() {
					return DOC_TYPE_FEATURE;
				}

				public Object getNotifier() {
					return TranslatorResourceImpl.this;
				}
			});
		}
	}

	/**
	 * Returns the xmlVersion.
	 * 
	 * @return String
	 */
	public String getXMLVersion() {
		if (xmlVersion == null)
			xmlVersion = DEFAULT_VERSION;
		return xmlVersion;
	}

	/**
	 * Sets the xmlVersion.
	 * 
	 * @param xmlVersion
	 *            The xmlVersion to set
	 */
	public void setXMLVersion(String xmlVersion) {
		this.xmlVersion = xmlVersion;
	}

	protected void basicDoLoad(InputStream inputStream, Map options) throws IOException {
		//long start = System.currentTimeMillis();
		boolean isTrackingMods = isTrackingModification();
		try {
			if (isTrackingMods)
				setTrackingModification(false);
			renderer.doLoad(inputStream, options);
		} finally {
			if (isTrackingMods)
				setTrackingModification(true);
		}
		//long end = System.currentTimeMillis();
		//recordTime("Load", start, end);
	}

	public void save(Map options) throws IOException {
		if (renderer.useStreamsForIO()) {
			super.save(options);
		} else {
			doSave(null, options);
			notifySaved();
		}
	}

	/**
	 * @see com.ibm.etools.xmi.helpers.CompatibilityXMIResourceImpl#doSave(OutputStream, Map)
	 */
	public void doSave(OutputStream outputStream, Map options) throws IOException {
		//long start = System.currentTimeMillis();
		renderer.doSave(outputStream, options);
		setModified(false);
		//long end = System.currentTimeMillis();
		//recordTime("Save", start, end);
	}

	//	private void recordTime(String type, long start, long end) {
	//		System.out.println(renderer.getClass().getName() + "\t" + type + "\t" + (end - start) +
	// "\t\t\tms" + "\t" + (( this.getVersionID()) / 10.0) + "\t" + this); //$NON-NLS-1$
	// //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	//	}

	/**
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#doUnload()
	 */
	protected void doUnload() {
		renderer.preUnload();
		super.doUnload();
	}

	public String toString() {
		return getClass().getName() + getURI().toString();
	}

	/**
	 * Returns the renderer.
	 * 
	 * @return Renderer
	 */
	public Renderer getRenderer() {
		return renderer;
	}

	/**
	 * Sets the renderer.
	 * 
	 * @param renderer
	 *            The renderer to set
	 */
	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
		if (renderer.getResource() != this)
			renderer.setResource(this);
	}

	/**
	 * For compatibility of old subtype resources, returns the super implementation
	 * 
	 * @return
	 */
	protected EList primGetContents() {
		return super.getContents();
	}

	public EList getContents() {
		if (contents == null) {
			initializeContents();
		}
		return contents;
	}

	protected void initializeContents() {
		contents = new ResourceImpl.ContentsEList() {
			public boolean add(Object object) {
				renderer.prepareToAddContents();
				return super.add(object);
			}

			public boolean addAll(Collection collection) {
				renderer.prepareToAddContents();
				return super.addAll(collection);
			}
		};
	}

	public void setDefaults() {
		if (systemId != null && publicId != null)
			return;
		String pubId = publicId == null ? getDefaultPublicId() : publicId;
		String sysId = systemId == null ? getDefaultSystemId() : systemId;
		setDoctypeValues(pubId, sysId);
	}

	/**
	 * @see org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl#init()
	 */
	protected void init() {
		super.init();
		setEncoding(DEFAULT_ENCODING);
	}

	/**
	 * Returns null by default; subclasses can override
	 * 
	 * @see com.ibm.etools.emf2xml.TranslatorResource#createEntityResolver()
	 */
	public EntityResolver getEntityResolver() {
		return null;
	}

	protected abstract String getDefaultPublicId();

	protected abstract String getDefaultSystemId();

	protected abstract int getDefaultVersionID();

	/**
	 * @return
	 */
	public int getVersionID() {
		return versionID;
	}

	/**
	 * @param i
	 */
	public void setVersionID(int i) {
		versionID = i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf2xml.TranslatorResource#usesDTD()
	 */
	public boolean usesDTD() {
		return getPublicId() != null && getSystemId() != null;
	}

	/**
	 * Overridden to notify when the ID gets set; need this to push it into the resource
	 * 
	 * @see org.eclipse.emf.ecore.xmi.XMLResource#setID(EObject, String)
	 */
	public void setID(EObject eObject, String id) {
		String oldId = getID(eObject);
		super.setID(eObject, id);
		eObject.eNotify(new ENotificationImpl((InternalEObject) eObject, Notification.SET, ID_FEATURE, oldId, id));
	}

	/**
	 * This method indicates whether or not the extent associated with the resource has been
	 * modified since the last time it was loaded or saved.
	 * 
	 * @return boolean
	 */
	public boolean isModified() {
		return super.isModified() || renderer.isModified();
	}

	/*
	 * Overriden to give the renderer a hook
	 * 
	 * @see org.eclipse.wst.internal.common.emf.resource.ReferencedResource#accessForWrite()
	 */
	public void accessForWrite() {
		renderer.accessForWrite();
		super.accessForWrite();
	}

	/*
	 * Overriden to give the renderer a hook
	 * 
	 * @see org.eclipse.wst.internal.common.emf.resource.ReferencedResource#accessForRead()
	 */
	public void accessForRead() {
		renderer.accessForRead();
		super.accessForRead();
	}

	public void releaseFromRead() {
		renderer.releaseFromRead();
		super.releaseFromRead();
	}

	public void releaseFromWrite() {
		renderer.releaseFromWrite();
		super.releaseFromWrite();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.etools.emf.workbench.ReferencedXMIResourceImpl#preDelete()
	 */
	public void preDelete() {
		super.preDelete();
		renderer.preDelete();
	}

	public boolean isShared() {
		return super.isShared() || renderer.isShared();
	}

	public boolean isSharedForWrite() {
		return super.isSharedForWrite() || renderer.isSharedForWrite();
	}

	public void load(Map options) throws IOException {
		if (renderer.useStreamsForIO()) {
			super.load(options);
		} else if (!isLoaded) {
			load(null, options);
		}
	}
}
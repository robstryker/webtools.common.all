/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 *  $$RCSfile: ProjectResourceSet.java,v $$
 *  $$Revision: 1.4 $$  $$Date: 2007/08/09 00:44:36 $$ 
 */
package org.eclipse.jem.util.emf.workbench;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.emf.ecore.resource.Resource.Factory;

/**
 * A ResourceSet for an entire project. It allows sharing of resources from multiple editors/viewers for a project.
 * <p>
 * An additional Notification type is sent out by ProjectResourceSet's of project resource set about to be released. A release is
 * called when projects are about to be closed. They release all of the resources and unload them. This notification can be used 
 * to know that this is about to happen and to do something before the resources become invalid. It will be sent out just before the
 * resource set will be released. 
 * 
 * @see ProjectResourceSet#SPECIAL_NOTIFICATION_TYPE
 * @see ProjectResourceSet#PROJECTRESOURCESET_ABOUT_TO_RELEASE_ID 
 * @since 1.0.0
 */

public interface ProjectResourceSet extends ResourceSet {

	IProject getProject();
	
	/**
	 * Notification type in notifications from the ProjectResourceSet for
	 * special notifications, and not the standard ones from ResourceSet.
	 * 
	 * @see org.eclipse.emf.common.notify.Notification#getEventType()
	 * @since 1.1.0
	 */
	static int SPECIAL_NOTIFICATION_TYPE = Notification.EVENT_TYPE_COUNT+4;
	
	/**
	 * Notification Feature ID for resource set about to be released.
	 * Use {@link org.eclipse.emf.common.notify.Notification#getFeatureID(java.lang.Class)} to
	 * get this id. The getFeature() on notification will return null.
	 * 
	 * @since 1.1.0
	 */
	static int PROJECTRESOURCESET_ABOUT_TO_RELEASE_ID = 1000;

	/**
	 * Call when the ResourceSet is no longer to be used.
	 * 
	 * 
	 * @since 1.0.0
	 */
	void release();

	/**
	 * Add the <code>resourceHandler</code> to the end of the list of resourceHandlers.
	 * 
	 * @param resourceHandler
	 *            IResourceHandler
	 * @return boolean Return <code>true</code> if it was added.
	 * @since 1.0.0
	 */
	boolean add(ResourceHandler resourceHandler);

	/**
	 * Add the <code>resourceHandler</code> to the front of the list of resourceHandlers.
	 * 
	 * @param resourceHandler
	 *            IResourceHandler
	 * @since 1.0.0
	 */
	void addFirst(ResourceHandler resourceHandler);

	/**
	 * Remove the <code>resourceHandler</code> from the list of resourceHandlers.
	 * 
	 * @param resourceHandler
	 *            IResourceHandler
	 * @return boolean Return true if it was removed.
	 * @since 1.0.0
	 */
	boolean remove(ResourceHandler resourceHandler);

	/**
	 * Return the ResourceSet synchronizer that will synchronize the ResourceSet with changes from the Workbench.
	 * 
	 * @return ResourceSetWorkbenchSynchronizer
	 * @since 1.0.0
	 */
	ResourceSetWorkbenchSynchronizer getSynchronizer();

	/**
	 * Set the ResourceSet synchronizer that will synchronize the ResourceSet with changes from the Workbench.
	 * 
	 * @param aSynchronizer
	 * @return ResourceSetWorkbenchSynchronizer
	 * @since 1.0.0
	 */
	void setSynchronizer(ResourceSetWorkbenchSynchronizer aSynchronizer);

	/**
	 * This should be called by clients whenever the structure of the project changes such that any cached URIs will be invalid. For example, if the
	 * source folders within the URIConverter change.
	 * 
	 * @since 1.0.0
	 */
	void resetNormalizedURICache();

	
	/**
	   * Returns the resource resolved by the URI.
	   * <p>
	   * A resource set is expected to implement the following strategy 
	   * in order to resolve the given URI to a resource.
	   * First it uses it's {@link #getURIConverter URI converter} to {@link URIConverter#normalize normalize} the URI 
	   * and then to compare it with the normalized URI of each resource;
	   * if it finds a match, 
	   * that resource becomes the result.
	   * Failing that,
	   * it {@link org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#delegatedGetResource delegates} 
	   * to allow the URI to be resolved elsewhere.
	   * For example, 
	   * the {@link org.eclipse.emf.ecore.EPackage.Registry#INSTANCE package registry}
	   * is used to {@link org.eclipse.emf.ecore.EPackage.Registry#getEPackage resolve} 
	   * the {@link org.eclipse.emf.ecore.EPackage namespace URI} of a package
	   * to the static instance of that package.
	   * So the important point is that an arbitrary implementation may resolve the URI to any resource,
	   * not necessarily to one contained by this particular resource set.
	   * If the delegation step fails to provide a result,
	   * and if <code>loadOnDemand</code> is <code>true</code>,
	   * a resource is {@link org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#demandCreateResource created} 
	   * and that resource becomes the result.
	   * If <code>loadOnDemand</code> is <code>true</code>
	   * and the result resource is not {@link Resource#isLoaded loaded}, 
	   * it will be {@link org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#demandLoad loaded} before it is returned.
	   * </p>
	   * @param uri the URI to resolve.
	   * @param loadOnDemand whether to create and load the resource, if it doesn't already exists.
	   * @param registeredFactory that is used to create resource if needed 
	   * @return the resource resolved by the URI, or <code>null</code> if there isn't one and it's not being demand loaded.
	   * @throws RuntimeException if a resource can't be demand created.
	   * @throws org.eclipse.emf.common.util.WrappedException if a problem occurs during demand load.
	   * @since 2.1
	   */
	
	Resource getResource(URI uri, boolean loadOnDemand, Factory registeredFactory);
	
	public Resource createResource(URI uri, Resource.Factory resourceFactory);
}
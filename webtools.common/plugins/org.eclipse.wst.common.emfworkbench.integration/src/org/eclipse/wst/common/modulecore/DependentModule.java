/**
 * <copyright>
 * </copyright>
 *
 * $Id: DependentModule.java,v 1.3 2005/02/09 02:48:39 cbridgha Exp $
 */
package org.eclipse.wst.common.modulecore;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Dependent Module</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.modulecore.DependentModule#getHandle <em>Handle</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.DependentModule#getDeployedPath <em>Deployed Path</em>}</li>
 *   <li>{@link org.eclipse.wst.common.modulecore.DependentModule#getDependencyType <em>Dependency Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getDependentModule()
 * @model 
 * @generated
 */
public interface DependentModule extends EObject{
	/**
	 * Returns the value of the '<em><b>Handle</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Handle</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Handle</em>' attribute.
	 * @see #setHandle(URI)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getDependentModule_Handle()
	 * @model dataType="org.eclipse.wst.common.modulecore.URI"
	 * @generated
	 */
	URI getHandle();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.DependentModule#getHandle <em>Handle</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Handle</em>' attribute.
	 * @see #getHandle()
	 * @generated
	 */
	void setHandle(URI value);

	/**
	 * Returns the value of the '<em><b>Deployed Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Deployed Path</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Deployed Path</em>' attribute.
	 * @see #setDeployedPath(URI)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getDependentModule_DeployedPath()
	 * @model dataType="org.eclipse.wst.common.modulecore.URI"
	 * @generated
	 */
	URI getDeployedPath();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.DependentModule#getDeployedPath <em>Deployed Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Deployed Path</em>' attribute.
	 * @see #getDeployedPath()
	 * @generated
	 */
	void setDeployedPath(URI value);

	/**
	 * Returns the value of the '<em><b>Dependency Type</b></em>' attribute.
	 * The literals are from the enumeration {@link org.eclipse.wst.common.modulecore.DependencyType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dependency Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dependency Type</em>' attribute.
	 * @see org.eclipse.wst.common.modulecore.DependencyType
	 * @see #setDependencyType(DependencyType)
	 * @see org.eclipse.wst.common.modulecore.ModuleCorePackage#getDependentModule_DependencyType()
	 * @model 
	 * @generated
	 */
	DependencyType getDependencyType();

	/**
	 * Sets the value of the '{@link org.eclipse.wst.common.modulecore.DependentModule#getDependencyType <em>Dependency Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dependency Type</em>' attribute.
	 * @see org.eclipse.wst.common.modulecore.DependencyType
	 * @see #getDependencyType()
	 * @generated
	 */
	void setDependencyType(DependencyType value);

} // DependentModule
/**
 * <copyright>
 * </copyright>
 *
 * $Id: ProjectComponentsImpl.java,v 1.3 2006/04/27 04:17:40 cbridgha Exp $
 */
package org.eclipse.wst.common.componentcore.internal.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.wst.common.componentcore.internal.ComponentcorePackage;
import org.eclipse.wst.common.componentcore.internal.ProjectComponents;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Project Modules</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.ProjectComponentsImpl#getProjectName <em>Project Name</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.ProjectComponentsImpl#getComponents <em>Components</em>}</li>
 *   <li>{@link org.eclipse.wst.common.componentcore.internal.impl.ProjectComponentsImpl#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ProjectComponentsImpl extends EObjectImpl implements ProjectComponents {
	/**
	 * The default value of the '{@link #getProjectName() <em>Project Name</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getProjectName()
	 * @generated
	 * @ordered
	 */
	protected static final String PROJECT_NAME_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getProjectName() <em>Project Name</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getProjectName()
	 * @generated
	 * @ordered
	 */
	protected String projectName = PROJECT_NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getComponents() <em>Components</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComponents()
	 * @generated
	 * @ordered
	 */
	protected EList components = null;

	/**
	 * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected static final String VERSION_EDEFAULT = "1.0.0";

	/**
	 * The cached value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected String version = VERSION_EDEFAULT;

	private boolean isIndexed;

	private final Map modulesIndex = new HashMap();

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected ProjectComponentsImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ComponentcorePackage.Literals.PROJECT_COMPONENTS;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public void setProjectName(String newProjectName) {
		String oldProjectName = projectName;
		projectName = newProjectName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentcorePackage.PROJECT_COMPONENTS__PROJECT_NAME, oldProjectName, projectName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getComponents() {
		if (components == null) {
			components = new EObjectContainmentEList(WorkbenchComponent.class, this, ComponentcorePackage.PROJECT_COMPONENTS__COMPONENTS);
		}
		return components;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setVersion(String newVersion) {
		String oldVersion = version;
		version = newVersion;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ComponentcorePackage.PROJECT_COMPONENTS__VERSION, oldVersion, version));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ComponentcorePackage.PROJECT_COMPONENTS__COMPONENTS:
				return ((InternalEList)getComponents()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ComponentcorePackage.PROJECT_COMPONENTS__PROJECT_NAME:
				return getProjectName();
			case ComponentcorePackage.PROJECT_COMPONENTS__COMPONENTS:
				return getComponents();
			case ComponentcorePackage.PROJECT_COMPONENTS__VERSION:
				return getVersion();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ComponentcorePackage.PROJECT_COMPONENTS__PROJECT_NAME:
				setProjectName((String)newValue);
				return;
			case ComponentcorePackage.PROJECT_COMPONENTS__COMPONENTS:
				getComponents().clear();
				getComponents().addAll((Collection)newValue);
				return;
			case ComponentcorePackage.PROJECT_COMPONENTS__VERSION:
				setVersion((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eUnset(int featureID) {
		switch (featureID) {
			case ComponentcorePackage.PROJECT_COMPONENTS__PROJECT_NAME:
				setProjectName(PROJECT_NAME_EDEFAULT);
				return;
			case ComponentcorePackage.PROJECT_COMPONENTS__COMPONENTS:
				getComponents().clear();
				return;
			case ComponentcorePackage.PROJECT_COMPONENTS__VERSION:
				setVersion(VERSION_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ComponentcorePackage.PROJECT_COMPONENTS__PROJECT_NAME:
				return PROJECT_NAME_EDEFAULT == null ? projectName != null : !PROJECT_NAME_EDEFAULT.equals(projectName);
			case ComponentcorePackage.PROJECT_COMPONENTS__COMPONENTS:
				return components != null && !components.isEmpty();
			case ComponentcorePackage.PROJECT_COMPONENTS__VERSION:
				return VERSION_EDEFAULT == null ? version != null : !VERSION_EDEFAULT.equals(version);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (projectName: ");
		result.append(projectName);
		result.append(", version: ");
		result.append(version);
		result.append(')');
		return result.toString();
	}

	public WorkbenchComponent findWorkbenchModule(String aDeployName) {
		if (getComponents().size() == 1)
			return ((WorkbenchComponent)getComponents().get(0)).getName().equals(aDeployName) ? (WorkbenchComponent)getComponents().get(0) : null;
		if (!isIndexed()) 
			indexModules(); 
		return (WorkbenchComponent) getModulesIndex().get(aDeployName);
	}

	/**
	 * @return
	 */
	private boolean isIndexed() {
		return isIndexed;
	}

	/**
	 * 
	 */
	private void indexModules() {
		if (isIndexed)
			return;

		synchronized (modulesIndex) {
			Adapter adapter = EcoreUtil.getAdapter(eAdapters(), ModuleIndexingAdapter.class);
			if (adapter == null) 
				eAdapters().add((adapter = new ModuleIndexingAdapter()));
			
			WorkbenchComponent module = null;
			for(Iterator iter = getComponents().iterator(); iter.hasNext(); ) {
				module = (WorkbenchComponent) iter.next();
				modulesIndex.put(module.getName(), module);
			}
		}
		isIndexed = true;
	}

	/**
	 * @return
	 */
	/* package */ Map getModulesIndex() {
		return modulesIndex;
	}

} // ProjectComponentsImpl

package org.eclipse.wst.common.componentcore.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.common.componentcore.ui.messages"; //$NON-NLS-1$
	public static String ModuleAssemblyRootPageDescription;
	public static String ModuleAssembly;
	public static String ErrorCheckingFacets;
	public static String ErrorNotVirtualComponent;
	public static String DeployPathColumn;
	public static String SourceColumn;
	public static String InternalLibJarWarning;
	public static String AddFolder;
	public static String AddFolderElipses;
	public static String AddReference;
	public static String RemoveSelected;
	public static String JarTitle;
	public static String JarDescription;
	public static String ExternalJarTitle;
	public static String ExternalJarDescription;
	public static String Browse;
	public static String NewReferenceTitle;
	public static String NewReferenceDescription;
	public static String NewReferenceWizard;
	public static String ProjectReferenceTitle;
	public static String ProjectReferenceDescription;
	public static String VariableReferenceTitle;
	public static String VariableReferenceDescription;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}

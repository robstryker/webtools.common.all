/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal.ui;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.event.HyperlinkEvent;

import org.eclipse.core.resources.IProject;
import org.eclipse.jem.util.logger.LogEntry;
import org.eclipse.jem.util.logger.proxy.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.wst.common.frameworks.internal.ui.WTPUIPlugin;
import org.eclipse.wst.validation.internal.ConfigurationManager;
import org.eclipse.wst.validation.internal.GlobalConfiguration;
import org.eclipse.wst.validation.internal.ProjectConfiguration;
import org.eclipse.wst.validation.internal.ValidatorMetaData;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.eclipse.wst.validation.internal.ui.plugin.ValidationUIPlugin;

/**
 * This class and its inner classes are not intended to be subclassed outside of the validation
 * framework.
 * 
 * This page implements the PropertyPage for validators; viewed when the user right-clicks on the
 * IProject, selects "Properties", and then "Validation."
 * 
 * There exist three possible page layouts: if there is an eclipse internal error, and the page is
 * brought up on a non-IProject type; if there are no validators configured on that type of
 * IProject, and a page which lists all validators configured on that type of IProject. These three
 * pages are implemented as inner classes, so that it's clear which method is needed for which
 * input. When all of the methods, and behaviour, were implemented in this one class, much more
 * error-checking had to be done, to ensure that the method wasn't being called incorrectly by one
 * of the pages.
 */
public class ValidationPropertiesPage extends PropertyPage {
	static final String NEWLINE = System.getProperty("line.separator"); //$NON-NLS-1$
	static final String TAB = "\t"; //$NON-NLS-1$
	static final String NEWLINE_AND_TAB = NEWLINE + TAB;
	private IValidationPage _pageImpl = null;

	/**
	 * Initially, this interface was created as an abstract class, and getControl() was implemented.
	 * (getProject() could also have been implemented in the abstract class.) However, at runtime, a
	 * NullPointerException was thrown; the inner class had lost its pointer to its enclosing class.
	 * After some experimentation, I discovered that if I changed the parent to an interface, the
	 * enclosing class could be found. (Merely moving the AValidationPage into its own file was
	 * insufficient.)
	 */
	public interface IValidationPage {
		public abstract Composite createPage(Composite parent) throws InvocationTargetException;

		public abstract boolean performOk() throws InvocationTargetException;

		public boolean performDefaults() throws InvocationTargetException;

		public Composite getControl();

		public abstract void dispose();
	}

	public class InvalidPage implements IValidationPage {
		private Composite page = null;

		private Composite composite = null;
		private GridLayout layout = null;
		private Label messageLabel = null;

		public InvalidPage(Composite parent) {
			page = createPage(parent);
		}

		/**
		 * This page is added to the Properties guide if some internal problem occurred; for
		 * example, the highlighted item in the workbench is not an IProject (according to this
		 * page's plugin.xml, this page is only valid when an IProject is selected).
		 */
		public Composite createPage(Composite parent) {
			// Don't create the default and apply buttons.
			noDefaultAndApplyButton();

			final ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
			sc1.setLayoutData(new GridData(GridData.FILL_BOTH));
			composite = new Composite(sc1, SWT.NONE);
			sc1.setContent(composite);
			layout = new GridLayout();
			composite.setLayout(layout);
			PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, ContextIds.VALIDATION_PROPERTIES_PAGE);

			messageLabel = new Label(composite, SWT.NONE);
			messageLabel.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INVALID_REGISTER));

			composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

			return composite;
		}

		public boolean performDefaults() {
			return true;
		}

		/**
		 * Since this page occurs under invalid circumstances, there is nothing to save.
		 */
		public boolean performOk() {
			return true;
		}

		public Composite getControl() {
			return page;
		}

		public void dispose() {
			messageLabel.dispose();
			//			layout.dispose();
			composite.dispose();
		}
	}

	public class NoValidatorsPage implements IValidationPage {
		private Composite page = null;

		private Composite composite = null;
		private GridLayout layout = null;
		private GridData data = null;
		private Label messageLabel = null;
		
		public NoValidatorsPage(Composite parent) {
			page = createPage(parent);
		}

		/**
		 * This page is created if an IProject is selected, but that project has no validators
		 * configured (i.e., the page is valid, but an empty list.)
		 */
		public Composite createPage(Composite parent) {
			// Don't create the default and apply buttons.
			noDefaultAndApplyButton();

			// top level group
			final ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
			sc1.setLayoutData(new GridData(GridData.FILL_BOTH));
			composite = new Composite(sc1, SWT.NONE);
			sc1.setContent(composite);
			layout = new GridLayout();
			composite.setLayout(layout);
			data = new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL);
			composite.setLayoutData(data);
			PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, ContextIds.VALIDATION_PROPERTIES_PAGE);

			messageLabel = new Label(composite, SWT.NONE);
			String[] msgParm = {getProject().getName()};
			messageLabel.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_LBL_NOVALIDATORS_DESC, msgParm));
			composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			return composite;
		}

		public boolean performDefaults() {
			return true;
		}

		/**
		 * Since there are no validators, there is nothing to save.
		 */
		public boolean performOk() {
			return true;
		}

		public Composite getControl() {
			return page;
		}

		public void dispose() {
			messageLabel.dispose();
			//			layout.dispose();
			//			data.dispose();
			composite.dispose();
		}
	}

	public class ValidatorListPage implements IValidationPage {
		Composite page = null;
		GridLayout layout = null;
		GridData data = null;
		Label messageLabel = null;
		TableViewer validatorList = null;
		Button overrideGlobalButton = null;
		Button disableAllValidation = null;
		private Button enableAllButton = null;
		private Button disableAllButton = null;
		Label emptyRowPlaceholder = null;
		private String[] columnProperties;
		private CellEditor[] columnEditors;
		private String[] COMBO_VALUES = new String[] {"Enabled","Disabled"};
		private static final String ENABLED = "Enabled";
		private static final String DISABLED = "Disabled";
		private static final int ENABLED_INT = 0;
		private static final int DISABLED_INT = 1;
		private Table validatorsTable;
		private static final String VALIDATORS = "validators"; //$NON-NLS-1$
		private static final String MANUAL_CHECK = "manualCheck";//$NON-NLS-2$
		private static final String BUILD_CHECK = "buildCheck";//$NON-NLS-2$
		private static final int VALUE_NOT_FOUND = -1;
		private static final int MANUAL_COL = 1;
		private static final int BUILD_COL = 2;
		private Label globalPrefLink = null;

		ProjectConfiguration pagePreferences = null;

		// default values for the widgets, initialized in the constructor
		//private boolean isAutoBuildEnabled = false;
		//private boolean isBuilderConfigured = false;
		private boolean canOverride = false;

		private ValidatorMetaData[] oldVmd = null; // Cache the enabled validators so that, if there

		// is no change to this list, the expensive task
		// list update can be avoided

		/**
		 * This class is provided for the CheckboxTableViewer in the
		 * ValidationPropertiesPage$ValidatorListPage class.
		 */
		public class ValidationContentProvider implements IStructuredContentProvider {
			/**
			 * Disposes of this content provider. This is called by the viewer when it is disposed.
			 */
			public void dispose() {
				//dispose
			}

			/**
			 * Returns the elements to display in the viewer when its input is set to the given
			 * element. These elements can be presented as rows in a table, items in a list, etc.
			 * The result is not modified by the viewer.
			 * 
			 * @param inputElement
			 *            the input element
			 * @return the array of elements to display in the viewer
			 */
			public java.lang.Object[] getElements(Object inputElement) {
				if (inputElement instanceof ValidatorMetaData[]) {
					// The ValidatorMetaData[] is the array which is returned by ValidatorManager's
					// getConfiguredValidatorMetaData(IProject) call.
					// This array is set to be the input of the CheckboxTableViewer in
					// ValidationPropertiesPage$ValidatorListPage's createPage(Composite)
					// method.
					return (ValidatorMetaData[]) inputElement;
				}
				return new Object[0];
			}

			/**
			 * Notifies this content provider that the given viewer's input has been switched to a
			 * different element.
			 * <p>
			 * A typical use for this method is registering the content provider as a listener to
			 * changes on the new input (using model-specific means), and deregistering the viewer
			 * from the old input. In response to these change notifications, the content provider
			 * propagates the changes to the viewer.
			 * </p>
			 * 
			 * @param viewer
			 *            the viewer
			 * @param oldInput
			 *            the old input element, or <code>null</code> if the viewer did not
			 *            previously have an input
			 * @param newInput
			 *            the new input element, or <code>null</code> if the viewer does not have
			 *            an input
			 */
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				//do nothing
			}



		}

		/**
		 * This class is provided for ValidationPropertiesPage$ValidatorListPage's
		 * checkboxTableViewer element.
		 */
		public class ValidationLabelProvider extends LabelProvider implements ITableLabelProvider {
			/**
			 * Override the LabelProvider's text, by customizing the text for a ValidatorMetaData
			 * element.
			 */
			public String getText(Object element) {
				if (element == null) {
					return ""; //$NON-NLS-1$
				} else if (element instanceof ValidatorMetaData) {
					return ((ValidatorMetaData) element).getValidatorDisplayName();
				} else {
					return super.getText(element);
				}
			}
			
			public String getColumnText(Object element, int columnIndex) {
				if(columnIndex == 0) {
					return ((ValidatorMetaData) element).getValidatorDisplayName();
				}
				if(columnIndex == 1) {
					if(((ValidatorMetaData)element).isManualValidation())
						return ENABLED;
					return DISABLED;	
				} else if(columnIndex == 2) {
					if(((ValidatorMetaData)element).isBuildValidation())
						return ENABLED;
					return DISABLED;
				}
				return null;
			}

			public Image getColumnImage(Object element, int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}
		}

		/**
		 * This class is used to sort the CheckboxTableViewer elements.
		 */
		public class ValidationViewerSorter extends ViewerSorter {
			/**
			 * Returns a negative, zero, or positive number depending on whether the first element
			 * is less than, equal to, or greater than the second element.
			 * <p>
			 * The default implementation of this method is based on comparing the elements'
			 * categories as computed by the <code>category</code> framework method. Elements
			 * within the same category are further subjected to a case insensitive compare of their
			 * label strings, either as computed by the content viewer's label provider, or their
			 * <code>toString</code> values in other cases. Subclasses may override.
			 * </p>
			 * 
			 * @param viewer
			 *            the viewer
			 * @param e1
			 *            the first element
			 * @param e2
			 *            the second element
			 * @return a negative number if the first element is less than the second element; the
			 *         value <code>0</code> if the first element is equal to the second element;
			 *         and a positive number if the first element is greater than the second element
			 */
			public int compare(Viewer viewer, Object e1, Object e2) {
				// Can't instantiate ViewerSorter because it's abstract, so use this
				// inner class to represent it.
				return super.compare(viewer, e1, e2);
			}
		}

		public ValidatorListPage(Composite parent) throws InvocationTargetException {
			ConfigurationManager prefMgr = ConfigurationManager.getManager();
			ValidatorManager vMgr = ValidatorManager.getManager();

			pagePreferences = new ProjectConfiguration(prefMgr.getProjectConfiguration(getProject())); // This
			// represents the values on the page that haven't been persisted yet.
			// Start with the last values that were persisted into the current
			// page's starting values.

			// store the default values for the widgets
			canOverride = prefMgr.getGlobalConfiguration().canProjectsOverride();
			//isAutoBuildEnabled = vMgr.isGlobalAutoBuildEnabled();
			//isBuilderConfigured = ValidatorManager.doesProjectSupportBuildValidation(getProject());
			oldVmd = pagePreferences.getEnabledValidators(); // Cache the enabled validators so
			// that, if there is no change to this
			// list, the expensive task list update
			// can be avoided

			createPage(parent);
		}
		
		private void setupTableColumns(Table table, TableViewer viewer) {
			TableColumn validatorColumn = new TableColumn(table, SWT.NONE);
	        validatorColumn.setText("Validator");
	        validatorColumn.setResizable(false);
	        validatorColumn.setWidth(240);
	        TableColumn manualColumn = new TableColumn(table, SWT.NONE);
	        manualColumn.setText("Manual");
	        manualColumn.setResizable(false);
	        manualColumn.setWidth(80);
	        TableColumn buildColumn = new TableColumn(table, SWT.NONE);
	        buildColumn.setText("Build");
	        buildColumn.setResizable(false);
	        buildColumn.setWidth(80);
	        setupCellModifiers(table, viewer);
	    }
		private void setupCellModifiers(Table table, TableViewer viewer) {
	        columnProperties = new String[3];
	        columnProperties[0] = VALIDATORS; //$NON-NLS-1$
	        columnProperties[1] = MANUAL_CHECK;//$NON-NLS-2$
	        columnProperties[2] = BUILD_CHECK;//$NON-NLS-2$
	        viewer.setColumnProperties(columnProperties);
	        columnEditors = new CellEditor[table.getColumnCount()];
	        columnEditors[1] = new ComboBoxCellEditor(table,COMBO_VALUES, SWT.READ_ONLY);
	        columnEditors[2] = new ComboBoxCellEditor(table,COMBO_VALUES, SWT.READ_ONLY);
	        viewer.setCellEditors(columnEditors);
	    }

		/**
		 * This page is created if the current project has at least one validator configured on it.
		 */
		public Composite createPage(Composite parent) throws InvocationTargetException {
			// top level group
			final ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
			sc1.setLayoutData(new GridData(GridData.FILL_BOTH));
			page = new Composite(sc1, SWT.NONE);
			sc1.setContent(page);
			page.setLayout(new GridLayout()); // use the layout's default preferences
			
			Composite validatorGroup = new Composite(page, SWT.NONE);
			GridLayout validatorGroupLayout = new GridLayout();
			validatorGroupLayout.numColumns = 2;
			validatorGroup.setLayout(validatorGroupLayout);
			
//			FormToolkit toolKit =  new FormToolkit(validatorGroup.getDisplay());
//			Hyperlink link = toolKit.createHyperlink(validatorGroup, "Gloabl Preferences...", SWT.RIGHT);
			
			
			
			GridData overrideData = new GridData(GridData.FILL_HORIZONTAL);
			overrideGlobalButton = new Button(validatorGroup, SWT.CHECK);
			overrideGlobalButton.setLayoutData(overrideData);
			overrideGlobalButton.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.PROP_BUTTON_OVERRIDE, new String[]{getProject().getName()}));
			overrideGlobalButton.setFocus(); // must focus on something for F1 to have a topic to
			// launch
			overrideGlobalButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					pagePreferences.setDoesProjectOverride(overrideGlobalButton.getSelection());
					try {
						updateWidgets();
					} catch (InvocationTargetException exc) {
						displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
					}
				}
			});

			Hyperlink link = new Hyperlink(validatorGroup,SWT.None);
			GridData layout = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END);
			link.setLayoutData(layout);
			link.setUnderlined(true);
			Color color = new Color(validatorGroup.getDisplay(),new RGB(0,0,255) );
			link.setForeground(color);
			link.setText("Gloabl Preferences...");
			link.addHyperlinkListener(new IHyperlinkListener() {
				
				public static final String DATA_NO_LINK= "PropertyAndPreferencePage.nolink"; //$NON-NLS-1$
				
					public void hyperlinkUpdate(HyperlinkEvent e) {
						
						
					}
					
					public void linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void linkExited(org.eclipse.ui.forms.events.HyperlinkEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {
						String id= getPreferencePageID();
						PreferencesUtil.createPreferenceDialogOn(getShell(), id, new String[] { id }, DATA_NO_LINK).open();
					}
					
					private String getPreferencePageID() {
						return "ValidationPreferencePage";
					}
					

				});
			
			emptyRowPlaceholder = new Label(validatorGroup, SWT.NONE);
			emptyRowPlaceholder.setLayoutData(new GridData());
			
			GridData disableValidationData = new GridData(GridData.FILL_HORIZONTAL);
			disableValidationData.horizontalSpan = 2;
			disableAllValidation = new Button(validatorGroup, SWT.CHECK);
			disableAllValidation.setLayoutData(disableValidationData);
			disableAllValidation.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.DISABLE_VALIDATION));
			disableAllValidation.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					pagePreferences.setDisableAllValidation(disableAllValidation.getSelection());
					disableAllValidation.setFocus();
					validatorsTable.setEnabled(!disableAllValidation.getSelection());
					enableAllButton.setEnabled(!disableAllValidation.getSelection());
					disableAllButton.setEnabled(!disableAllValidation.getSelection());
				}
			});
			
			emptyRowPlaceholder = new Label(validatorGroup, SWT.NONE);
			emptyRowPlaceholder.setLayoutData(new GridData());



			GridData listLabelData = new GridData(GridData.FILL_HORIZONTAL);
			listLabelData.horizontalSpan = 2;
			messageLabel = new Label(validatorGroup, SWT.NONE);
			messageLabel.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_LBL_DESC, new String[]{getProject().getName()}));
			messageLabel.setLayoutData(listLabelData);

			validatorsTable = new Table(validatorGroup,SWT.BORDER);
			TableLayout tableLayout = new TableLayout();
			tableLayout.addColumnData(new ColumnWeightData(160, true));
	        tableLayout.addColumnData(new ColumnWeightData(80, true));
	        tableLayout.addColumnData(new ColumnWeightData(80, true));
			validatorsTable.setHeaderVisible(true);
			validatorsTable.setLinesVisible(true);
	        validatorsTable.setLayout(tableLayout);
			
			validatorList = new TableViewer(validatorsTable);
	        GridData validatorListData = new GridData(GridData.FILL_HORIZONTAL);
			validatorListData.horizontalSpan = 2;
			validatorsTable.setLayoutData(validatorListData);
			validatorList.getTable().setLayoutData(validatorListData);
			validatorList.setLabelProvider(new ValidationLabelProvider());
			validatorList.setContentProvider(new ValidationContentProvider());
			validatorList.setSorter(new ValidationViewerSorter());
	        setupTableColumns(validatorsTable,validatorList);
	        
			validatorList.setCellModifier(new ICellModifier() {

				public boolean canModify(Object element, String property) {
					ComboBoxCellEditor cellEditor = getComboBoxCellEditor(property);
					if (cellEditor == null)
						return false;
					return true;
				}

				protected ComboBoxCellEditor getComboBoxCellEditor(String property) {
					CellEditor cellEditor = getCellEditor(property);
					if (cellEditor instanceof ComboBoxCellEditor)
						return (ComboBoxCellEditor) cellEditor;
					return null;

				}

				protected int getPropertyIntValue(String property) {
					if (columnProperties != null) {
						for (int i = 0; i < columnProperties.length; i++) {
							if (columnProperties[i].equals(property))
								return i;
						}
					}
					return VALUE_NOT_FOUND;
				}

				protected CellEditor getCellEditor(String property) {
					int comboCellEditorIndex = getPropertyIntValue(property);
					if (comboCellEditorIndex == VALUE_NOT_FOUND)
						return null;
					return columnEditors[comboCellEditorIndex];
				}

				public Object getValue(Object element, String property) {
					ValidatorMetaData data = (ValidatorMetaData) element;
					if (property == MANUAL_CHECK) {
						if (data.isManualValidation())
							return new Integer(ENABLED_INT);
						else
							return new Integer(DISABLED_INT);

					} else if (property == BUILD_CHECK) {
						if (data.isBuildValidation())
							return new Integer(ENABLED_INT);
						else
							return new Integer(DISABLED_INT);
					}
					return new Integer(VALUE_NOT_FOUND);
				}

				public void modify(Object element, String property, Object value) {
					ValidatorMetaData data = (ValidatorMetaData) ((TableItem) element).getData();
					int intValue = ((Integer) value).intValue();
					if (property.equals(MANUAL_CHECK)) {
						if (intValue == ENABLED_INT) {
							data.setManualValidation(true);
						} else if (intValue == DISABLED_INT) {
							data.setManualValidation(false);
						}
					} else if (property.equals(BUILD_CHECK)) {
						if (intValue == ENABLED_INT) {
							data.setBuildValidation(true);
						} else if (intValue == DISABLED_INT) {
							data.setBuildValidation(false);
						}
					}
					validatorList.refresh();
				}
			});
			
			validatorList.setInput(pagePreferences.getValidators());
			validatorsTable.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					pagePreferences.setEnabledValidators(getEnabledValidators());
					try {
						updateWidgets();
					} catch (InvocationTargetException exc) {
						displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
					}
				}
				
				public void widgetDefaultSelected(SelectionEvent e) {
					try {
						performDefaults();
					} catch (InvocationTargetException exc) {
						displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
					}
				}
			});


			enableAllButton = new Button(validatorGroup, SWT.PUSH);
			GridData selectData = new GridData();
			enableAllButton.setLayoutData(selectData);
			enableAllButton.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.PREF_BUTTON_ENABLEALL));
			enableAllButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						performSelectAll();
					} catch (InvocationTargetException exc) {
						displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
					}
				}
			});
			PlatformUI.getWorkbench().getHelpSystem().setHelp(enableAllButton, ContextIds.VALIDATION_PROPERTIES_PAGE);


			GridData deselectData = new GridData();
			disableAllButton = new Button(validatorGroup, SWT.PUSH);
			disableAllButton.setLayoutData(deselectData);
			disableAllButton.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.PREF_BUTTON_DISABLEALL));
			disableAllButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						performDeselectAll();
					} catch (InvocationTargetException exc) {
						displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
					}
				}
			});
			PlatformUI.getWorkbench().getHelpSystem().setHelp(disableAllButton, ContextIds.VALIDATION_PROPERTIES_PAGE);

			Composite maxGroup = new Composite(page, SWT.NONE);
			GridLayout maxGroupLayout = new GridLayout();
			maxGroupLayout.numColumns = 2;
			maxGroup.setLayout(maxGroupLayout);
			GridData maxGroupData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			maxGroup.setLayoutData(maxGroupData);

			// Have to set the tab order or only the first checkbox in a Composite can
			// be tab-ed to. (Seems to apply only to checkboxes. Have to use the arrow
			// key to navigate the checkboxes.)
			validatorGroup.setTabList(new Control[]{overrideGlobalButton, validatorList.getTable(), enableAllButton, disableAllButton});

			updateWidgets();

			page.setSize(page.computeSize(SWT.DEFAULT, SWT.DEFAULT));

			return page;
		}

		protected void updateTable() throws InvocationTargetException {
			TableItem[] items = validatorsTable.getItems();
			for (int i = 0; i < items.length; i++) {
				TableItem item = items[i];
				ValidatorMetaData vmd = (ValidatorMetaData) item.getData();

				// Should the validator be enabled? Read the user's preferences from last time,
				// if they exist, and set from that. If they don't exist, use the Validator class'
				// default value.
				if(pagePreferences.isManualEnabled(vmd))
					vmd.setManualValidation(true);
				else
					vmd.setManualValidation(false);
				if(pagePreferences.isBuildEnable(vmd))
					vmd.setBuildValidation(true);
				else
					vmd.setBuildValidation(false);
			}
			validatorList.refresh();
		}

		public boolean performDefaults() throws InvocationTargetException {
			pagePreferences.resetToDefault();
			updateWidgets();
			//checkInteger(); // clear the "max must be a positive integer" message if it exists
			getDefaultsButton().setFocus();
			return true;
		}

		public boolean performSelectAll() throws InvocationTargetException {
			setAllValidators(true);
			pagePreferences.setEnabledValidators(getEnabledValidators());
			updateWidgets();
			enableAllButton.setFocus();
			return true;
		}

		public boolean performDeselectAll() throws InvocationTargetException {
			setAllValidators(false);
			pagePreferences.setEnabledValidators(getEnabledValidators());
			updateWidgets();
			disableAllButton.setFocus();
			return true;
		}
		
		public ValidatorMetaData[] getEnabledValidators() {
			List enabledValidators = new ArrayList();
			TableItem[] items = validatorsTable.getItems();
			for (int i = 0; i < items.length; i++) {
				ValidatorMetaData validatorMetaData = (ValidatorMetaData) items[i].getData();
				if(validatorMetaData.isManualValidation() || validatorMetaData.isBuildValidation())
					enabledValidators.add(validatorMetaData);
			}
			return (ValidatorMetaData[])enabledValidators.toArray(new ValidatorMetaData[enabledValidators.size()]);
		}

		
		/**
		 * 
		 */
		private void setAllValidators(boolean bool) {
			TableItem[] items = validatorsTable.getItems();
			for (int i = 0; i < items.length; i++) {
				ValidatorMetaData validatorMetaData = (ValidatorMetaData) items[i].getData();
				validatorMetaData.setManualValidation(bool);
				validatorMetaData.setBuildValidation(bool);
			}
		}

		/*protected int checkInteger() throws InvocationTargetException {
			ProjectConfiguration pc = ConfigurationManager.getManager().getProjectConfiguration(getProject());
			String text = maxValProblemsField.getText();
			if (text == null) {
				setErrorMessage(ResourceHandler.getExternalizedMessage(ResourceConstants.PROP_ERROR_INT));
				return pc.getMaximumNumberOfMessages();
			}
			try {
				Integer tempInt = new Integer(text.trim());

				// no exception? It's an int, then.
				if (tempInt.intValue() <= 0) {
					setErrorMessage(ResourceHandler.getExternalizedMessage(ResourceConstants.PROP_ERROR_INT));
					return pc.getMaximumNumberOfMessages();
				}
				setErrorMessage(null);
				return Integer.valueOf(maxValProblemsField.getText().trim()).intValue();
			} catch (NumberFormatException exc) {
				setErrorMessage(ResourceHandler.getExternalizedMessage(ResourceConstants.PROP_ERROR_INT));
				return pc.getMaximumNumberOfMessages();
			}

		}*/

		void updateWidgets() throws InvocationTargetException {
			// Since the setting of the "override" button enables/disables the other widgets on the
			// page,
			// update the enabled state of the other widgets from the "override" button.
			boolean overridePreferences = canOverride && pagePreferences.doesProjectOverride();

			overrideGlobalButton.setEnabled(canOverride);
			overrideGlobalButton.setSelection(overridePreferences);
			disableAllValidation.setEnabled(overridePreferences);
			validatorList.getTable().setEnabled(overridePreferences);
			validatorsTable.setEnabled(overridePreferences);
			validatorsTable.setEnabled(overridePreferences);
			enableAllButton.setEnabled(overridePreferences); // since help messsage isn't
			disableAllButton.setEnabled(overridePreferences);

			updateTable();

			// Never check if builder is configured because if it isn't, the user needs to be able
			// to add the builder via the instructions on the F1 infopops.
			// In the case when the builder isn't configured, show the checkbox as enabled but
			// cleared
			// The only time that these two checkboxes are disabled is when no validators are
			// enabled in the list.
			boolean valEnabled = (pagePreferences.numberOfEnabledValidators() > 0);
			/*valWhenBuildButton.setEnabled(overridePreferences && valEnabled);
			valWhenBuildButton.setSelection(pagePreferences.isBuildValidate() && valEnabled && isBuilderConfigured);
*/
			boolean incValEnabled = (pagePreferences.numberOfEnabledIncrementalValidators() > 0);
			//autoButton.setEnabled(overridePreferences && isAutoBuildEnabled && incValEnabled);
			//autoButton.setSelection(pagePreferences.isAutoValidate() && incValEnabled && isAutoBuildEnabled && isBuilderConfigured);

			updateHelp();
		}

		protected void updateHelp() throws InvocationTargetException {
			// Whenever a widget is disabled, it cannot get focus.
			// Since it can't have focus, its context-sensitive F1 help can't come up.
			// From experimentation, I know that the composite parent of the widget
			// can't have focus either. So, fudge the focus by making the table the widget
			// surrogate so that the F1 help can be shown, with its instructions on how to
			// enable the disabled widget. The table never has F1 help associated with it other
			// than the page F1, so this fudge doesn't remove any context-sensitive help
			// from the table widget.

			/*if (autoButton.getEnabled()) {
				// set the table's help back to what it was
				PlatformUI.getWorkbench().getHelpSystem().setHelp(validatorList.getTable(), ContextIds.VALIDATION_PROPERTIES_PAGE);
				PlatformUI.getWorkbench().getHelpSystem().setHelp(autoButton, ContextIds.VALIDATION_PROPERTIES_PAGE_AUTO_ENABLED);
			} else {
				// The order of the following if statement is important!
				// If the user cannot enable automatic validation on the project, then the user
				// should not be told, for example, to turn auto-build on. Let the user know that
				// no matter what they do they cannot run auto-validate on the project. IF the
				// project
				// supports auto-validate, THEN check for the items which the user can change.
				validatorList.getTable().setFocus();
				if (pagePreferences.numberOfIncrementalValidators() == 0) {
					PlatformUI.getWorkbench().getHelpSystem().setHelp(validatorList.getTable(), ContextIds.VALIDATION_PROPERTIES_PAGE_DISABLED_AUTO_NOINCVALCONFIG);
				} else if (!ValidatorManager.getManager().isGlobalAutoBuildEnabled()) {
					PlatformUI.getWorkbench().getHelpSystem().setHelp(validatorList.getTable(), ContextIds.VALIDATION_PROPERTIES_PAGE_DISABLED_AUTO_AUTOBUILD);
				} else {
					// Incremental validators configured but not selected
					PlatformUI.getWorkbench().getHelpSystem().setHelp(validatorList.getTable(), ContextIds.VALIDATION_PROPERTIES_PAGE_DISABLED_AUTO_NOINCVALSELECTED);
				}
			}

			// if autoButton AND build button are disabled, show the build button's "to enable" text
			if (valWhenBuildButton.getEnabled()) {
				// Do NOT set the table's help back to what it was.
				// Only if auto-validate is enabled should the page go back.
				PlatformUI.getWorkbench().getHelpSystem().setHelp(valWhenBuildButton, ContextIds.VALIDATION_PROPERTIES_PAGE_REBUILD_ENABLED);
			} else {
				//				page.getParent().setFocus();
				validatorList.getTable().setFocus();
				PlatformUI.getWorkbench().getHelpSystem().setHelp(validatorList.getTable(), ContextIds.VALIDATION_PROPERTIES_PAGE_DISABLED_BUILD_NOVALSELECTED);
			}
*/
			// if the override button is disabled, show its "to enable" text.
			if (overrideGlobalButton.getEnabled()) {
				// Do NOT set the table's help back to what it was.
				// Only if auto-validate is enabled should the page go back.
				boolean doesProjectSupportBuildValidation = ValidatorManager.doesProjectSupportBuildValidation(getProject());
				GlobalConfiguration gp = ConfigurationManager.getManager().getGlobalConfiguration();
				//boolean isPrefAuto = gp.isAutoValidate();
				//boolean isPrefManual = gp.isBuildValidate();
				if (doesProjectSupportBuildValidation) {
					// Project supports build validation, so it doesn't matter what the preferences
					// are
					PlatformUI.getWorkbench().getHelpSystem().setHelp(overrideGlobalButton, ContextIds.VALIDATION_PROPERTIES_PAGE_OVERRIDE_ENABLED);
				} /*else if (!doesProjectSupportBuildValidation && (isPrefAuto && isPrefManual)) {
					// Project doesn't support build validation, and the user prefers both auto and
					// manual build validation
					PlatformUI.getWorkbench().getHelpSystem().setHelp(overrideGlobalButton, ContextIds.VALIDATION_PROPERTIES_PAGE_OVERRIDE_ENABLED_CANNOT_HONOUR_BOTH);
				} else if (!doesProjectSupportBuildValidation && isPrefAuto) {
					// Project doesn't support build validation, and the user prefers auto build
					// validation
					PlatformUI.getWorkbench().getHelpSystem().setHelp(overrideGlobalButton, ContextIds.VALIDATION_PROPERTIES_PAGE_OVERRIDE_ENABLED_CANNOT_HONOUR_AUTO);
				} else if (!doesProjectSupportBuildValidation && isPrefManual) {
					// Project doesn't support build validation, and the user prefers manual build
					// validation
					PlatformUI.getWorkbench().getHelpSystem().setHelp(overrideGlobalButton, ContextIds.VALIDATION_PROPERTIES_PAGE_OVERRIDE_ENABLED_CANNOT_HONOUR_MANUAL);
				} else if (!doesProjectSupportBuildValidation && !isPrefAuto && !isPrefManual) {
					// Project doesn't support build validation, but that doesn't matter because the
					// user prefers no build validation.
					PlatformUI.getWorkbench().getHelpSystem().setHelp(overrideGlobalButton, ContextIds.VALIDATION_PROPERTIES_PAGE_OVERRIDE_ENABLED);
				}*/
			} else {
				validatorList.getTable().setFocus();
				// Preference page doesn't allow projects to override
				PlatformUI.getWorkbench().getHelpSystem().setHelp(validatorList.getTable(), ContextIds.VALIDATION_PROPERTIES_PAGE_DISABLED_OVERRIDE);
			}
		}

		/*
		 * Store the current values of the controls into the preference store.
		 */
		private void storeValues() throws InvocationTargetException {
			pagePreferences.setDoesProjectOverride(overrideGlobalButton.getSelection());

			if (pagePreferences.doesProjectOverride()) {
				pagePreferences.setEnabledValidators(getEnabledValidators());
			} else {
				pagePreferences.resetToDefault(); // If the project can't or doesn't override,
				// update its values to match the global
				// preference values.
			}
			pagePreferences.passivate();
		}

		/**
		 * Reads the list of validators, enables the validators which are selected, disables the
		 * validators which are not selected, and if the auto-validate checkbox is chosen, performs
		 * a full validation.
		 */
		public boolean performOk() throws InvocationTargetException {
			// addBuilder MUST be called before storeValues
			// addBuilder adds a builder to the project, and that changes the project description.
			// Changing a project's description triggers the validation framework's "natureChange"
			// migration, and a nature change requires that the list of validators be recalculated.
			// If the builder is added after the values are stored, the stored values are
			// overwritten.
			addBuilder();

			// If this method is being called because an APPLY was hit instead of an OK,
			// recalculate the "can build be enabled" status because the builder may have
			// been added in the addBuilder() call above.
			// Also recalculate the values that depend on the isBuilderConfigured value.
			//isBuilderConfigured = ValidatorManager.doesProjectSupportBuildValidation(getProject());

			// Persist the values.
			storeValues();

			/*if (autoButton.getSelection()) {
				int enabledIncrementalValidators = pagePreferences.numberOfEnabledIncrementalValidators();
				int enabledValidators = pagePreferences.numberOfEnabledValidators();
				if (enabledValidators != enabledIncrementalValidators) {
					// Then some of the enabled validators are not incremental
					int iIconType = org.eclipse.swt.SWT.ICON_INFORMATION;
					Display display = Display.getCurrent();
					Shell shell = (display == null) ? null : display.getActiveShell();
					MessageBox messageBox = new MessageBox(shell, org.eclipse.swt.SWT.OK | iIconType);
					messageBox.setText(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_MSSGBOX_TITLE_NONINC));

					ValidatorMetaData[] vmds = pagePreferences.getEnabledValidators();
					StringBuffer buffer = new StringBuffer(NEWLINE_AND_TAB);
					for (int i = 0; i < vmds.length; i++) {
						ValidatorMetaData vmd = vmds[i];

						if (!vmd.isIncremental()) {
							buffer.append(vmd.getValidatorDisplayName());
							buffer.append(NEWLINE_AND_TAB);
						}
					}
					messageBox.setMessage(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_UI_AUTO_ON_NONINC, new String[]{buffer.toString()}));
					messageBox.open();
				}
			}*/

			if (pagePreferences.hasEnabledValidatorsChanged(oldVmd, false) || ValidatorManager.getManager().isMessageLimitExceeded(getProject())) { // false
				// means
				// that
				// the
				// preference
				// "allow"
				// value
				// hasn't
				// changed
				ValidatorManager.getManager().updateTaskList(getProject()); // Do not remove the
				// exceeded message;
				// only
				// ValidationOperation
				// should do that
				// because it's about to
				// run validation. If
				// the limit is
				// increased, messages
				// may still be missing,
				// so don't remove the
				// "messages may be
				// missing" message.
			}

			return true;
		}

		/**
		 * If the current project doesn't have the validation builder configured on it, add the
		 * builder. Otherwise return without doing anything.
		 */
		private void addBuilder() {
			if (overrideGlobalButton.getSelection()) { // do not add the builder unless the user
				// overrides the preferences
				/*if (autoButton.getSelection() || valWhenBuildButton.getSelection()) {
					ValidatorManager.addProjectBuildValidationSupport(getProject());
				}*/
			}
		}

		public Composite getControl() {
			return page;
		}

		public void dispose() {
			enableAllButton.dispose();
			disableAllButton.dispose();
			validatorList.getTable().dispose();
			messageLabel.dispose();
			//			layout.dispose();
			//			data.dispose();
			emptyRowPlaceholder.dispose();
			overrideGlobalButton.dispose();
			page.dispose();
		}
	}

	/**
	 * ValidationPreferencePage constructor comment.
	 */
	public ValidationPropertiesPage() {
		// Some of the initialization is done in the "initialize" method, which is
		// called by the "getPageType" method, because the current project must
		// be known in order to initialize those fields.
	}

	/**
	 * Given a parent (the Properties guide), create the Validators page to be added to it.
	 */
	protected Control createContents(Composite parent) {
		IProject project = getProject();

		if ((project == null) || !project.isOpen()) {
			_pageImpl = new InvalidPage(parent);
		} else {
			try {
				if (ConfigurationManager.getManager().getProjectConfiguration(project).numberOfValidators() == 0) {
					_pageImpl = new NoValidatorsPage(parent);
				} else {
					_pageImpl = new ValidatorListPage(parent);
				}
			} catch (InvocationTargetException exc) {
				_pageImpl = new InvalidPage(parent);
				displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
			} catch (Throwable exc) {
				_pageImpl = new InvalidPage(parent);
				displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
			}
		}

		return _pageImpl.getControl();
	}

	/**
	 * Since the pages are inner classes of a child PreferencePage, not a PreferencePage itself,
	 * DialogPage's automatic disposal of its children's widgets cannot be used. Instead, dispose of
	 * each inner class' widgets explicitly.
	 */
	public void dispose() {
		super.dispose();
		try {
			_pageImpl.dispose();
		} catch (Throwable exc) {
			logError(exc);
		}
	}

	/**
	 * Returns the highlighted item in the workbench.
	 */
	public IProject getProject() {
		Object element = getElement();

		if (element == null) {
			return null;
		}

		if (element instanceof IProject) {
			return (IProject) element;
		}

		return null;
	}

	protected void noDefaultAndApplyButton() {
		super.noDefaultAndApplyButton();
	}

	/**
	 * Performs special processing when this page's Defaults button has been pressed.
	 * <p>
	 * This is a framework hook method for sublcasses to do special things when the Defaults button
	 * has been pressed. Subclasses may override, but should call <code>super.performDefaults</code>.
	 * </p>
	 */
	protected void performDefaults() {
		super.performDefaults();
		try {
			_pageImpl.performDefaults();
		} catch (InvocationTargetException exc) {
			displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
		} catch (Throwable exc) {
			displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
		}
	}

	/**
	 * When the user presses the "OK" or "Apply" button on the Properties Guide/Properties Page,
	 * respectively, some processing is performed by this PropertyPage. If the page is found, and
	 * completes successfully, true is returned. Otherwise, false is returned, and the guide doesn't
	 * finish.
	 */
	public boolean performOk() {
		try {
			return _pageImpl.performOk();
		} catch (InvocationTargetException exc) {
			displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
			return false;
		} catch (Throwable exc) {
			displayAndLogError(ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_TITLE), ResourceHandler.getExternalizedMessage(ResourceConstants.VBF_EXC_INTERNAL_PAGE), exc);
			return false;
		}
	}

	void logError(Throwable exc) {
		Logger logger = WTPUIPlugin.getLogger();
		if (logger.isLoggingLevel(Level.SEVERE)) {
			LogEntry entry = ValidationUIPlugin.getLogEntry();
			entry.setSourceIdentifier("ValidationPropertiesPage.displayAndLogError"); //$NON-NLS-1$
			entry.setMessageTypeIdentifier(ResourceConstants.VBF_EXC_INTERNAL_PAGE);
			entry.setTargetException(exc);
			logger.write(Level.SEVERE, entry);

			if (exc instanceof InvocationTargetException) {
				if (((InvocationTargetException) exc).getTargetException() != null) {
					entry.setTargetException(((InvocationTargetException) exc).getTargetException());
					logger.write(Level.SEVERE, entry);
				}
			}
		}
	}

	void displayAndLogError(String title, String message, Throwable exc) {
		logError(exc);
		displayMessage(title, message, org.eclipse.swt.SWT.ICON_ERROR);
	}

	private void displayMessage(String title, String message, int iIconType) {
		MessageBox messageBox = new MessageBox(getShell(), org.eclipse.swt.SWT.OK | iIconType | org.eclipse.swt.SWT.APPLICATION_MODAL);
		messageBox.setMessage(message);
		messageBox.setText(title);
		messageBox.open();
	}

	/**
	 * @see org.eclipse.jface.preference.PreferencePage#getDefaultsButton()
	 */
	protected Button getDefaultsButton() {
		return super.getDefaultsButton();
	}
}
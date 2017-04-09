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
package org.eclipse.wst.validation.core;


import java.util.MissingResourceException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Default implementation of the IMessage interface, provided for the convenience of the
 * IValidators. If an IValidator needs to run in both AAT and WSAD then this IMessage implementation
 * should be used; if the IValidator runs in WSAD alone, the WSAD LocalizedMessage may be used in
 * place of this implementation.
 */
public class Message implements IMessage {
	private String id = null;
	private String[] params = null;
	private int severity = MessageFilter.ANY_SEVERITY;
	private Object targetObject = null;
	private String bundleName = null;
	private String groupName = null;
	private int lineNumber = IMessage.LINENO_UNSET;
	private int length = IMessage.OFFSET_UNSET;
	private int offset = IMessage.OFFSET_UNSET;

	public Message() {
		super();
	}

	/**
	 * aBundleName must not be null or the empty string ("") aSeverity must be one of the constants
	 * specified in SeverityEnum anId must not be null or the empty string ("")
	 */
	public Message(String aBundleName, int aSeverity, String anId) {
		this(aBundleName, aSeverity, anId, null, null);
	}

	/**
	 * aBundleName must not be null or the empty string ("") aSeverity must be one of the constants
	 * specified in SeverityEnum anId must not be null or the empty string ("") aParams may be null,
	 * if there are no parameters in the message.
	 */
	public Message(String aBundleName, int aSeverity, String anId, String[] aParams) {
		this(aBundleName, aSeverity, anId, aParams, null);
	}

	/**
	 * aBundleName must not be null or the empty string ("") aSeverity must be one of the constants
	 * specified in SeverityEnum anId must not be null or the empty string ("") aParams may be null,
	 * if there are no parameters in the message. targetObject may be null, if the message does not
	 * pertain to a particular object.
	 */
	public Message(String aBundleName, int aSeverity, String anId, String[] aParams, Object aTargetObject) {
		bundleName = aBundleName;
		severity = aSeverity;
		id = anId;
		params = aParams;
		targetObject = aTargetObject;
	}

	/**
	 * Return the resource bundle which contains the messages, as identified by
	 * 
	 * @link #getBundleName()
	 */
	public ResourceBundle getBundle(Locale locale, ClassLoader classLoader) {
		ResourceBundle bundle = null;
		try {
			if (classLoader == null) {
				bundle = ResourceBundle.getBundle(getBundleName(), locale);
			} else {
				bundle = ResourceBundle.getBundle(getBundleName(), locale, classLoader);
			}
		} catch (MissingResourceException e) {
			e.printStackTrace();
		}
		return bundle;
	}

	/**
	 * @see IMessage#getBundleName()
	 */
	public String getBundleName() {
		return bundleName;
	}

	/**
	 * @see IMessage#getGroupName()
	 */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * @see IMessage#getId()
	 */
	public String getId() {
		return id;
	}

	/**
	 * @see IMessage#getLength()
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @see IMessage#getLineNo()
	 */
	public int getLineNo() {
		return lineNumber;
	}

	/**
	 * @see IMessage#getOffset()
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @see IMessage#getParams()
	 */
	public String[] getParams() {
		return params;
	}

	/**
	 * @see IMessage#getSeverity()
	 */
	public int getSeverity() {
		return severity;
	}

	/**
	 * @see IMessage#getTargetObject()
	 */
	public Object getTargetObject() {
		return targetObject;
	}

	/**
	 * @see IMessage#getText()
	 */
	public String getText() {
		return getText(Locale.getDefault(), null);
	}

	/**
	 * @see IMessage#getText(ClassLoader)
	 */
	public String getText(ClassLoader classLoader) {
		return getText(Locale.getDefault(), classLoader);
	}

	/**
	 * @see IMessage#getText(Locale)
	 */
	public String getText(Locale locale) {
		return getText(locale, null);
	}

	/**
	 * @see IMessage#getText(Locale, ClassLoader)
	 */
	public java.lang.String getText(Locale locale, ClassLoader classLoader) {
		String message = ""; //$NON-NLS-1$

		if (locale == null) {
			return message;
		}

		ResourceBundle bundle = getBundle(locale, classLoader);
		if (bundle == null) {
			return message;
		}

		try {
			message = bundle.getString(getId());

			if (getParams() != null) {
				message = java.text.MessageFormat.format(message, getParams());
			}
		} catch (MissingResourceException exc) {
			System.err.println(exc.getMessage());
			System.err.println(getId());
		} catch (NullPointerException exc) {
			System.err.println(exc.getMessage());
			System.err.println(getId());
		}

		return message;
	}

	/**
	 * @see IMessage#setBundleName(String)
	 */
	public void setBundleName(String aBundleName) {
		bundleName = aBundleName;
	}

	/**
	 * @see IMessage#setGroupName(String)
	 */
	public void setGroupName(String name) {
		groupName = name;
	}

	/**
	 * @see IMessage#setId(String)
	 */
	public void setId(String newId) {
		id = newId;
	}

	/**
	 * @see IMessage#setLength(int)
	 */
	public void setLength(int length) {
		if (length < 0) {
			length = IMessage.OFFSET_UNSET;
		}
		this.length = length;
	}

	/**
	 * @see IMessage#setLineNo(int)
	 */
	public void setLineNo(int lineNumber) {
		if (lineNumber < 0) {
			this.lineNumber = IMessage.LINENO_UNSET;
		} else {
			this.lineNumber = lineNumber;
		}
	}

	/**
	 * @see IMessage#setOffset(int)
	 */
	public void setOffset(int offset) {
		if (offset < 0) {
			offset = IMessage.OFFSET_UNSET;
		}
		this.offset = offset;
	}

	/**
	 * @see IMessage#setParams(String[])
	 */
	public void setParams(String[] newParams) {
		params = newParams;
	}

	/**
	 * @see IMessage#setSeverity(int)
	 */
	public void setSeverity(int newSeverity) {
		severity = newSeverity;
	}

	/**
	 * @see IMessage#setTargetObject(Object)
	 */
	public void setTargetObject(Object obj) {
		targetObject = obj;
	}
}
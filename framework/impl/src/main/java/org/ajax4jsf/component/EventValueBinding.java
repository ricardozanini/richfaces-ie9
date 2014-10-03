/**
 * License Agreement.
 *
 * Rich Faces - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package org.ajax4jsf.component;

import org.richfaces.webapp.taglib.ValueBindingValueExpressionAdaptor;


/**
 * Inner class for build event string for parent component.
 * 
 * @author shura (latest modification by $Author: alexsmirnov $)
 * @version $Revision: 1.1.2.1 $ $Date: 2007/01/09 18:57:38 $ Disadvantages -
 *          not rebuild event string setted as EL expression. TODO - save
 *          expressions for build event string at render phase.
 */
public class EventValueBinding extends ValueBindingValueExpressionAdaptor {

	/**
         * 
         */
	private static final long serialVersionUID = -6583167387542332290L;

	/**
	 * Default constructor for restoreState.
	 */
	public EventValueBinding() {
		super(new EventValueExpression());
	}

	/**
	 * Constructor for build from AjaxComponent.
	 * 
	 * @param update
	 */
	public EventValueBinding(AjaxSupport update) {
		super(new EventValueExpression(update));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.StateHolder#isTransient()
	 */
	public boolean isTransient() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.StateHolder#setTransient(boolean)
	 */
	public void setTransient(boolean newTransientValue) {
	}

}
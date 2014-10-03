/*
 * JBoss, Home of Professional Open Source
 * Copyright ${year}, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.ajax4jsf.component;

import javax.el.ELException;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.ajax4jsf.Messages;
import org.ajax4jsf.renderkit.AjaxRendererUtils;

/**
 * @author Nick Belaevski
 * 
 */
public class EventValueExpression extends AbstractEventValueExpression
		implements StateHolder {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6583167387542332290L;

	private String componentId;

	/**
	 * current update component. transient since saved state as component.
	 */
	transient private AjaxSupport component = null;

	public EventValueExpression() {
	}

	public EventValueExpression(AjaxSupport component) {

		super();
		this.component = component;
	}

	public boolean isTransient() {
		return false;
	}

	public void setTransient(boolean newTransientValue) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.faces.component.StateHolder#restoreState(javax.faces.context.
	 * FacesContext, java.lang.Object)
	 */
	public void restoreState(FacesContext context, Object state) {
		componentId = (String) state;
	}

	public Object saveState(FacesContext context) {
		if (null == component) {
			return componentId;
		} else {
			return AjaxRendererUtils
					.getAbsoluteId((UIComponent) getComponent());
		}
	}

	/**
	 * @param component
	 *            the component to set
	 */
	public void setComponent(AjaxSupport component) {
		this.component = component;
	}

	@Override
	protected AjaxSupport getComponent() {
		if (component == null) {
			FacesContext facesContext = FacesContext.getCurrentInstance();
			UIComponent uiComponent = facesContext.getViewRoot().findComponent(
					componentId);
			if (null != uiComponent && uiComponent instanceof AjaxSupport) {
				component = (AjaxSupport) uiComponent;
			} else {
				throw new ELException(Messages.getMessage(
						Messages.COMPONENT_NOT_FOUND, componentId));
			}

		}

		return component;
	}

}

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

import javax.el.ELException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.ajax4jsf.Messages;

/**
 * @author Maksim Kaszynski
 * 
 */
public class EventValueExpression extends AbstractEventValueExpression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6583167387542332290L;

	private String componentId;
	
	public EventValueExpression() {
		super();
	}

	public EventValueExpression(AjaxSupport component) {
		super();
		
		this.componentId = ((UIComponent) component).getId();
	}

	private UIComponent quickFindComponent(UIComponent component, String componentId) {
		UIComponent result = null;
		
		if (component.getFacetCount() > 0) {
			for (UIComponent c : component.getFacets().values()) {
				if (componentId.equals(c.getId())) {
					result = c;
					break;
				}
			}
		}
		
		if (result == null && component.getChildCount() > 0) {
			for (UIComponent c : component.getChildren()) {
				if (componentId.equals(c.getId())) {
					result = c;
					break;
				}
			}
		}
		
		return result;
	}
	
	protected AjaxSupport getComponent() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		UIComponent currentComponent = UIComponent.getCurrentComponent(facesContext);
		UIComponent foundComponent = null;
		
		if (currentComponent != null) {
			foundComponent = quickFindComponent(currentComponent, componentId);
		}
		
		if (foundComponent == null || !(foundComponent instanceof AjaxSupport)) {
			throw new ELException(Messages.getMessage(
					Messages.COMPONENT_NOT_FOUND, componentId));
		} 
		
		return (AjaxSupport) foundComponent;
	}

	@Override
	public void setComponent(AjaxSupport ajaxSupport) {
		this.componentId = ((UIComponent) ajaxSupport).getId();
	}

}

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

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;

import org.ajax4jsf.Messages;

/**
 * @author Nick Belaevski
 *
 */
public abstract class AbstractEventValueExpression extends ValueExpression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2600139922687495517L;

	protected abstract AjaxSupport getComponent();

	/**
	 * @param component
	 *            the component to set
	 */
	public abstract void setComponent(AjaxSupport component);

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.el.Expression#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.el.ValueExpression#getExpectedType()
	 */
	@Override
	public Class<?> getExpectedType() {
		return String.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.el.Expression#getExpressionString()
	 */
	@Override
	public String getExpressionString() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.el.ValueExpression#getType(javax.el.ELContext)
	 */
	@Override
	public Class<?> getType(ELContext context) {
		return String.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.el.ValueExpression#getValue(javax.el.ELContext)
	 */
	@Override
	public Object getValue(ELContext context) {
		AjaxSupport ajaxSupport = getComponent();
		if (((UIComponent) ajaxSupport).isRendered()) {
			return ajaxSupport.getEventString();
		} else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.el.Expression#hashCode()
	 */
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.el.Expression#isLiteralText()
	 */
	@Override
	public boolean isLiteralText() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.el.ValueExpression#isReadOnly(javax.el.ELContext)
	 */
	@Override
	public boolean isReadOnly(ELContext context) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.el.ValueExpression#setValue(javax.el.ELContext,
	 * java.lang.Object)
	 */
	@Override
	public void setValue(ELContext context, Object value) {
		throw new ELException(Messages.getMessage(Messages.EVENT_IS_READ_ONLY));
	}

	
}

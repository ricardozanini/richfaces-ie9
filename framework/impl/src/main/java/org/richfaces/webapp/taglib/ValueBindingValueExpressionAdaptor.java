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
package org.richfaces.webapp.taglib;

import java.io.Serializable;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.ValueBinding;

/**
 * @author Maksim Kaszynski
 *
 */
@SuppressWarnings("deprecation")
public class ValueBindingValueExpressionAdaptor extends ValueBinding implements StateHolder, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3242030160417022811L;

	private ValueExpression expression;
	private boolean tranzient;
	
	/* (non-Javadoc)
	 * @see javax.faces.el.ValueBinding#getType(javax.faces.context.FacesContext)
	 */
	@Override
	public Class<?> getType(FacesContext context) throws EvaluationException,
			PropertyNotFoundException {
		try {
			return expression.getType(context.getELContext());
		} catch (javax.el.PropertyNotFoundException e) {
			throw new PropertyNotFoundException(e);
		} catch (ELException e) {
			throw new EvaluationException(e);
		}
	}

	/* (non-Javadoc)
	 * @see javax.faces.el.ValueBinding#getValue(javax.faces.context.FacesContext)
	 */
	@Override
	public Object getValue(FacesContext context) throws EvaluationException,
			PropertyNotFoundException {
		try {
			return expression.getValue(context.getELContext());
		} catch(javax.el.PropertyNotFoundException e) {
			throw new PropertyNotFoundException(e);
		} catch (ELException e) {
			throw new EvaluationException(e);
		}
	}

	/* (non-Javadoc)
	 * @see javax.faces.el.ValueBinding#isReadOnly(javax.faces.context.FacesContext)
	 */
	@Override
	public boolean isReadOnly(FacesContext context) throws EvaluationException,
			PropertyNotFoundException {
		try {
			return expression.isReadOnly(context.getELContext());
		} catch(javax.el.PropertyNotFoundException e) {
			throw new PropertyNotFoundException(e);
		} catch (ELException e) {
			throw new EvaluationException(e);
		}
	}

	/* (non-Javadoc)
	 * @see javax.faces.el.ValueBinding#setValue(javax.faces.context.FacesContext, java.lang.Object)
	 */
	@Override
	public void setValue(FacesContext context, Object value)
			throws EvaluationException, PropertyNotFoundException {
		
		try {
			expression.setValue(context.getELContext(), value);
		} catch(javax.el.PropertyNotFoundException e) {
			throw new PropertyNotFoundException(e);
		} catch (ELException e) {
			throw new EvaluationException(e);
		}
		
	}
	
	public boolean isTransient() {
		return tranzient;
	}
	
	public void restoreState(FacesContext context, Object state) {
		if (state instanceof ValueExpression) {
			expression = (ValueExpression) state;
		} else {
			expression = (ValueExpression) UIComponentBase.restoreAttachedState(context, state);
		}
	}
	
	public Object saveState(FacesContext context) {
		Object result = null;
		if (!tranzient) {
			if (expression instanceof StateHolder) {
				result = UIComponentBase.saveAttachedState(context, expression);
			} else {
				result = expression;
			}
		}
		
		return result;
	}
	
	public void setTransient(boolean newTransientValue) {
		tranzient = newTransientValue;
	}

	public ValueExpression getExpression() {
		return expression;
	}

	public void setExpression(ValueExpression expression) {
		this.expression = expression;
	}
	
	public ValueBindingValueExpressionAdaptor() {
		// TODO Auto-generated constructor stub
	}

	public ValueBindingValueExpressionAdaptor(ValueExpression expression) {
		super();
		this.expression = expression;
	}


}

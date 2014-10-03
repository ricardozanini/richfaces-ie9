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

package org.ajax4jsf.webapp.taglib;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;

import org.ajax4jsf.tests.AbstractAjax4JsfTestCase;
import org.ajax4jsf.tests.MockValueExpression;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.richfaces.webapp.taglib.ValueBindingValueExpressionAdaptor;

/**
 * @author Nick Belaevski
 * 
 */
public class ValueBindingValueExpressionAdaptorTest extends
		AbstractAjax4JsfTestCase {

	public ValueBindingValueExpressionAdaptorTest(String name) {
		super(name);
	}

	public static final class StateHolderTestValueExpression extends
			MockValueExpression implements StateHolder {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4288036600538778835L;

		public StateHolderTestValueExpression() {
			super(null);
		}

		public StateHolderTestValueExpression(Object value) {
			super(value);
		}

		public boolean isTransient() {
			return false;
		}

		public void restoreState(FacesContext context, Object state) {
			setValue(context.getELContext(), state);
		}

		public Object saveState(FacesContext context) {
			return getValue(context.getELContext());
		}

		public void setTransient(boolean newTransientValue) {
		}

	}

	private ValueBindingValueExpressionAdaptor saveRestoreState(
			ValueBindingValueExpressionAdaptor adaptor) {
		ValueBindingValueExpressionAdaptor restoredAdaptor = new ValueBindingValueExpressionAdaptor();
		restoredAdaptor.restoreState(facesContext, adaptor
				.saveState(facesContext));
		return restoredAdaptor;
	}

	private ValueBindingValueExpressionAdaptor serializeDeserialize(
			ValueBindingValueExpressionAdaptor adaptor) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		try {
			oos.writeObject(adaptor);
		} finally {
			oos.close();
		}

		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(
				baos.toByteArray()));
		try {
			return (ValueBindingValueExpressionAdaptor) ois.readObject();
		} finally {
			ois.close();
		}
	}

	public void testSaveRestoreState() throws Exception {
		ValueBindingValueExpressionAdaptor adaptor1 = new ValueBindingValueExpressionAdaptor(
				new MockValueExpression("testValue1"));

		assertEquals("testValue1", saveRestoreState(adaptor1).getValue(
				facesContext));

		ValueBindingValueExpressionAdaptor adaptor2 = new ValueBindingValueExpressionAdaptor(
				new StateHolderTestValueExpression("testValue2"));

		assertEquals("testValue2", saveRestoreState(adaptor2).getValue(
				facesContext));
	}

	public void testSerializeDeserialize() throws Exception {
		ValueBindingValueExpressionAdaptor adaptor1 = new ValueBindingValueExpressionAdaptor(
				new MockValueExpression("testValue1"));

		assertEquals("testValue1", serializeDeserialize(adaptor1).getValue(
				facesContext));

		ValueBindingValueExpressionAdaptor adaptor2 = new ValueBindingValueExpressionAdaptor(
				new StateHolderTestValueExpression("testValue2"));

		assertEquals("testValue2", serializeDeserialize(adaptor2).getValue(
				facesContext));
	}
}

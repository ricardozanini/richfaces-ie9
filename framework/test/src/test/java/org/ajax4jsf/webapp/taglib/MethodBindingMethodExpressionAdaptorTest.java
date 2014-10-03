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

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.MethodInfo;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;

import org.ajax4jsf.tests.AbstractAjax4JsfTestCase;
import org.apache.commons.io.output.ByteArrayOutputStream;

import com.sun.faces.application.MethodBindingMethodExpressionAdapter;

/**
 * @author Nick Belaevski
 * 
 */
public class MethodBindingMethodExpressionAdaptorTest extends
		AbstractAjax4JsfTestCase {

	public MethodBindingMethodExpressionAdaptorTest(String name) {
		super(name);
	}

	public static class TestMethodExpression extends MethodExpression {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8789388146351873628L;

		private String returnValue;

		public TestMethodExpression() {
			this(null);
		}

		public TestMethodExpression(String returnValue) {
			super();
			this.returnValue = returnValue;
		}

		public String getReturnValue() {
			return returnValue;
		}
		
		public void setReturnValue(String returnValue) {
			this.returnValue = returnValue;
		}
		
		@Override
		public MethodInfo getMethodInfo(ELContext context) {
			return null;
		}

		@Override
		public Object invoke(ELContext context, Object[] params) {
			return returnValue;
		}

		@Override
		public boolean equals(Object obj) {
			return false;
		}

		@Override
		public String getExpressionString() {
			return null;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean isLiteralText() {
			return false;
		}

	}

	public static final class StateHolderTestMethodExpression extends TestMethodExpression
			implements StateHolder {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 4430232388824229636L;

		public StateHolderTestMethodExpression() {
			super();
		}
		
		public StateHolderTestMethodExpression(String string) {
			super(string);
		}

		public boolean isTransient() {
			return false;
		}

		public void restoreState(FacesContext context, Object state) {
			setReturnValue((String) state);
		}

		public Object saveState(FacesContext context) {
			return getReturnValue();
		}

		public void setTransient(boolean newTransientValue) {
		}

	}

	private MethodBindingMethodExpressionAdapter saveRestoreState(
			MethodBindingMethodExpressionAdapter adaptor) {
		MethodBindingMethodExpressionAdapter restoredAdaptor = new MethodBindingMethodExpressionAdapter();
		restoredAdaptor.restoreState(facesContext, adaptor
				.saveState(facesContext));
		return restoredAdaptor;
	}

	private MethodBindingMethodExpressionAdapter serializeDeserialize(
			MethodBindingMethodExpressionAdapter adaptor) throws Exception {
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
			return (MethodBindingMethodExpressionAdapter) ois.readObject();
		} finally {
			ois.close();
		}
	}

	public void testSaveRestoreState() throws Exception {
		MethodBindingMethodExpressionAdapter adaptor1 = new MethodBindingMethodExpressionAdapter(
				new TestMethodExpression("testValue1"));

		assertEquals("testValue1", saveRestoreState(adaptor1).invoke(facesContext, null));

		MethodBindingMethodExpressionAdapter adaptor2 = new MethodBindingMethodExpressionAdapter(
				new StateHolderTestMethodExpression("testValue2"));

		assertEquals("testValue2", saveRestoreState(adaptor2).invoke(facesContext, null));
	}

	public void testSerializeDeserialize() throws Exception {
		MethodBindingMethodExpressionAdapter adaptor1 = new MethodBindingMethodExpressionAdapter(
				new TestMethodExpression("testValue1"));

		assertEquals("testValue1", serializeDeserialize(adaptor1).invoke(facesContext, null));

		MethodBindingMethodExpressionAdapter adaptor2 = new MethodBindingMethodExpressionAdapter(
				new StateHolderTestMethodExpression("testValue2"));

		assertEquals("testValue2", serializeDeserialize(adaptor2).invoke(facesContext, null));
	}
}

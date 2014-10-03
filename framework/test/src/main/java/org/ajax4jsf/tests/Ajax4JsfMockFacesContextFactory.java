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

package org.ajax4jsf.tests;

import java.lang.reflect.Constructor;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shale.test.mock.MockExternalContext12;
import org.apache.shale.test.mock.MockFacesContextFactory;

public class Ajax4JsfMockFacesContextFactory extends MockFacesContextFactory {

    private Constructor<? extends FacesContext> constructor = null;

    private static Class<?>[] facesContextSignature = new Class[] {
    	ExternalContext.class, Lifecycle.class
    };
	
    public Ajax4JsfMockFacesContextFactory() {
        Class<? extends FacesContext> clazz = null;

        // Try to load the 2.0 version of our mock FacesContext class
        try {
            clazz = this.getClass().getClassLoader().loadClass("org.ajax4jsf.tests.MockFacesContext20").
            	asSubclass(FacesContext.class);
			//force loading classes for fields
            clazz.getDeclaredFields();
            
            constructor = clazz.getConstructor(facesContextSignature);
        } catch (NoClassDefFoundError e) {
            // We are not running on JSF 2.0
            clazz = null;
            constructor = null;
        } catch (ClassNotFoundException e) {
            // Same as above
            clazz = null;
            constructor = null;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new FacesException(e);
        }
	}
	
	@Override
	public FacesContext getFacesContext(Object context, Object request,
			Object response, Lifecycle lifecycle) throws FacesException {
		
		if (constructor != null) {

			try {
				MockExternalContext12 mockExternalContext12 = new MockExternalContext12((ServletContext) context, 
						(HttpServletRequest) request, (HttpServletResponse) response);
				
				return constructor.newInstance(mockExternalContext12, lifecycle);
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new FacesException(e.getMessage(), e);
			}
		}
		
		return super.getFacesContext(context, request, response, lifecycle);
	}
}

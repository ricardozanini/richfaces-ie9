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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.context.FacesContext;

import org.ajax4jsf.resource.FacesResourceContext;
import org.ajax4jsf.resource.InternetResource;
import org.ajax4jsf.resource.InternetResourceBuilder;
import org.ajax4jsf.resource.ResourceNotFoundException;

public class MockResourceHandler extends ResourceHandler {

	@Override
	public Resource createResource(String resourceName) {
		return createResource(resourceName, null);
	}

	@Override
	public Resource createResource(String resourceName, String libraryName) {
		return createResource(resourceName, libraryName, null);
	}

	@Override
	public Resource createResource(String resourceName, String libraryName,
			String contentType) {

		StringBuilder path = new StringBuilder();
		if (libraryName != null) {
			path.append(libraryName);
		}
		
		if (libraryName.length() > 0) {
			path.append('/');
		}
		
		path.append(resourceName);
		
		InternetResourceBuilder resourceBuilder = InternetResourceBuilder.getInstance();
		try {
			final InternetResource resource = resourceBuilder.getResource(path.toString());
			return new Resource() {
				
				@Override
				public boolean userAgentNeedsUpdate(FacesContext context) {
					return true;
				}
				
				@Override
				public URL getURL() {
					throw new UnsupportedOperationException();
				}
				
				@Override
				public Map<String, String> getResponseHeaders() {
					return new HashMap<String, String>();
				}
				
				@Override
				public String getRequestPath() {
					return resource.getUri(FacesContext.getCurrentInstance(), null);
				}
				
				@Override
				public InputStream getInputStream() throws IOException {
					FacesContext facesContext = FacesContext.getCurrentInstance();
					return resource.getResourceAsStream(new FacesResourceContext(facesContext));
				}
			};
		} catch (ResourceNotFoundException e) {
			//ignore
		}
		
		return null;
	}

	@Override
	public String getRendererTypeForResourceName(String resourceName) {
		if (resourceName.endsWith(".js")) {
			return "javax.faces.resource.Script";
		}
		
		if (resourceName.endsWith(".css")) {
			return "javax.faces.resource.Stylesheet";
		}
		
		return null;
	}

	@Override
	public void handleResourceRequest(FacesContext context) throws IOException {
		throw new IllegalStateException();
	}

	@Override
	public boolean isResourceRequest(FacesContext context) {
		return false;
	}

	@Override
	public boolean libraryExists(String libraryName) {
		return true;
	}

}

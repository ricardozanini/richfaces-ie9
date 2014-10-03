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
package org.richfaces.context;

import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextWrapper;

/**
 * @author Nick Belaevski
 *
 */
public class PartialViewContextImpl extends PartialViewContextWrapper {

	private static final String PARTIAL_REQUEST_INDICATOR_ATTTRIBUTE_NAME = PartialViewContextImpl.class.getName();
	
	private PartialViewContext partialViewContext;

	private FacesContext facesContext;
	
	public PartialViewContextImpl(PartialViewContext partialViewContext, FacesContext facesContext) {
		this.partialViewContext = partialViewContext;
		this.facesContext = facesContext;
	}

	/* Hack for Sun RI implementation of StateManager/ResponseStateManager */
	private static Object getPartialViewContextIndicator(FacesContext facesContext) {
		return facesContext.getAttributes().get(PARTIAL_REQUEST_INDICATOR_ATTTRIBUTE_NAME);
	}

	private static void setPartialViewContextIndicator(FacesContext facesContext, Object value) {
		facesContext.getAttributes().put(PARTIAL_REQUEST_INDICATOR_ATTTRIBUTE_NAME, value);
	}
	
	/**
	 * Hack for Sun RI implementation of StateManager/ResponseStateManager
	 * @param facesContext
	 */
	public static void setupPartialViewContextIndicator(FacesContext facesContext) {
		setPartialViewContextIndicator(facesContext, Boolean.TRUE);
	}

	/**
	 * Hack for Sun RI implementation of StateManager/ResponseStateManager
	 * @param facesContext
	 */
	public static void resetPartialViewContextIndicator(FacesContext facesContext) {
		setPartialViewContextIndicator(facesContext, null);
	}
	/* End hack */

	
	@Override
	public PartialViewContext getWrapped() {
		return partialViewContext;
	}

	@Override
	public boolean isAjaxRequest() {
		if (partialViewContext.isAjaxRequest()) {
			return true;
		}
		
		/* Hack for Sun RI implementation of StateManager/ResponseStateManager */
		return Boolean.TRUE.equals(getPartialViewContextIndicator(facesContext));
	}
	
	@Override
	public boolean isPartialRequest() {
		if (partialViewContext.isPartialRequest()) {
			return true;
		}

		/* Hack for Sun RI implementation of StateManager/ResponseStateManager */
		return Boolean.TRUE.equals(getPartialViewContextIndicator(facesContext));
	}

	@Override
	public void setPartialRequest(boolean isPartialRequest) {
		partialViewContext.setPartialRequest(isPartialRequest);
	}
	
}

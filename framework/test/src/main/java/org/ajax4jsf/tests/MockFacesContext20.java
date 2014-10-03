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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.FactoryFinder;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextFactory;
import javax.faces.event.PhaseId;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.render.RenderKit;

import org.apache.shale.test.mock.MockFacesContext12;

import com.sun.faces.renderkit.RenderKitUtils;

public class MockFacesContext20 extends MockFacesContext12 {

	private Map<Object, Object> attributes = new HashMap<Object, Object>();
	private ExceptionHandler exceptionHandler = null;
	private boolean validationFailed = false;
	private PhaseId phaseId = null;
	private PartialViewContext partialViewContext = null;

	public MockFacesContext20(ExternalContext externalContext, Lifecycle lifecycle) {
		super(externalContext, lifecycle);
	}

	@Override
	public Map<Object, Object> getAttributes() {
		return attributes;
	}

	@Override
	public PartialViewContext getPartialViewContext() {
		if (partialViewContext == null) {
			PartialViewContextFactory partialViewContextFactory = (PartialViewContextFactory) FactoryFinder.
			getFactory(FactoryFinder.PARTIAL_VIEW_CONTEXT_FACTORY);

			partialViewContext = partialViewContextFactory.getPartialViewContext(this);
		}

		return partialViewContext;
	}

	@Override
	public ExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	@Override
	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	private List<FacesMessage> createMessageList(Iterator<?> messagesIterator) {
		List<FacesMessage> result = new ArrayList<FacesMessage>();
		while (messagesIterator.hasNext()) {
			FacesMessage nextMessage = (FacesMessage) messagesIterator.next();
			result.add(nextMessage);
		}
		
		return Collections.unmodifiableList(result);
	}
	
	@Override
	public List<FacesMessage> getMessageList() {
		return createMessageList(getMessages());
	}

	@Override
	public List<FacesMessage> getMessageList(String clientId) {
		return createMessageList(getMessages(clientId));
	}

	@Override
	public boolean isValidationFailed() {
		return validationFailed;
	}

	@Override
	public void validationFailed() {
		validationFailed = true;
	}

	@Override
	public boolean isPostback() {
		RenderKit rk = this.getRenderKit();
		boolean postback;
		if (rk != null) {
			postback = rk.getResponseStateManager().isPostback(this);
		} else {
			// ViewRoot hasn't been set yet, so calculate the RK
			ViewHandler vh = this.getApplication().getViewHandler();
			String rkId = vh.calculateRenderKitId(this);
			postback = RenderKitUtils.getResponseStateManager(this, rkId).isPostback(this);
		}

		return postback;
	}

	@Override
	public PhaseId getCurrentPhaseId() {
		return phaseId;
	}

	@Override
	public void setCurrentPhaseId(PhaseId currentPhaseId) {
		phaseId = currentPhaseId;
	}

}

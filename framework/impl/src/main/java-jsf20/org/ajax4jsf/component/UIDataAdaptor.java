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

import java.util.Iterator;

import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;

/**
 * @author Nick Belaevski
 *
 */
public abstract class UIDataAdaptor extends UIDataAdaptorBase {

	public boolean visitTree(VisitContext context, VisitCallback callback) {

		// First check to see whether we are visitable. If not
		// short-circuit out of this subtree, though allow the
		// visit to proceed through to other subtrees.
		if (!isVisitable(context))
			return false;

		// Push ourselves to EL before visiting
		FacesContext facesContext = context.getFacesContext();
		pushComponentToEL(facesContext, null);

		try {
			// Visit ourselves. Note that we delegate to the
			// VisitContext to actually perform the visit.
			VisitResult result = context.invokeVisitCallback(this, callback);

			// If the visit is complete, short-circuit out and end the visit
			if (result == VisitResult.COMPLETE)
				return true;

			// Visit children if necessary
			if (result == VisitResult.ACCEPT) {
				Iterator<UIComponent> kids = this.getFacetsAndChildren();

				while (kids.hasNext()) {
					boolean done = kids.next().visitTree(context, callback);

					// If any kid visit returns true, we are done.
					if (done)
						return true;
				}
			}
		} finally {
			// Pop ourselves off the EL stack
			popComponentFromEL(facesContext);
		}

		// Return false to allow the visit to continue
		return false;
	}
}

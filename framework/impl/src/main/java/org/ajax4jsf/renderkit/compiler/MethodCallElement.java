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

package org.ajax4jsf.renderkit.compiler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodNotFoundException;
import javax.faces.render.Renderer;

import org.ajax4jsf.Messages;
import org.ajax4jsf.renderkit.RendererBase;
import org.ajax4jsf.renderkit.RendererUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

/**
 * @author asmirnov@exadel.com (latest modification by $Author: alexsmirnov $)
 * @version $Revision: 1.1.2.1 $ $Date: 2007/01/09 18:57:47 $
 * 
 */
public class MethodCallElement extends ElementBase {

	public static final String UTILS_PREFIX = "utils.";

	static final Log _log = LogFactory.getLog(MethodCallElement.class);

	private String _name = null;

	private List<MethodParameterElement> parameters = new ArrayList<MethodParameterElement>(
			3);

	private volatile Invoker invoker;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ajax4jsf.renderkit.compiler.CompiledXML#encode(javax.faces.render
	 * .Renderer, javax.faces.context.FacesContext,
	 * javax.faces.component.UIComponent)
	 */
	public void encode(TemplateContext context) throws IOException {
		getValue(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ajax4jsf.renderkit.compiler.ElementBase#encode(org.ajax4jsf.renderkit
	 * .compiler.TemplateContext, java.lang.String)
	 */
	public void encode(TemplateContext context, String breakPoint)
			throws IOException {
		// Text not contain breakpoints.
		encode(context);
	}

	public Object getValue(TemplateContext context) throws FacesException {
		if (null == invoker) {
			throw new FacesException(Messages
					.getMessage(Messages.RENDERER_METHOD_NOT_SET_ERROR));
		}
		// prepare method params. we attempt to call 3 signatures :
		// a) name(FacesContext,UIComponent [, param0...])
		// b) name(TempalateContext [,param0...])
		// c) name([param0...])
		Object[] values = computeParameterValues(context);

		return invoker.invokeMethod(context, values);
		// perform childrens.
		// super.encode(renderer,context,component);
	}

	public Object[] computeParameterValues(TemplateContext context) {
		Object[] ps = new Object[parameters.size()];
		for (int i = 0; i < ps.length; i++) {
			ps[i] = parameters.get(i).valueGetter.getValueOrDefault(context);
		}
		return ps;
	}

	public void addParameter(MethodParameterElement parameter) {
		parameters.add(parameter);
	}

	/**
	 * @return Returns the methodName.
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @param methodName
	 *            The methodName to set.
	 */
	public void setName(String methodName) {
		if (methodName.startsWith(UTILS_PREFIX)) {
			this._name = methodName.substring(UTILS_PREFIX.length());
			this.invoker = getRendererUtilsInvoker(_name);
		} else if (methodName.indexOf('.') >= 0) {
			this._name = methodName;
			this.invoker = getStaticInvoker(_name);
		} else {
			this._name = methodName;
			this.invoker = getRendererInvoker(_name);
		}
	}

	public Invoker getStaticInvoker(String methodName) {
		StaticInvoker invoker = new StaticInvoker(methodName);
		return invoker;
	}

	public Invoker getRendererInvoker(String methodName) {
		RendererInvoker invoker = new RendererInvoker(methodName);
		return invoker;
	}

	public Invoker getRendererUtilsInvoker(String methodName) {
		RendererInvoker invoker = new RendererUtilsInvoker(methodName);
		return invoker;
	}

	public String getTag() {
		return HtmlCompiler.NS_PREFIX + HtmlCompiler.CALL_TAG;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ajax4jsf.renderkit.compiler.ElementBase#setParent(org.ajax4jsf.renderkit
	 * .compiler.PreparedTemplate)
	 */
	public void setParent(PreparedTemplate parent) throws SAXException {
		super.setParent(parent);
		if (getName() == null) {
			throw new SAXException(Messages.getMessage(
					Messages.NO_NAME_ATTRIBUTE_ERROR, getTag()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.ajax4jsf.renderkit.compiler.ElementBase#getString(org.ajax4jsf.renderkit
	 * .compiler.TemplateContext)
	 */
	public String getString(TemplateContext context) throws FacesException {
		Object result = getValue(context);
		if (null == result || result.toString().length() == 0) {
			result = "";
		}
		return result.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ajax4jsf.renderkit.compiler.ElementBase#getAllowedClasses()
	 */
	@SuppressWarnings("unchecked")
	protected Class<? extends ElementBase>[] getAllowedClasses() {
		return new Class[] { MethodParameterElement.class,
				ResourceElement.class };
	}

}

abstract class Invoker {

	protected volatile Signature current;

	protected String methodName;

	public Invoker(String name) {
		this.methodName = name;
	}

	public Object invokeMethod(TemplateContext context, Object[] parameters) {
		Object result = null;
		Class<?>[] parameterTypes = new Class[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			Object parameter = parameters[i];
			if (null != parameter) {
				parameterTypes[i] = parameter.getClass();
			}
		}
		// Use a copy to avoid synchronization.
			Signature signature = current;
			if (null == signature
					|| !signature.isApplicable(context, getInvokedClass(context),
							parameterTypes)) {
				// Clalculate target signature
				signature = provideMethod(context, parameterTypes);
				current = signature;
			}
			try {
				result = signature.invoke(context, getInvokedObject(context),
						parameters);
			} catch (IllegalArgumentException e) {
				throw new FacesException(e);
			} catch (IllegalAccessException e) {
				handleIllegalAccessException(context, e);
			} catch (InvocationTargetException e) {
				handleInvocationTargetException(context, e);
			}
		return result;
	}

	abstract void handleInvocationTargetException(TemplateContext context,
			InvocationTargetException e);

	abstract void handleIllegalAccessException(TemplateContext context,
			IllegalAccessException e);

	abstract void handleMethodNotFoundException(TemplateContext context);

	public abstract Class<?> getInvokedClass(TemplateContext context);

	public abstract Object getInvokedObject(TemplateContext context);

	protected Signature provideMethod(TemplateContext context,
			Class<?>[] parameterTypes) {
		Class<?> cls = getInvokedClass(context);
		if (null != cls) {
			// TODO - cache signatures ?
			Object object = getInvokedObject(context);
			Method[] methods = cls.getMethods();
			for (int m = 0; m < methods.length; m++) {
				if (methods[m].getName().equals(methodName)
						&& (object != null || Modifier.isStatic(methods[m]
								.getModifiers()))) {
					Signature s = new Signature0(methods[m]);
					if (s.isApplicable(context, cls, parameterTypes)) {
						return s;
					}
					s = new Signature1(methods[m]);
					if (s.isApplicable(context, cls, parameterTypes)) {
						return s;
					}
					s = new Signature2(methods[m]);
					if (s.isApplicable(context, cls, parameterTypes)) {
						return s;
					}
				}
			}
		}
		handleMethodNotFoundException(context);
		return null;
	}

	static boolean isMatching(Class<?>[] cs, Class<?>[] args) {
		if (cs.length != args.length)
			return false;
		for (int i = 0; i < cs.length; i++) {
			if (args[i] != null && !cs[i].isAssignableFrom(args[i]))
				return false;
		}
		return true;
	}
}

class RendererInvoker extends Invoker {

	public RendererInvoker(String methodName) {
		super(methodName);
	}

	public Class<? extends Object> getInvokedClass(TemplateContext context) {
		Object object = getInvokedObject(context);
		return (object != null) ? object.getClass() : Renderer.class;
	}

	public Object getInvokedObject(TemplateContext context) {
		return context.getRenderer();
	}

	void handleInvocationTargetException(TemplateContext context,
			InvocationTargetException e) {
		String logMessage = Messages.getMessage(Messages.METHOD_CALL_ERROR_1a,
				methodName, context.getComponent().getId());
		String excMessage = Messages.getMessage(Messages.METHOD_CALL_ERROR_2a,
				new Object[] { methodName, context.getComponent().getId(),
						e.getCause().getMessage() });
		MethodCallElement._log.error(logMessage, e);
		throw new FacesException(excMessage, e);
	}

	void handleIllegalAccessException(TemplateContext context,
			IllegalAccessException e) {
		String logMessage = Messages.getMessage(Messages.getMessage(
				Messages.METHOD_CALL_ERROR_3a, methodName, context
						.getComponent().getId()));
		String excMessage = Messages.getMessage(Messages.getMessage(
				Messages.METHOD_CALL_ERROR_4a, new Object[] { methodName,
						context.getComponent().getId(),
						e.getCause().getMessage() }));
		MethodCallElement._log.error(logMessage, e);
		throw new FacesException(excMessage, e);
	}

	void handleMethodNotFoundException(TemplateContext context)
			throws MethodNotFoundException {
		String logMessage = Messages.getMessage(Messages.getMessage(
				Messages.METHOD_CALL_ERROR_5a, methodName, context
						.getComponent().getId()));
		String excMessage = Messages.getMessage(Messages.getMessage(
				Messages.METHOD_CALL_ERROR_6a, methodName));
		MethodCallElement._log.error(logMessage);
		throw new FacesException(excMessage);
	}
}

class RendererUtilsInvoker extends RendererInvoker {

	public RendererUtilsInvoker(String methodName) {
		super(methodName);
	}

	public Class<? extends Object> getInvokedClass(TemplateContext context) {
		Object object = getInvokedObject(context);
		return (object != null) ? object.getClass() : RendererUtils.class;
	}

	public Object getInvokedObject(TemplateContext context) {
		RendererBase renderer = context.getRenderer();
		return null != renderer ? renderer.getUtils() : null;
	}

	void handleInvocationTargetException(TemplateContext context,
			InvocationTargetException e) {
		String logMessage = Messages.getMessage(Messages.METHOD_CALL_ERROR_1,
				methodName, context.getComponent().getId());
		String excMessage = Messages.getMessage(Messages.METHOD_CALL_ERROR_2,
				new Object[] { methodName, context.getComponent().getId(),
						e.getCause().getMessage() });
		MethodCallElement._log.error(logMessage, e);
		throw new FacesException(excMessage, e);
	}

	void handleIllegalAccessException(TemplateContext context,
			IllegalAccessException e) {
		String logMessage = Messages.getMessage(Messages.getMessage(
				Messages.METHOD_CALL_ERROR_3, methodName, context
						.getComponent().getId()));
		String excMessage = Messages.getMessage(Messages.getMessage(
				Messages.METHOD_CALL_ERROR_4, new Object[] { methodName,
						context.getComponent().getId(),
						e.getCause().getMessage() }));
		MethodCallElement._log.error(logMessage, e);
		throw new FacesException(excMessage, e);
	}

	void handleMethodNotFoundException(TemplateContext context)
			throws MethodNotFoundException {
		String logMessage = Messages.getMessage(Messages.getMessage(
				Messages.METHOD_CALL_ERROR_5, methodName, context
						.getComponent().getId()));
		String excMessage = Messages.getMessage(Messages.getMessage(
				Messages.METHOD_CALL_ERROR_6, methodName));
		MethodCallElement._log.error(logMessage);
		throw new FacesException(excMessage);
	}
}

class StaticInvoker extends Invoker {
	String className;
	Class<?> cls;
	Map<String, Method> methods = new HashMap<String, Method>();

	StaticInvoker(String methodName) {
		super(methodName);
		int i = methodName.lastIndexOf('.');
		className = methodName.substring(0, i);
		this.methodName = methodName.substring(i + 1);
		try {
			cls = Thread.currentThread().getContextClassLoader().loadClass(
					className);
		} catch (ClassNotFoundException e) {
			// ignore, throw exception when invoking
		}
	}

	@Override
	public Class<?> getInvokedClass(TemplateContext context) {
		return cls;
	}

	@Override
	public Object getInvokedObject(TemplateContext context) {
		return null;
	}

	void handleInvocationTargetException(TemplateContext context,
			InvocationTargetException e) {
		MethodCallElement._log.error(Messages.getMessage(
				Messages.METHOD_CALL_ERROR_1a, methodName, context
						.getComponent().getId()), e);
		throw new FacesException(Messages.getMessage(
				Messages.METHOD_CALL_ERROR_2a, new Object[] { methodName,
						context.getComponent().getId(),
						e.getCause().getMessage() }), e);
	}

	void handleIllegalAccessException(TemplateContext context,
			IllegalAccessException e) {
		MethodCallElement._log.error(Messages.getMessage(
				Messages.METHOD_CALL_ERROR_3a, methodName, context
						.getComponent().getId()), e);
		throw new FacesException(Messages.getMessage(
				Messages.METHOD_CALL_ERROR_4a, new Object[] { methodName,
						context.getComponent().getId(),
						e.getCause().getMessage() }), e);
	}

	void handleMethodNotFoundException(TemplateContext context)
			throws MethodNotFoundException {
		MethodCallElement._log.error(Messages.getMessage(
				Messages.METHOD_CALL_ERROR_5a, methodName, context
						.getComponent().getId()));
		throw new MethodNotFoundException(Messages.getMessage(
				Messages.METHOD_CALL_ERROR_6a, methodName));
	}

}

abstract class Signature {

	Method method;

	Signature(Method method) {
		this.method = method;
	}

	boolean isApplicable(TemplateContext context, Class targetClass,
			Class<?>[] parameters) {
		if (!method.getDeclaringClass().isAssignableFrom(targetClass)) {
			return false;
		}
		Class<?>[] methodParameterTypes = this.method.getParameterTypes();
		Class<?>[] parameterTypes = getParameterTypes(context, parameters);
		if (methodParameterTypes.length != parameterTypes.length) {
			return false;
		}
		for (int i = 0; i < parameterTypes.length; i++) {
			if (null != parameterTypes[i]
					&& !methodParameterTypes[i]
							.isAssignableFrom(parameterTypes[i])) {
				return false;
			}
		}
		return true;
	}

	abstract Class<?>[] getParameterTypes(TemplateContext context,
			Class<?>[] parameters);

	abstract Object[] getParameterValues(TemplateContext context,
			Object[] parameters);

	Object invoke(TemplateContext context, Object target, Object[] parameters)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		return this.method.invoke(target, getParameterValues(context,
				parameters));
	}

}

class Signature0 extends Signature {

	Signature0(Method method) {
		super(method);
	}

	@Override
	Class<?>[] getParameterTypes(TemplateContext context, Class<?>[] parameters) {
		return parameters;
	}

	@Override
	Object[] getParameterValues(TemplateContext context, Object[] parameters) {
		return parameters;
	}

}

class Signature1 extends Signature {
	Signature1(Method method) {
		super(method);
	}

	@Override
	Class<?>[] getParameterTypes(TemplateContext context, Class<?>[] parameters) {
		Class<?>[] types = new Class[parameters.length + 1];
		types[0] = TemplateContext.class;
		System.arraycopy(parameters, 0, types, 1, parameters.length);
		return types;
	}

	@Override
	Object[] getParameterValues(TemplateContext context, Object[] parameters) {
		Object[] values = new Object[parameters.length + 1];
		values[0] = context;
		System.arraycopy(parameters, 0, values, 1, parameters.length);
		return values;
	}

}

class Signature2 extends Signature {
	Signature2(Method method) {
		super(method);
	}

	@Override
	Class<?>[] getParameterTypes(TemplateContext context, Class<?>[] parameters) {
		Class<?>[] types = new Class[parameters.length + 2];
		types[0] = FacesContext.class;
		types[1] = context.getComponent().getClass();
		System.arraycopy(parameters, 0, types, 2, parameters.length);
		return types;
	}

	@Override
	Object[] getParameterValues(TemplateContext context, Object[] parameters) {
		Object[] values = new Object[parameters.length + 2];
		values[0] = context.getFacesContext();
		values[1] = context.getComponent();
		System.arraycopy(parameters, 0, values, 2, parameters.length);
		return values;
	}
}

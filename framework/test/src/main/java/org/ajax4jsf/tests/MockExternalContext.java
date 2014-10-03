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

package org.ajax4jsf.tests;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.faces.FacesException;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.EnumerationUtils;
import org.apache.shale.test.mock.MockExternalContext12;

/**
 * @author Siarhej Chalipau
 *
 */
public class MockExternalContext extends MockExternalContext12 {
	/*
	 * Realizes methods unimplemented by org.apache.shale.test.mock.MockExternalContext operations.
	 *  
	 */
	
	private Map requestParameterValuesMap = null;
	private Map requestHeaderMap = null;
	private Map requestHeaderValuesMap = null;
	private Iterator requestParameterNamesIterator = null;
	private Set resourcePathsSet = null;
	
	private Map requestMap;
	
	private Map sessionMap;
	
	private Map applicationMap;
	
	public MockExternalContext(org.apache.shale.test.mock.MockExternalContext baseContext) {
		super((ServletContext)baseContext.getContext(), 
				(HttpServletRequest)baseContext.getRequest(), (HttpServletResponse)baseContext.getResponse());
	}
	
	public Map getRequestParameterValuesMap() {
		if (null == requestParameterValuesMap) {
			requestParameterValuesMap = new HashMap();
			HttpServletRequest request = (HttpServletRequest)getRequest();
			for ( Enumeration e = request.getParameterNames(); e.hasMoreElements(); ) {
				String name = (String) e.nextElement();
				requestParameterValuesMap.put(name, request.getParameterValues(name));
			}
		}
		return Collections.unmodifiableMap(requestParameterValuesMap);
	}
	
	public void dispatch(String requestURI) 
		throws IOException, FacesException {
		//TODO hans, should be implemented
		super.dispatch(requestURI);
	}
	
	 public Map getRequestHeaderMap() {
		 if (null == requestHeaderMap) {
			 requestHeaderMap = new TreeMap(CASE_INSENSITIVE_COMPARATOR);
			 
			 HttpServletRequest request = (HttpServletRequest)getRequest();
			 for ( Enumeration e = request.getHeaderNames(); e.hasMoreElements(); ) {
				 String name = (String) e.nextElement();
				 requestHeaderMap.put(name, request.getHeader(name));
			 }
		 }
		 return Collections.unmodifiableMap(requestHeaderMap);
	 }
	 
	 public Map getRequestHeaderValuesMap() {
		 if (null == requestHeaderValuesMap) {
			 requestHeaderValuesMap = new TreeMap(CASE_INSENSITIVE_COMPARATOR);
			 
			 HttpServletRequest request = (HttpServletRequest)getRequest();
			 for ( Enumeration e = request.getHeaderNames(); e.hasMoreElements(); ) {
				 String name = (String) e.nextElement();
				 requestHeaderValuesMap.put(name, EnumerationUtils.toList(request.getHeaders(name)).toArray());
			 }
		 }
		 return Collections.unmodifiableMap(requestHeaderValuesMap);
	 }
	 
	 public Iterator getRequestParameterNames() {
		 if (null == requestParameterNamesIterator) {
			 requestParameterNamesIterator = getRequestParameterValuesMap().keySet().iterator();
		 }
		 return requestParameterNamesIterator;
	 }
	 
	 /**
	 * <p>Add the specified request parameter for this request.</p>
	 *
	 * @param key Parameter name
	 * @param value Parameter value
	 */
	public void addRequestParameterMap(String key, String value) {
		super.addRequestParameterMap(key, value);
		
		String [] currentValue = (String[]) getRequestParameterValuesMap().get(key);
		if (null == currentValue) {
			requestParameterValuesMap.put(key, new String[] { value });
		} else {
			String [] newArray = new String [currentValue.length + 1];
			System.arraycopy(currentValue, 0, newArray, 0, currentValue.length);
			newArray[currentValue.length] = value;
			requestParameterValuesMap.put(key, newArray);
		}
	}
	 
	 public Set getResourcePaths(String path) {
		 // TODO hans, should be implemented
		 if (null == resourcePathsSet) {
			 resourcePathsSet = new HashSet();			 
		 }
		 return resourcePathsSet;
	 }
	 
	 public void redirect(String requestURI) throws IOException {
		 // TODO hans, should be implemented
		 super.redirect(requestURI);
	 }
	 
	 protected final static Comparator CASE_INSENSITIVE_COMPARATOR = new Comparator() {
		 public int compare(Object arg0, Object arg1) {
			String s0 =  ( String ) arg0; 
		    String s1 =  ( String ) arg1; 
		    return s0.toUpperCase().compareTo(s1.toUpperCase()); 
		}
	 };
	 
	 //JSF 2.0 methods 
	 
//	 public javax.faces.context.Flash getFlash() {
//		 
//	 };
	 
	 public void addResponseCookie(String name, String value, java.util.Map<String,Object> properties) {
		 Cookie cookie = new Cookie(name, value);
		 if (properties != null) {
			 String comment = (String) properties.get("comment");
			 if (comment != null) {
				 cookie.setComment(comment);
			 }
			 String domain = (String) properties.get("domain");
			 if (domain != null) {
				 cookie.setDomain(domain);
			 }
			 String path = (String) properties.get("path");
			 if (path != null) {
				 cookie.setPath(path);
			 }
			 Integer maxAge = (Integer) properties.get("maxAge");
			 if (maxAge != null) {
				 cookie.setMaxAge(maxAge);
			 }
			 Boolean secure = (Boolean) properties.get("secure");
			 if (secure != null) {
				 cookie.setSecure(secure);
			 }
		 }
		 
		 ((HttpServletResponse) getResponse()).addCookie(cookie);
	 };
	 
	 public String encodePartialActionURL(String url) {
		 return encodeActionURL(url);
	 };

	 public String getMimeType(String file) {
		 return "text/html";
	 }
	 
	public String getContextName() {
		return ((ServletContext) this.getContext()).getServletContextName();
	}
	
	public String getRequestScheme() {
		return ((HttpServletRequest) this.getRequest()).getScheme();
	}
	
	public String getRequestServerName() {
		return ((HttpServletRequest) this.getRequest()).getServerName();
	}
	
	public int getRequestServerPort() {
		return ((HttpServletRequest) this.getRequest()).getServerPort();
	}
	
	public String getRealPath(String path) {
		return ((ServletContext) this.getContext()).getRealPath(path);
	}

	public int getRequestContentLength() {
		return ((HttpServletRequest) this.getRequest()).getContentLength();
	}
	
	public OutputStream getResponseOutputStream() throws IOException {
		return ((HttpServletResponse) this.getResponse()).getOutputStream();
	}
	
	public Writer getResponseOutputWriter() throws IOException {
		return ((HttpServletResponse) this.getResponse()).getWriter();
	}
	
	public void setResponseContentType(String contentType) {
		((HttpServletResponse) this.getResponse()).setContentType(contentType);
	}
	
	public void invalidateSession() {
		Object session = getSession(false);
		if (session != null) {
			((HttpSession) session).invalidate();
		}
	}

	public void setResponseHeader(String name, String value) {
		((HttpServletResponse) this.getResponse()).setHeader(name, value);
	}
	
	public void addResponseHeader(String name, String value) {
		((HttpServletResponse) this.getResponse()).addHeader(name, value);
	}
	
	public void setResponseBufferSize(int size) {
		((HttpServletResponse) this.getResponse()).setBufferSize(size);
	}
	
	public int getResponseBufferSize() {
		return ((HttpServletResponse) this.getResponse()).getBufferSize();
	}
	
	public boolean isResponseCommitted() {
		return ((HttpServletResponse) this.getResponse()).isCommitted();
	}
	
	public void responseReset() {
		((HttpServletResponse) this.getResponse()).reset();
	}
	
	public void responseSendError(int statusCode, String message)
			throws IOException {
		((HttpServletResponse) this.getResponse()).sendError(statusCode, message);
	}
	
	public void setResponseStatus(int statusCode) {
		((HttpServletResponse) this.getResponse()).setStatus(statusCode);
	}
	
	public void responseFlushBuffer() throws IOException {
		((HttpServletResponse) this.getResponse()).flushBuffer();
	}
	
	public void setResponseContentLength(int length) {
		((HttpServletResponse) this.getResponse()).setContentLength(length);
	}
	
	private String encodeUrl(String baseUrl,
			Map<String, List<String>> parameters) {
		
		StringBuilder sb = new StringBuilder(baseUrl);
		sb.append('?');
		
		if (parameters != null) {
			for (Map.Entry<String, List<String>> entry: parameters.entrySet()) {
				List<String> values = entry.getValue();
				if (values != null) {
					String name = entry.getKey();
					for (String value: values) {
						sb.append(name);
						sb.append('=');
						sb.append(value);
						sb.append('&');
					}
				}
			}
		}
		
		return sb.toString();
	}
	
	public String encodeBookmarkableURL(String baseUrl,
			Map<String, List<String>> parameters) {
		
		return encodeUrl(baseUrl, parameters);
	}
	
	public String encodeRedirectURL(String baseUrl,
			Map<String, List<String>> parameters) {

		return encodeUrl(baseUrl, parameters);
	}
	
	public Map getRequestMap() {
		if (requestMap == null) {
			requestMap = new MockContextMap(super.getRequestMap());
		}
		return requestMap;
	}
	
	public Map getSessionMap() {
		if (sessionMap == null) {
			sessionMap = new MockContextMap(super.getSessionMap());
		}
		return sessionMap;
	}
	
	public Map getApplicationMap() {
		if (applicationMap == null) {
			applicationMap = new MockContextMap(super.getApplicationMap());
		}
		return applicationMap;
	}
}

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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Nick Belaevski
 * Fixes incorrectly implemented in Shale Mock Map#entrySet() method
 */
class MockContextMap implements Map {

	private static final class MapEntry implements Map.Entry {

		private Object key;
		
		private Map originalMap;
		
		public MapEntry(Object key, Map originalMap) {
			super();
			this.key = key;
			this.originalMap = originalMap;
		}

		public Object getKey() {
			return key;
		}

		public Object getValue() {
			return originalMap.get(key);
		}

		public Object setValue(Object value) {
			return originalMap.put(key, value);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result
					+ ((originalMap == null) ? 0 : originalMap.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			MapEntry other = (MapEntry) obj;
			if (key == null) {
				if (other.key != null) {
					return false;
				}
			} else if (!key.equals(other.key)) {
				return false;
			}
			if (originalMap == null) {
				if (other.originalMap != null) {
					return false;
				}
			} else if (!originalMap.equals(other.originalMap)) {
				return false;
			}
			return true;
		}
		
	}
	
	private Map shaleMockMap;

	public MockContextMap(Map shaleMockMap) {
		super();
		this.shaleMockMap = shaleMockMap;
	}

	public void clear() {
		shaleMockMap.clear();
	}

	public boolean containsKey(Object key) {
		return shaleMockMap.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return shaleMockMap.containsValue(value);
	}

	public Set entrySet() {
		Set keySet = shaleMockMap.keySet();
		
		Set entrySet = null;
		if (keySet != null) {
			entrySet = new HashSet();
		
			for (Object key : keySet) {
				entrySet.add(new MapEntry(key, shaleMockMap));
			}
		}
		
		return entrySet;
	}

	public boolean equals(Object o) {
		return shaleMockMap.equals(o);
	}

	public Object get(Object key) {
		return shaleMockMap.get(key);
	}

	public int hashCode() {
		return shaleMockMap.hashCode();
	}

	public boolean isEmpty() {
		return shaleMockMap.isEmpty();
	}

	public Set keySet() {
		return shaleMockMap.keySet();
	}

	public Object put(Object key, Object value) {
		return shaleMockMap.put(key, value);
	}

	public void putAll(Map m) {
		shaleMockMap.putAll(m);
	}

	public Object remove(Object key) {
		return shaleMockMap.remove(key);
	}

	public int size() {
		return shaleMockMap.size();
	}

	public Collection values() {
		return shaleMockMap.values();
	}
	
}

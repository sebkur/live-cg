/* This file is part of LiveCG. 
 * 
 * Copyright (C) 2013  Sebastian Kuerten
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.topobyte.util.resources;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarDict
{

	private class PathElement
	{
		private JarEntry entry = null;
		private Map<String, PathElement> entries = new TreeMap<String, PathElement>(
				new FileNameComparator());
	}

	private PathElement entry = new PathElement();

	public JarDict(JarFile jar)
	{
		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String name = entry.getName();
			String[] parts = name.split("/");
			PathElement e = this.entry;
			for (String part : parts) {
				PathElement next = e.entries.get(part);
				if (next == null) {
					next = new PathElement();
					e.entries.put(part, next);
				}
				e = next;
			}
			e.entry = entry;
		}
	}

	public JarEntry get(String relativePath)
	{
		String[] parts = relativePath.split("/");
		PathElement e = this.entry;
		for (String part : parts) {
			e = e.entries.get(part);
		}
		if (e == null) {
			return null;
		}
		return e.entry;
	}

	public Set<String> getFiles(String relativePath)
	{
		String[] parts = relativePath.split("/");
		PathElement e = this.entry;
		for (String part : parts) {
			e = e.entries.get(part);
		}
		Set<String> entries = e.entries.keySet();
		return entries;
	}

}

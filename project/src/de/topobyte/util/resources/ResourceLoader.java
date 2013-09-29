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

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ResourceLoader
{

	public static ResourceFile open(String path) throws IOException
	{
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		URL url = classLoader.getResource(path);
		if (url == null) {
			return null;
		}
		if (url.getProtocol().equals("file")) {
			return new FileFile(new File(url.getFile()));
		} else if (url.getProtocol().equals("jar")) {
			return new JarFileFile(url);
		}
		return null;
	}
}

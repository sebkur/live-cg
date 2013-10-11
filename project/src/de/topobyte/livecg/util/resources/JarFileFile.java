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
package de.topobyte.livecg.util.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarFileFile implements ResourceFile
{

	private String relativePath;
	private String name;

	private JarFile jar;
	private JarDict jarDict;
	private JarEntry entry;

	public JarFileFile(URL url) throws IOException
	{
		String path = url.getPath();
		int start = path.indexOf("!/");
		relativePath = path.substring(start + 2);

		setNameFromPath();

		JarURLConnection urlcon = (JarURLConnection) (url.openConnection());
		jar = urlcon.getJarFile();
		jarDict = new JarDict(jar);

		System.out.println("jardict.get: " + relativePath);
		entry = jarDict.get(relativePath);
		System.out.println(entry);
	}

	public JarFileFile(JarFile jar, JarDict jarDict, String relativePath)
	{
		this.jar = jar;
		this.jarDict = jarDict;
		this.relativePath = relativePath;

		setNameFromPath();

		entry = jarDict.get(relativePath);
	}

	private void setNameFromPath()
	{
		int lastSlash = relativePath.lastIndexOf('/');
		if (lastSlash < 0) {
			name = relativePath;
		} else {
			name = relativePath.substring(lastSlash + 1);
		}

	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public boolean isDirectory()
	{
		return entry.isDirectory();
	}

	@Override
	public List<ResourceFile> listFiles()
	{
		List<ResourceFile> files = new ArrayList<ResourceFile>();
		Set<String> sub = jarDict.getFiles(relativePath);
		for (String s : sub) {
			String path = relativePath + "/" + s;
			files.add(new JarFileFile(jar, jarDict, path));
		}
		return files;
	}

	@Override
	public InputStream open() throws IOException
	{
		return jar.getInputStream(entry);
	}

	@Override
	public String toString()
	{
		return relativePath;
	}

}

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileFile implements ResourceFile
{

	private File file;

	private static FileNameComparator nameComparator = new FileNameComparator();

	private static Comparator<ResourceFile> comparator = new Comparator<ResourceFile>() {

		@Override
		public int compare(ResourceFile o1, ResourceFile o2)
		{
			return nameComparator.compare(o1.getName(), o2.getName());
		}
	};

	public FileFile(File file)
	{
		this.file = file;
	}

	@Override
	public String getName()
	{
		return file.getName();
	}

	@Override
	public boolean isDirectory()
	{
		return file.isDirectory();
	}

	@Override
	public List<ResourceFile> listFiles()
	{
		File[] files = file.listFiles();
		if (files == null) {
			return null;
		}
		List<ResourceFile> results = new ArrayList<ResourceFile>();
		for (File file : files) {
			results.add(new FileFile(file));
		}
		Collections.sort(results, comparator);
		return results;
	}

	@Override
	public InputStream open() throws FileNotFoundException
	{
		return new FileInputStream(file);
	}

	@Override
	public String toString()
	{
		return file.toString();
	}

}

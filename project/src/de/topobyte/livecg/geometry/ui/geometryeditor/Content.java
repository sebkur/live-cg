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

package de.topobyte.livecg.geometry.ui.geometryeditor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.topobyte.livecg.geometry.ui.geom.Coordinate;
import de.topobyte.livecg.geometry.ui.geom.Editable;

public class Content
{

	private Editable currentEditable = null;

	private List<Editable> editables = new ArrayList<Editable>();

	public Editable getEditingLine()
	{
		return currentEditable;
	}

	public void setEditingLine(Editable editable)
	{
		this.currentEditable = editable;
	}

	public List<Editable> getLines()
	{
		return editables;
	}

	public void addLine(Editable line)
	{
		editables.add(0, line);
	}

	public void removeLine(Editable line)
	{
		editables.remove(line);
	}

	public Set<Editable> getEditablesNear(Coordinate coordinate)
	{
		Set<Editable> results = new HashSet<Editable>();
		if (currentEditable != null) {
			if (currentEditable.hasPointWithinThreshold(coordinate, 4)) {
				results.add(currentEditable);
			}
		}
		for (Editable line : editables) {
			if (line.hasPointWithinThreshold(coordinate, 4)) {
				results.add(line);
			}
		}
		return results;
	}

	public void changeEditingLine(Editable editable)
	{
		if (currentEditable == editable) {
			return;
		}
		boolean found = editables.remove(editable);
		if (!found) {
			return;
		}
		if (currentEditable != null) {
			editables.add(currentEditable);
		}
		currentEditable = editable;
	}

	private List<ContentChangedListener> contentListenerns = new ArrayList<ContentChangedListener>();

	public void addContentChangedListener(ContentChangedListener l)
	{
		contentListenerns.add(l);
	}

	public void removeContentChangedListener(ContentChangedListener l)
	{
		contentListenerns.remove(l);
	}

	public void fireContentChanged()
	{
		for (ContentChangedListener l : contentListenerns) {
			l.contentChanged();
		}
	}
}

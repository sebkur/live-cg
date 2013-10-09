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
package de.topobyte.livecg.ui.geometryeditor.debug;

import java.util.List;

import javax.swing.AbstractListModel;

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.Polygon;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;

public class MultipleObjectsListModel extends AbstractListModel
{

	private static final long serialVersionUID = 4450493472776466165L;

	private GeometryEditPane editPane;

	public MultipleObjectsListModel(GeometryEditPane editPane)
	{
		this.editPane = editPane;
	}

	@Override
	public int getSize()
	{
		Content content = editPane.getContent();
		List<Chain> chains = content.getChains();
		List<Polygon> polygons = content.getPolygons();
		return chains.size() + polygons.size();
	}

	@Override
	public Object getElementAt(int index)
	{
		Content content = editPane.getContent();
		List<Chain> chains = content.getChains();
		List<Polygon> polygons = content.getPolygons();
		if (index < chains.size()) {
			Chain chain = chains.get(index);
			return String.format("chain (%d nodes)", chain.getNumberOfNodes());
		} else if (index < chains.size() + polygons.size()) {
			Polygon polygon = polygons.get(index - chains.size());
			return String.format("polygon (%d nodes)", polygon.getShell()
					.getNumberOfNodes());
		}
		return "what";
	}

	public void update()
	{
		fireContentsChanged(this, 0, getSize() - 1);
	}

}

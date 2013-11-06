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
package de.topobyte.livecg.algorithms.polygon.monotonepieces;

import java.util.List;

import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.util.graph.Graph;

public class SplitResult
{

	private List<Polygon> polygons;
	private Graph<Polygon, Diagonal> graph;

	public SplitResult(List<Polygon> polygons, Graph<Polygon, Diagonal> graph)
	{
		this.polygons = polygons;
		this.graph = graph;
	}

	public List<Polygon> getPolygons()
	{
		return polygons;
	}

	public Graph<Polygon, Diagonal> getGraph()
	{
		return graph;
	}
}

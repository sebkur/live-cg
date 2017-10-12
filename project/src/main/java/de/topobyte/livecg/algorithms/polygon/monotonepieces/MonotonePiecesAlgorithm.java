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

import de.topobyte.livecg.algorithms.polygon.util.Diagonal;
import de.topobyte.livecg.core.algorithm.DefaultSceneAlgorithm;
import de.topobyte.livecg.core.geometry.geom.BoundingBoxes;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangles;
import de.topobyte.livecg.util.graph.Graph;
import de.topobyte.viewports.geometry.Rectangle;

public class MonotonePiecesAlgorithm extends DefaultSceneAlgorithm
{

	private Polygon polygon;
	private MonotonePiecesOperation monotonePiecesOperation;
	private List<Polygon> monotonePieces;
	private Graph<Polygon, Diagonal> graph;
	private Graph<Polygon, Object> extendedGraph;

	public MonotonePiecesAlgorithm(Polygon polygon)
	{
		this.polygon = polygon;
		execute();
	}

	public Polygon getPolygon()
	{
		return polygon;
	}

	@Override
	public Rectangle getScene()
	{
		Rectangle bbox = BoundingBoxes.get(polygon);
		Rectangle scene = Rectangles.extend(bbox, 15);
		return scene;
	}

	protected void execute()
	{
		System.out.println("execute: " + this.getClass().getSimpleName());
		monotonePiecesOperation = new MonotonePiecesOperation(polygon);
		SplitResult split = monotonePiecesOperation
				.getMonotonePiecesWithGraph();
		monotonePieces = split.getPolygons();
		graph = split.getGraph();
		extendedGraph = PolygonGraphUtil.addNodeEdges(graph);
	}

	public MonotonePiecesOperation getMonotonePiecesOperation()
	{
		return monotonePiecesOperation;
	}

	public List<Polygon> getMonotonePieces()
	{
		return monotonePieces;
	}

	public Graph<Polygon, Diagonal> getGraph()
	{
		return graph;
	}

	public Graph<Polygon, Object> getExtendedGraph()
	{
		return extendedGraph;
	}
}

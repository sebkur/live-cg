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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.painting.AwtPainter;
import de.topobyte.livecg.util.coloring.ColorMapBuilder;
import de.topobyte.livecg.util.graph.Graph;

public class MonotonePiecesPanel extends JPanel implements PolygonPanel
{

	private static final long serialVersionUID = 2129465700417909129L;

	private MonotonePiecesOperation monotonePiecesOperation;

	private List<Polygon> monotonePieces;
	private Graph<Polygon, Diagonal> graph;

	private Map<Polygon, Color> colorMap;

	private Config polygonConfig = new Config();

	private AwtPainter painter;
	private MonotonePiecesPainter algorithmPainter;

	public MonotonePiecesPanel(Polygon polygon)
	{
		monotonePiecesOperation = new MonotonePiecesOperation(polygon);
		SplitResult split = monotonePiecesOperation
				.getMonotonePiecesWithGraph();
		monotonePieces = split.getPolygons();
		graph = split.getGraph();

		Graph<Polygon, Object> extendedGraph = PolygonGraphUtil
				.addNodeEdges(graph);

		colorMap = ColorMapBuilder.buildColorMap(extendedGraph);

		painter = new AwtPainter(null);
		algorithmPainter = new MonotonePiecesPainter(painter, polygon,
				monotonePiecesOperation, monotonePieces, polygonConfig,
				colorMap);
	}

	@Override
	public void paint(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		painter.setGraphics(g);
		algorithmPainter.setWidth(getWidth());
		algorithmPainter.setHeight(getHeight());
		algorithmPainter.paint();
	}

	@Override
	public Config getPolygonConfig()
	{
		return polygonConfig;
	}

	@Override
	public void settingsUpdated()
	{
		repaint();
	}
}

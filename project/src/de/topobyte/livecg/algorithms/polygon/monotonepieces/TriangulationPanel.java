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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.JPanel;

import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.painting.AwtPainter;
import de.topobyte.livecg.util.SwingUtil;
import de.topobyte.livecg.util.graph.Graph;

public class TriangulationPanel extends JPanel implements PolygonPanel
{

	private static final long serialVersionUID = 1265869392513220699L;

	private TriangulationOperation triangulationOperation;
	private List<Diagonal> diagonals;

	private Config polygonConfig = new Config();

	private AwtPainter painter;
	private TriangulationPainter algorithmPainter;

	public TriangulationPanel(Polygon polygon)
	{
		triangulationOperation = new TriangulationOperation(polygon);
		diagonals = triangulationOperation.getDiagonals();

		SplitResult splitResult = DiagonalUtil.split(polygon, diagonals);
		Graph<Polygon, Diagonal> graph = splitResult.getGraph();

		painter = new AwtPainter(null);
		algorithmPainter = new TriangulationPainter(polygon, diagonals, graph,
				polygonConfig, painter);
	}

	@Override
	public void paint(Graphics graphics)
	{
		Graphics2D g = (Graphics2D) graphics;
		SwingUtil.useAntialiasing(g, true);

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

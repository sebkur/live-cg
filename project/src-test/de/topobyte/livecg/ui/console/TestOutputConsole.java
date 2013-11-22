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
package de.topobyte.livecg.ui.console;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import de.topobyte.livecg.algorithms.polygon.shortestpath.PairOfNodes;
import de.topobyte.livecg.algorithms.polygon.shortestpath.ShortestPathAlgorithm;
import de.topobyte.livecg.algorithms.polygon.shortestpath.ShortestPathHelper;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.io.ContentReader;
import de.topobyte.livecg.ui.geometryeditor.Content;

public class TestOutputConsole
{
	public static void main(String[] args) throws IOException,
			ParserConfigurationException, SAXException
	{
		/*
		 * An Algorithm
		 */
		String path = "res/presets/polygons/Big.geom";
		ContentReader contentReader = new ContentReader();
		Content content = contentReader.read(new File(path));
		Polygon polygon = content.getPolygons().get(0);

		PairOfNodes nodes = ShortestPathHelper.determineGoodNodes(polygon);
		Node start = nodes.getA();
		Node target = nodes.getB();
		final ShortestPathAlgorithm algorithm = new ShortestPathAlgorithm(
				polygon, start, target);

		/*
		 * Console
		 */
		AlgorithmOutputConsole console = new AlgorithmOutputConsole(algorithm);

		/*
		 * UI
		 */

		JPanel main = new JPanel(new BorderLayout());
		JToolBar tools = new JToolBar();

		main.add(tools, BorderLayout.NORTH);
		main.add(console, BorderLayout.CENTER);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setContentPane(main);

		frame.setSize(500, 400);
		frame.setVisible(true);

		JButton next = new JButton("next");
		JButton prev = new JButton("prev");

		tools.add(prev);
		tools.add(next);

		next.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				int status = algorithm.getStatus();
				algorithm.setStatus(status + 1);
			}
		});

		prev.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				int status = algorithm.getStatus();
				algorithm.setStatus(status - 1);
			}
		});
	}
}

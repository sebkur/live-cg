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
package de.topobyte.livecg.algorithms.farthestpair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.topobyte.livecg.algorithms.convexhull.ConvexHullOperation;
import de.topobyte.livecg.algorithms.farthestpair.FarthestPairResult;
import de.topobyte.livecg.algorithms.farthestpair.NaiveFarthestPairOperation;
import de.topobyte.livecg.algorithms.farthestpair.ShamosFarthestPairOperation;
import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Coordinate;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;

public class TestFarthestPairWithRandomPoints
{
	public static void main(String[] args) throws IOException,
			ParserConfigurationException, SAXException
	{
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);

		Random random = new Random(0);

		for (int i = 0; i < 10000; i++) {
			List<Node> nodes = new ArrayList<Node>();
			for (int k = 0; k < 50; k++) {
				int x = random.nextInt(400) + 10;
				int y = random.nextInt(400) + 10;
				nodes.add(new Node(new Coordinate(x, y)));
			}
			Polygon p = ConvexHullOperation.compute(nodes, null, null);
			test(p);
		}
	}

	private static void test(Polygon polygon)
	{
		Chain shell = polygon.getShell();
		FarthestPairResult result1 = ShamosFarthestPairOperation.compute(shell);
		FarthestPairResult result2 = NaiveFarthestPairOperation.compute(shell);
		if (result1.getI() != result2.getI()
				|| result1.getJ() != result2.getJ()) {
			System.out.println("Shamos: " + result1.getI() + ", "
					+ result1.getJ() + ", " + result1.getDistance());
			System.out.println("Naive:  " + result2.getI() + ", "
					+ result2.getJ() + ", " + result2.getDistance());
		}
	}
}

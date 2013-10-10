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
package de.topobyte.farthestpair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.topobyte.convexhull.ConvexHullOperation;
import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.Coordinate;
import de.topobyte.livecg.geometry.geom.Node;
import de.topobyte.livecg.geometry.geom.Polygon;

public class TestFarthestPairWithRandomPoints
{
	public static void main(String[] args) throws IOException,
			ParserConfigurationException, SAXException
	{
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.DEBUG);

		Random random = new Random(0);

		for (int i = 0; i < 10; i++) {
			List<Node> nodes = new ArrayList<Node>();
			for (int k = 0; k < 20; k++) {
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
		FarthestPairResult result = ShamosFarthestPairOperation.compute(shell);
		System.out.println(result.getI() + ", " + result.getJ() + ", "
				+ result.getDistance());
	}
}

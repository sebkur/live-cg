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
package de.topobyte.livecg.algorithms.convexhull.chan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.topobyte.livecg.core.geometry.geom.Polygon;

public class ChanUtil
{

	private int total = 0;
	private int major = 0;
	private Map<Integer, Integer> steps = new HashMap<>();

	public ChanUtil(List<Polygon> polygons)
	{
		ChansAlgorithm algorithm = new ChansAlgorithm(polygons);
		int lastHullSize = -1;
		while (!algorithm.isFinished()) {
			int hullSize = algorithm.getNumberOfNodesOnHull();
			if (hullSize > lastHullSize) {
				lastHullSize = hullSize;
				steps.put(hullSize, 1);
			} else {
				steps.put(hullSize, steps.get(hullSize) + 1);
			}
			algorithm.nextStep();
			total++;
		}
		major = lastHullSize;
	}

	public int getTotalNumberOfSteps()
	{
		return total;
	}

	public int getNumberOfMajorSteps()
	{
		return major;
	}

	public int getNumberOfMinorSteps(int major)
	{
		return steps.get(major);
	}
}

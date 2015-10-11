/* This file is part of LiveCG.
 *
 * Copyright (C) 2014  Sebastian Kuerten
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
package de.topobyte.livecg.algorithms.polygon.monotone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.GeomMath;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;

public class Util
{

	static final Logger logger = LoggerFactory.getLogger(Util.class);

	/**
	 * Check whether it is possible to add a diagonal from node 'from' to node
	 * 'to'. It is possible if the diagonal stays within the interior of the
	 * polygon and this can be checked by taking into account node 'check' which
	 * is in between the other two nodes on the same chain. Depending on the
	 * side of the chain (left or right) a different test has to be applied.
	 */
	static boolean canAdd(Side side, Node from, Node to, Node check)
	{
		if (side == Side.RIGHT) {
			return GeomMath.isRightOf(from.getCoordinate(), to.getCoordinate(),
					check.getCoordinate());
		} else {
			return GeomMath.isLeftOf(from.getCoordinate(), to.getCoordinate(),
					check.getCoordinate());
		}
	}

	static int findIndex(Polygon polygon, Node n)
	{
		Chain shell = polygon.getShell();
		for (int i = 0; i < shell.getNumberOfNodes(); i++) {
			if (n == shell.getNode(i)) {
				return i;
			}
		}
		return -1;
	}
}

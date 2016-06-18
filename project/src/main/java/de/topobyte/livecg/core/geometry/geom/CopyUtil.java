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
package de.topobyte.livecg.core.geometry.geom;

import java.util.ArrayList;
import java.util.List;

public class CopyUtil
{

	public enum PolygonMode {

		REUSE_CHAINS,
		REUSE_NODES,
		REUSE_NOTHING

	}

	public enum ChainMode {

		REUSE_NODES,
		REUSE_NOTHING

	}

	public static Polygon copy(Polygon polygon, PolygonMode mode)
	{
		switch (mode) {
		default:
		case REUSE_CHAINS:
			Chain shell = polygon.getShell();
			List<Chain> holes = polygon.getHoles();
			List<Chain> holesCopy = new ArrayList<>();
			for (Chain hole : holes) {
				holesCopy.add(hole);
			}
			return new Polygon(shell, holesCopy);
		case REUSE_NODES:
			return copyPolygonDeep(polygon, ChainMode.REUSE_NODES);
		case REUSE_NOTHING:
			return copyPolygonDeep(polygon, ChainMode.REUSE_NOTHING);
		}
	}

	private static Polygon copyPolygonDeep(Polygon polygon, ChainMode mode)
	{
		Chain shell = copy(polygon.getShell(), mode);
		List<Chain> holes = new ArrayList<>();
		for (Chain hole : holes) {
			holes.add(copy(hole, mode));
		}
		return new Polygon(shell, holes);
	}

	public static Chain copy(Chain chain, ChainMode mode)
	{
		Chain c;
		switch (mode) {
		default:
		case REUSE_NODES:
			c = new Chain();
			for (int i = 0; i < chain.getNumberOfNodes(); i++) {
				c.appendNode(chain.getNode(i));
			}
			break;
		case REUSE_NOTHING:
			c = new Chain();
			for (int i = 0; i < chain.getNumberOfNodes(); i++) {
				Node node = chain.getNode(i);
				Node n = new Node(new Coordinate(node.getCoordinate()));
				c.appendNode(n);
			}
			break;
		}
		try {
			c.setClosed(chain.isClosed());
		} catch (CloseabilityException e) {
			// should not happen
		}
		return c;
	}
}

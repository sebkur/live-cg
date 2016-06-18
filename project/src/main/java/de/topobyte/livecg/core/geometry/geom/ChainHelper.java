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

import java.util.HashMap;
import java.util.Map;

public class ChainHelper
{

	public static Chain invert(Chain chain) throws CloseabilityException
	{
		Chain result = new Chain();
		int n = chain.getNumberOfNodes();
		for (int i = n - 1; i >= 0; i--) {
			result.appendNode(chain.getNode(i));
		}
		result.setClosed(chain.isClosed());
		return result;
	}

	public static Map<Node, Integer> buildNodeIndexLookup(Chain chain)
	{
		Map<Node, Integer> index = new HashMap<>();
		for (int i = 0; i < chain.getNumberOfNodes(); i++) {
			Node node = chain.getNode(i);
			index.put(node, i);
		}
		return index;
	}

}

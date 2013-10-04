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
package de.topobyte.polygon.monotonepieces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.ChainHelper;
import de.topobyte.livecg.geometry.geom.CloseabilityException;
import de.topobyte.livecg.geometry.geom.IntRing;
import de.topobyte.livecg.geometry.geom.IntRingInterval;
import de.topobyte.livecg.geometry.geom.Node;
import de.topobyte.livecg.geometry.geom.Polygon;

public class DiagonalUtil
{
	final static Logger logger = LoggerFactory.getLogger(DiagonalUtil.class);

	public static List<Polygon> split(Polygon polygon,
			Collection<Diagonal> diagonals)
	{
		List<Polygon> pieces = new ArrayList<Polygon>();
		split(pieces, polygon, diagonals);
		return pieces;
	}

	private static void split(List<Polygon> pieces, Polygon polygon,
			Collection<Diagonal> diagonals)
	{
		if (diagonals.isEmpty()) {
			pieces.add(polygon);
			return;
		}

		Chain shell = polygon.getShell();
		logger.debug("Split, polygon size: " + shell.getNumberOfNodes()
				+ ", #diagonals: " + diagonals.size());
		Map<Node, Integer> index = ChainHelper.buildNodeIndexLookup(shell);

		// Print some info about diagonals
		for (Diagonal d : diagonals) {
			int a = index.get(d.getA());
			int b = index.get(d.getB());
			logger.debug(String.format("Available diagonal %d -> %d", a + 1,
					b + 1));
		}

		// Select the first diagonal
		Diagonal diagonal = diagonals.iterator().next();
		int a = index.get(diagonal.getA());
		int b = index.get(diagonal.getB());
		logger.debug(String.format("Selected Diagonal %d -> %d", a + 1, b + 1));

		// Create two chains for both subpolygons
		Chain chainA = createChain(shell, a, b);
		Chain chainB = createChain(shell, b, a);
		logger.debug("Chain A: " + a + " -> " + b);
		logger.debug("Chain B: " + b + " -> " + a);

		// Now assign diagonals to subpolygons
		IntRingInterval intA = new IntRingInterval(shell.getNumberOfNodes(), a,
				b);
		List<Diagonal> diagsA = new ArrayList<Diagonal>();

		IntRingInterval intB = new IntRingInterval(shell.getNumberOfNodes(), b,
				a);
		List<Diagonal> diagsB = new ArrayList<Diagonal>();

		for (Diagonal d : diagonals) {
			if (d == diagonal) {
				continue;
			}
			int da = index.get(d.getA());
			int db = index.get(d.getB());
			if (intA.contains(da, false) && intA.contains(db, false)) {
				diagsA.add(d);
				logger.debug("Diagonal " + (da + 1) + " <-> " + (db + 1)
						+ " -> part A");
			} else if (intB.contains(da, false) && intB.contains(db, false)) {
				diagsB.add(d);
				logger.debug("Diagonal " + (da + 1) + " <-> " + (db + 1)
						+ " -> part B");
			} else {
				// Ignore, should not happen
				logger.error("Diagonal contained in neither part");
			}
		}

		// Recurse with subpolygons and diagonals
		recurse(pieces, chainA, diagsA);
		recurse(pieces, chainB, diagsB);
	}

	private static void recurse(List<Polygon> pieces, Chain chain,
			List<Diagonal> diags)
	{
		Polygon piece = new Polygon(chain, null);
		if (diags.size() == 0) {
			logger.debug("Recursion end. Polygon size: "
					+ chain.getNumberOfNodes());
			pieces.add(piece);
		} else {
			split(pieces, piece, diags);
		}
	}

	private static Chain createChain(Chain shell, int a, int b)
	{
		Chain chain = new Chain();
		IntRing ring = new IntRing(shell.getNumberOfNodes(), a);
		for (; ring.value() != b; ring.next()) {
			// logger.debug("node " + (ring.value() + 1));
			chain.appendNode(shell.getNode(ring.value()));
		}
		// logger.debug("node " + (ring.value() + 1));
		chain.appendNode(shell.getNode(ring.value()));

		try {
			chain.setClosed(true);
		} catch (CloseabilityException e) {
			// Ignore, should not happen
			logger.error("Subchain not closeable");
		}
		return chain;
	}

}

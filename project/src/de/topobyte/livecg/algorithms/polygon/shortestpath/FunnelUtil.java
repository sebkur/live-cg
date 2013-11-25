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
package de.topobyte.livecg.algorithms.polygon.shortestpath;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.algorithms.polygon.monotonepieces.Diagonal;
import de.topobyte.livecg.algorithms.polygon.shortestpath.funnel.Step;
import de.topobyte.livecg.algorithms.polygon.shortestpath.funnel.StepFunnelPathEmpty;
import de.topobyte.livecg.algorithms.polygon.shortestpath.funnel.StepLocateNextNode;
import de.topobyte.livecg.algorithms.polygon.shortestpath.funnel.StepMoveApexToLastNode;
import de.topobyte.livecg.algorithms.polygon.shortestpath.funnel.StepUpdateFunnel;
import de.topobyte.livecg.algorithms.polygon.shortestpath.funnel.StepWalkBackward;
import de.topobyte.livecg.algorithms.polygon.shortestpath.funnel.StepWalkForward;
import de.topobyte.livecg.core.geometry.geom.GeomMath;
import de.topobyte.livecg.core.geometry.geom.Node;

public class FunnelUtil
{
	final static Logger logger = LoggerFactory.getLogger(FunnelUtil.class);

	static boolean turnOk(Node pn1, Node pn2, Node notOnChain, Side side)
	{
		if (side == Side.LEFT) {
			return GeomMath.isLeftOf(pn1.getCoordinate(), pn2.getCoordinate(),
					notOnChain.getCoordinate());
		} else {
			return GeomMath.isRightOf(pn1.getCoordinate(), pn2.getCoordinate(),
					notOnChain.getCoordinate());
		}
	}

	static void updateFunnel(Data data, Node notYetOnChain, Side on)
	{
		if (data.getFunnelLength(on) == 0) {
			logger.debug("case1: path1 has length 1");
			data.append(on, notYetOnChain);
			return;
		}

		logger.debug("case2: walking backward on path1");
		for (int k = data.getFunnelLength(on) - 1; k >= 0; k--) {
			Node pn1 = data.getSafe(on, k - 1);
			Node pn2 = data.get(on, k);
			boolean turnOk = turnOk(pn1, pn2, notYetOnChain, on);
			if (!turnOk) {
				data.removeLast(on);
			} else {
				data.append(on, notYetOnChain);
				return;
			}
		}

		Side other = on.other();

		logger.debug("case3: reached apex");
		if (data.getFunnelLength(other) == 0) {
			data.append(on, notYetOnChain);
			return;
		}
		Node p1 = data.getApex();
		Node p2 = data.get(other, 0);
		if (turnOk(p1, p2, notYetOnChain, on)) {
			data.append(on, notYetOnChain);
			return;
		}

		logger.debug("case4: walking forward on path2");
		for (int k = 0; k < data.getFunnelLength(other) - 1; k++) {
			Node pn1 = data.get(other, k);
			Node pn2 = data.get(other, k + 1);
			boolean turnOk = turnOk(pn1, pn2, notYetOnChain, on);
			if (turnOk) {
				logger.debug("turn is ok with k=" + k);
				Node w = pn1;
				data.append(on, notYetOnChain);
				for (int l = 0; l <= k; l++) {
					data.appendCommon(data.removeFirst(other));
				}
				data.appendCommon(w);
				return;
			}
		}

		logger.debug("case5: moving apex to last node of path2");
		data.append(on, notYetOnChain);
		for (int k = 0; k < data.getFunnelLength(other);) {
			data.appendCommon(data.removeFirst(other));
		}
	}

	static List<Step> stepsToUpdateFunnel(Data data, Diagonal next, Side on,
			Node notYetOnChain)
	{
		List<Step> steps = new ArrayList<Step>();

		steps.add(new StepLocateNextNode());

		if (data.getFunnelLength(on) == 0) {
			steps.add(new StepFunnelPathEmpty());
			return steps;
		}

		int counterBackward = 0;
		for (int k = data.getFunnelLength(on) - 1; k >= 0; k--) {
			counterBackward++;
			Node pn1 = data.getSafe(on, k - 1);
			Node pn2 = data.get(on, k);
			boolean turnOk = FunnelUtil.turnOk(pn1, pn2, notYetOnChain, on);
			if (turnOk) {
				steps.add(new StepWalkBackward(counterBackward));
				steps.add(new StepUpdateFunnel());
				return steps;
			}
		}

		Side other = on.other();

		if (data.getFunnelLength(other) > 0) {
			steps.add(new StepWalkBackward(++counterBackward));
			Node p1 = data.getApex();
			Node p2 = data.get(other, 0);
			if (FunnelUtil.turnOk(p1, p2, notYetOnChain, on)) {
				steps.add(new StepUpdateFunnel());
				return steps;
			}
		}

		int counterForward = 0;
		for (int k = 0; k < data.getFunnelLength(other) - 1; k++) {
			counterForward++;
			Node pn1 = data.get(other, k);
			Node pn2 = data.get(other, k + 1);
			boolean turnOk = FunnelUtil.turnOk(pn1, pn2, notYetOnChain, on);
			if (turnOk) {
				steps.add(new StepWalkForward(counterForward));
				steps.add(new StepUpdateFunnel());
				return steps;
			}
		}
		steps.add(new StepWalkForward(counterForward));

		steps.add(new StepMoveApexToLastNode());
		return steps;
	}

	static Node getNthNodeOfFunnelTraversal(Data data, Diagonal next, Side on,
			int s)
	{
		int lengthOfFirstPath = data.getFunnelLength(on);
		if (s <= lengthOfFirstPath) {
			return data.get(on, lengthOfFirstPath - s);
		}
		if (s == lengthOfFirstPath + 1) {
			return data.getApex();
		}
		return data.get(on.other(), s - lengthOfFirstPath - 2);
	}
}

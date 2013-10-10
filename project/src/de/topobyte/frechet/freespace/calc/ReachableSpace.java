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
package de.topobyte.frechet.freespace.calc;

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.util.DoubleUtil;

public class ReachableSpace
{

	private Interval[][] L;
	private Interval[][] B;

	public ReachableSpace(Chain line1, Chain line2, int epsilon)
	{
		int nSegmentsP = line1.getNumberOfNodes() - 1;
		int nSegmentsQ = line2.getNumberOfNodes() - 1;

		L = new Interval[nSegmentsP + 1][nSegmentsQ + 1];
		B = new Interval[nSegmentsP + 1][nSegmentsQ + 1];

		// Initialize for cell 0,0

		LineSegment seg1 = FrechetUtil.getSegment(line1, 0);
		LineSegment seg2 = FrechetUtil.getSegment(line2, 0);

		Interval BF1 = FreeSpaceUtil // bottom
				.freeSpace(seg2, seg1, 0, epsilon);
		Interval LF1 = FreeSpaceUtil // left
				.freeSpace(seg1, seg2, 0, epsilon);

		double ls = LF1.getStart();
		double le = LF1.getEnd();
		double bs = BF1.getStart();
		double be = BF1.getEnd();
		Interval BR1 = null;
		Interval LR1 = null;
		if (DoubleUtil.isValid(ls) && ls <= 0 && le >= 0) {
			LR1 = new Interval(LF1.getStart(), LF1.getEnd());
		}
		if (DoubleUtil.isValid(bs) && bs <= 0 && be >= 0) {
			BR1 = new Interval(BF1.getStart(), BF1.getEnd());
		}

		B[0][0] = BR1;
		L[0][0] = LR1;

		// Compute other cells

		for (int y = 0; y < nSegmentsQ; y++) {
			for (int x = 0; x < nSegmentsP; x++) {
				seg1 = FrechetUtil.getSegment(line1, x);
				seg2 = FrechetUtil.getSegment(line2, y);

				Interval LRij = L[x][y];
				Interval BRij = B[x][y];
				Interval LFi1j = FreeSpaceUtil
						.freeSpace(seg1, seg2, 1, epsilon);
				Interval BFij1 = FreeSpaceUtil
						.freeSpace(seg2, seg1, 1, epsilon);
				L[x + 1][y] = FreeSpaceUtil
						.reachableL(LRij, BRij, LFi1j, BFij1);
				B[x][y + 1] = FreeSpaceUtil
						.reachableB(LRij, BRij, LFi1j, BFij1);
			}
		}
	}

	public Interval getLR(int p, int q)
	{
		return L[p][q];
	}

	public Interval getBR(int p, int q)
	{
		return B[p][q];
	}

}

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
package de.topobyte.livecg.core.geometry.util;

import java.util.Iterator;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.util.circular.IntRing;

public class SegmentIterable implements Iterable<Segment>
{
	private Chain chain;

	public SegmentIterable(Chain chain)
	{
		this.chain = chain;
	}

	@Override
	public Iterator<Segment> iterator()
	{
		return new SegmentIterator();
	}

	private class SegmentIterator implements Iterator<Segment>
	{

		int i, max;
		IntRing ring;

		public SegmentIterator()
		{
			i = 0;
			int n = chain.getNumberOfNodes();
			max = n - 2;
			if (chain.isClosed()) {
				max += 1;
			}
			ring = new IntRing(n);
		}

		@Override
		public boolean hasNext()
		{
			return i <= max;
		}

		@Override
		public Segment next()
		{
			int j = ring.next().value();
			Node n1 = chain.getNode(i++);
			Node n2 = chain.getNode(j);
			return new Segment(n1, n2);
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}

	}
}

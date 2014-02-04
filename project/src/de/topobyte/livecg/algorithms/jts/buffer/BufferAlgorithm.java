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
package de.topobyte.livecg.algorithms.jts.buffer;

import com.vividsolutions.jts.geom.Geometry;

import de.topobyte.livecg.core.algorithm.DefaultSceneAlgorithm;
import de.topobyte.livecg.core.geometry.geom.BoundingBoxes;
import de.topobyte.livecg.core.geometry.geom.JtsUtil;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.geom.Rectangles;

// TODO: handle MultiPolygon results correctly (occur with negative distance values)
public class BufferAlgorithm extends DefaultSceneAlgorithm
{

	private Polygon polygon;
	private int distance;
	private Polygon result;

	public BufferAlgorithm(Polygon polygon, int distance)
	{
		this.polygon = polygon;
		this.distance = distance;
		computeResult();
	}

	public int getDistance()
	{
		return distance;
	}

	public void setDistance(int distance)
	{
		this.distance = distance;
		computeResult();
	}

	public Polygon getOriginal()
	{
		return polygon;
	}

	public Polygon getResult()
	{
		return result;
	}

	private void computeResult()
	{
		JtsUtil jtsUtil = new JtsUtil();
		com.vividsolutions.jts.geom.Polygon p = jtsUtil.toJts(polygon);
		Geometry buffer = p.buffer(distance);
		if (!(buffer instanceof com.vividsolutions.jts.geom.Polygon)) {
			result = null;
			return;
		}
		com.vividsolutions.jts.geom.Polygon r = (com.vividsolutions.jts.geom.Polygon) buffer;
		result = jtsUtil.fromJts(r);
	}

	public void update()
	{
		computeResult();
	}

	@Override
	public Rectangle getScene()
	{
		Rectangle bbox = BoundingBoxes.get(polygon);
		if (result != null) {
			bbox = Rectangles.union(bbox, BoundingBoxes.get(result));
		}
		Rectangle scene = Rectangles.extend(bbox, 15);
		return scene;
	}

}

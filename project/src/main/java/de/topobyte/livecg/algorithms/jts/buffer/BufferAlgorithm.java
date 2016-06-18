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

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.operation.buffer.BufferOp;
import com.vividsolutions.jts.operation.buffer.BufferParameters;

import de.topobyte.livecg.core.algorithm.DefaultSceneAlgorithm;
import de.topobyte.livecg.core.geometry.geom.BoundingBoxes;
import de.topobyte.livecg.core.geometry.geom.JtsUtil;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.core.geometry.geom.Rectangle;
import de.topobyte.livecg.core.geometry.geom.Rectangles;
import de.topobyte.livecg.ui.geometryeditor.SetOfGeometries;

public class BufferAlgorithm extends DefaultSceneAlgorithm
{

	private SetOfGeometries geometries;
	private BufferConfig config;
	private List<Polygon> result = new ArrayList<>();

	public BufferAlgorithm(SetOfGeometries geometries, BufferConfig config)
	{
		this.geometries = geometries;
		this.config = config;
		computeResult();
	}

	public BufferConfig getConfig()
	{
		return config;
	}

	public void setConfig(BufferConfig config)
	{
		this.config = config;
		computeResult();
	}

	public SetOfGeometries getInput()
	{
		return geometries;
	}

	public List<Polygon> getResult()
	{
		return result;
	}

	private void computeResult()
	{
		JtsUtil jtsUtil = new JtsUtil();
		GeometryCollection gc = jtsUtil.toJts(geometries);

		BufferParameters parameters = new BufferParameters();
		parameters.setEndCapStyle(config.getCapStyle());
		parameters.setJoinStyle(config.getJoinStyle());

		BufferOp bufferOp = new BufferOp(gc, parameters);
		Geometry buffer = bufferOp.getResultGeometry(config.getDistance());
		result.clear();
		if (buffer instanceof com.vividsolutions.jts.geom.Polygon) {
			com.vividsolutions.jts.geom.Polygon r = (com.vividsolutions.jts.geom.Polygon) buffer;
			result.add(jtsUtil.fromJts(r));
			return;
		} else if (buffer instanceof GeometryCollection) {
			GeometryCollection collection = (GeometryCollection) buffer;
			for (int i = 0; i < collection.getNumGeometries(); i++) {
				Geometry part = collection.getGeometryN(i);
				if (!(part instanceof com.vividsolutions.jts.geom.Polygon)) {
					continue;
				}
				com.vividsolutions.jts.geom.Polygon r = (com.vividsolutions.jts.geom.Polygon) part;
				result.add(jtsUtil.fromJts(r));
			}
		}
	}

	public void update()
	{
		computeResult();
	}

	@Override
	public Rectangle getScene()
	{
		Rectangle bbox = BoundingBoxes.get(geometries);
		if (result != null) {
			for (Polygon p : result) {
				bbox = Rectangles.union(bbox, BoundingBoxes.get(p));
			}
		}
		Rectangle scene = Rectangles.extend(bbox, 15);
		return scene;
	}

}

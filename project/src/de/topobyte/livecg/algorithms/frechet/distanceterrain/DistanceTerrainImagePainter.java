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
package de.topobyte.livecg.algorithms.frechet.distanceterrain;

import de.topobyte.livecg.algorithms.frechet.freespace.calc.LineSegment;
import de.topobyte.livecg.core.lina.Vector2;
import de.topobyte.livecg.core.painting.Color;
import de.topobyte.livecg.core.painting.Image;
import de.topobyte.livecg.util.colorgradient.Gradient;
import de.topobyte.livecg.util.colorgradient.HueGradient;

public class DistanceTerrainImagePainter
{
	private Image image;
	private int ox;
	private int oy;
	private int width;
	private int height;
	private LineSegment seg1 = null;
	private LineSegment seg2 = null;

	private Gradient gradient = new HueGradient();

	public DistanceTerrainImagePainter(Image image, int x, int y, int width,
			int height, LineSegment seg1, LineSegment seg2)
	{
		this.image = image;
		this.ox = x;
		this.oy = y;
		this.width = width;
		this.height = height;
		this.seg1 = seg1;
		this.seg2 = seg2;
	}

	public void paint()
	{
		if (seg1 == null || seg2 == null) {
			return;
		}

		for (int y = 0; y < height; y++) {
			double t = y / (double) height;
			Vector2 Qt = get(seg2, t);
			for (int x = 0; x < width; x++) {
				double s = x / (double) width;
				Vector2 Ps = get(seg1, s);

				double dx = Qt.getX() - Ps.getX();
				double dy = Qt.getY() - Ps.getY();

				double d = Math.sqrt(dx * dx + dy * dy);
				Color c = getColor(d);
				image.setRGB(ox + x, oy + height - y - 1, c.getRGB());
			}
		}
	}

	private Vector2 get(LineSegment seg, double s)
	{
		return seg.getStart().add(seg.getDirection().mult(s));
	}

	private Color getColor(double distance)
	{
		return gradient.getColor(distance / 600);
	}
}

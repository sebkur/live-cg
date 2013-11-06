/* This file is part of LiveCG.$
 *$
 * Copyright (C) 2013  Sebastian Kuerten
 *$
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *$
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *$
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.topobyte.livecg.geometryeditor.geometryeditor.scale;

public class ScaleLine
{
	private final float height;
	private final float strokeWidth;
	private final double step;
	private final boolean hasLabel;

	public ScaleLine(float height, float strokeWidth, double step,
			boolean hasLabel)
	{
		this.height = height;
		this.strokeWidth = strokeWidth;
		this.step = step;
		this.hasLabel = hasLabel;
	}

	public float getHeight()
	{
		return height;
	}

	public float getStrokeWidth()
	{
		return strokeWidth;
	}

	public double getStep()
	{
		return step;
	}

	public boolean hasLabel()
	{
		return hasLabel;
	}

	public boolean occupies(double position)
	{
		double s = (position / step);
		return Math.abs(Math.round(s) - s) < 0.0001;
	}

}

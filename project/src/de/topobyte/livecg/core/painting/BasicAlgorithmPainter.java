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
package de.topobyte.livecg.core.painting;

public abstract class BasicAlgorithmPainter implements AlgorithmPainter
{

	protected Painter painter;
	protected int width, height;

	protected double zoom = 1;
	protected double positionX = 0;
	protected double positionY = 0;

	public BasicAlgorithmPainter(Painter painter)
	{
		this.painter = painter;
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public void setWidth(int width)
	{
		this.width = width;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public void setHeight(int height)
	{
		this.height = height;
	}

	@Override
	public void setPainter(Painter painter)
	{
		this.painter = painter;
	}

	@Override
	public double getPositionX()
	{
		return positionX;
	}

	@Override
	public double getPositionY()
	{
		return positionY;
	}

	@Override
	public double getZoom()
	{
		return zoom;
	}

	@Override
	public void setZoom(double zoom)
	{
		this.zoom = zoom;
	}

	@Override
	public void setPositionX(double x)
	{
		this.positionX = x;
	}

	@Override
	public void setPositionY(double y)
	{
		this.positionY = y;
	}
}

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
package de.topobyte.livecg.geometryeditor.geometryeditor.action;

import java.awt.event.ActionEvent;

import de.topobyte.livecg.geometryeditor.action.BasicAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.GeometryEditPane;

public class ZoomAction extends BasicAction
{

	public enum Type {
		IN, OUT, IDENTITY
	}

	private static final long serialVersionUID = -3260994363917453585L;

	private GeometryEditPane editPane;
	private Type type;

	public ZoomAction(GeometryEditPane editPane, Type type)
	{
		super(title(type), description(type), icon(type));
		this.editPane = editPane;
		this.type = type;
	}

	private static String icon(Type type)
	{
		switch (type) {
		default:
		case IDENTITY:
			return "res/images/24x24/zoom-original.png";
		case IN:
			return "res/images/24x24/zoom-in.png";
		case OUT:
			return "res/images/24x24/zoom-out.png";
		}
	}

	private static String title(Type type)
	{
		switch (type) {
		default:
		case IDENTITY:
			return "Zoom 100%";
		case IN:
			return "Zoom in";
		case OUT:
			return "Zoom out";
		}
	}

	private static String description(Type type)
	{
		switch (type) {
		default:
		case IDENTITY:
			return "Zoom the scene to 100%";
		case IN:
			return "Zoom in the scene";
		case OUT:
			return "Zoom out the scene";
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		double zoom = editPane.getZoom();
		switch (type) {
		default:
		case IDENTITY:
			zoom = 1;
			break;
		case IN:
			zoom *= 1.2;
			break;
		case OUT:
			zoom /= 1.2;
			break;
		}
		editPane.setZoomCentered(zoom);
		editPane.repaint();
	}

}

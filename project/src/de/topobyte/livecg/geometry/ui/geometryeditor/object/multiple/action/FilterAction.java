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
package de.topobyte.livecg.geometry.ui.geometryeditor.object.multiple.action;

import java.awt.event.ActionEvent;

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.geometry.ui.geometryeditor.action.BasicAction;
import de.topobyte.livecg.util.ListUtil;

public class FilterAction extends BasicAction
{

	private static final long serialVersionUID = -9125573108574206860L;

	public enum ObjectType {
		Node, Chain, Ring, Polygon
	}

	private GeometryEditPane editPane;
	private ObjectType type;
	private boolean inverse;

	public FilterAction(GeometryEditPane editPane, ObjectType type,
			boolean inverse)
	{
		super(getName(type, inverse), getDescription(type, inverse), getImage(
				type, inverse));
		this.editPane = editPane;
		this.type = type;
		this.inverse = inverse;
	}

	private static String getName(ObjectType type, boolean inverse)
	{
		String prefix = inverse ? "-" : "=";
		switch (type) {
		case Chain:
			return prefix + "Chains";
		case Node:
			return prefix + "Nodes";
		case Polygon:
			return prefix + "Polygons";
		case Ring:
			return prefix + "Rings";
		default:
			return null;
		}
	}

	private static String getDescription(ObjectType type, boolean inverse)
	{
		if (!inverse) {
			switch (type) {
			case Chain:
				return "Select chains only";
			case Node:
				return "Select nodes only";
			case Polygon:
				return "Select polygons only";
			case Ring:
				return "Select rings only";
			default:
				return null;
			}
		} else {
			switch (type) {
			case Chain:
				return "Unselect chains";
			case Node:
				return "Unselect nodes";
			case Polygon:
				return "Unselect polygons";
			case Ring:
				return "Unselect rings";
			default:
				return null;
			}
		}
	}

	private static String getImage(ObjectType type, boolean inverse)
	{
		switch (type) {
		case Chain:
			return "res/images/way.png";
		case Node:
			return "res/images/node.png";
		case Polygon:
			return "res/images/multipolygon.png";
		case Ring:
			return "res/images/closedway.png";
		default:
			return null;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		boolean changed = false;
		if (!inverse) {
			switch (type) {
			case Node:
				changed |= editPane.clearCurrentChains();
				changed |= editPane.clearCurrentPolygons();
				break;
			case Chain:
				changed |= editPane.clearCurrentNodes();
				changed |= editPane.clearCurrentPolygons();
				break;
			case Polygon:
				changed |= editPane.clearCurrentNodes();
				changed |= editPane.clearCurrentChains();
				break;
			case Ring:
				changed |= editPane.clearCurrentNodes();
				changed |= editPane.clearCurrentPolygons();
				for (Chain chain : ListUtil.copy(editPane.getCurrentChains())) {
					if (!chain.isClosed()) {
						changed |= editPane.removeCurrentChain(chain);
					}
				}
				break;
			}
		} else {
			switch (type) {
			case Node:
				changed |= editPane.clearCurrentNodes();
				break;
			case Chain:
				changed |= editPane.clearCurrentChains();
				break;
			case Polygon:
				changed |= editPane.clearCurrentPolygons();
				break;
			case Ring:
				for (Chain chain : ListUtil.copy(editPane.getCurrentChains())) {
					if (chain.isClosed()) {
						changed |= editPane.removeCurrentChain(chain);
					}
				}
				break;
			}
		}
		if (changed) {
			editPane.repaint();
		}
	}

}

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

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.topobyte.livecg.geometry.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.geometry.ui.geometryeditor.action.BasicAction;
import de.topobyte.livecg.geometry.ui.geometryeditor.object.multiple.action.FilterAction.ObjectType;

public class ShowFilterMenuAction extends BasicAction
{

	private static final long serialVersionUID = -8993679410125030493L;

	private GeometryEditPane editPane;
	private Component invoker;

	public ShowFilterMenuAction(GeometryEditPane editPane, Component invoker)
	{
		super(null, "Show filter options", "res/images/24x24/filter.png");
		this.editPane = editPane;
		this.invoker = invoker;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JPopupMenu popup = new JPopupMenu("Filter");

		// @formatter:off
		JMenuItem itemNodes = new JMenuItem(new FilterAction(editPane, ObjectType.Node, false));
		JMenuItem itemChains = new JMenuItem(new FilterAction(editPane, ObjectType.Chain, false));
		JMenuItem itemRings = new JMenuItem(new FilterAction(editPane, ObjectType.Ring, false));
		JMenuItem itemPolygons = new JMenuItem(new FilterAction(editPane, ObjectType.Polygon, false));
		
		JMenuItem itemNodesI = new JMenuItem(new FilterAction(editPane, ObjectType.Node, true));
		JMenuItem itemChainsI = new JMenuItem(new FilterAction(editPane, ObjectType.Chain, true));
		JMenuItem itemRingsI = new JMenuItem(new FilterAction(editPane, ObjectType.Ring, true));
		JMenuItem itemPolygonsI = new JMenuItem(new FilterAction(editPane, ObjectType.Polygon, true));
		// @formatter:on

		popup.add(itemNodes);
		popup.add(itemChains);
		popup.add(itemRings);
		popup.add(itemPolygons);
		popup.add(itemNodesI);
		popup.add(itemChainsI);
		popup.add(itemRingsI);
		popup.add(itemPolygonsI);

		popup.show(invoker, invoker.getWidth(), 0);
	}

}

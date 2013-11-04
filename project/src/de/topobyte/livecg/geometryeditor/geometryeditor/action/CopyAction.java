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

package de.topobyte.livecg.geometryeditor.geometryeditor.action;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.Node;
import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.geometryeditor.action.BasicAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.geometryeditor.geometryeditor.SetOfGeometries;
import de.topobyte.livecg.geometryeditor.geometryeditor.clipboard.GeometryTransferable;

public class CopyAction extends BasicAction
{

	private static final long serialVersionUID = 5990737748224404622L;

	static final Logger logger = LoggerFactory.getLogger(CopyAction.class);

	private final GeometryEditPane editPane;

	public CopyAction(GeometryEditPane editPane)
	{
		super("Copy", "Copy to clipboard",
				"org/freedesktop/tango/22x22/actions/edit-copy.png");
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		SetOfGeometries geometries = new SetOfGeometries();
		for (Node node : editPane.getCurrentNodes()) {
			for (Chain chain : node.getEndpointChains()) {
				if (chain.getNumberOfNodes() == 1) {
					geometries.addChain(chain);
				}
			}
		}
		for (Chain chain : editPane.getCurrentChains()) {
			geometries.addChain(chain);
		}
		for (Polygon polygon : editPane.getCurrentPolygons()) {
			geometries.addPolygon(polygon);
		}

		GeometryTransferable transferable = new GeometryTransferable(geometries);
		clipboard.setContents(transferable, null);
	}

}

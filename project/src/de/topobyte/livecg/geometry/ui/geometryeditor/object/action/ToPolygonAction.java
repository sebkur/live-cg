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
package de.topobyte.livecg.geometry.ui.geometryeditor.object.action;

import java.awt.event.ActionEvent;

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.Polygon;
import de.topobyte.livecg.geometry.ui.geometryeditor.Content;
import de.topobyte.livecg.geometry.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.geometry.ui.geometryeditor.action.BasicAction;

public class ToPolygonAction extends BasicAction
{

	private static final long serialVersionUID = -7826180655312955433L;

	private GeometryEditPane editPane;
	private Chain chain;

	public ToPolygonAction(GeometryEditPane editPane, Chain chain)
	{
		super("to polygon", "Convert to polygon",
				"res/images/24x24/multipolygon.png");
		this.editPane = editPane;
		this.chain = chain;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Polygon polygon = new Polygon(chain, null);
		Content content = editPane.getContent();
		content.removeChain(chain);
		editPane.removeCurrentChain(chain);
		content.addPolygon(polygon);
		content.fireContentChanged();
	}

}

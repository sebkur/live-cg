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
package de.topobyte.livecg.ui.geometryeditor.object.multiple.action;

import java.awt.event.ActionEvent;
import java.util.List;

import de.topobyte.livecg.geometry.geom.Chain;
import de.topobyte.livecg.geometry.geom.CrossingsTestHelper;
import de.topobyte.livecg.geometry.geom.Polygon;
import de.topobyte.livecg.ui.geometryeditor.Content;
import de.topobyte.livecg.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.ui.geometryeditor.action.BasicAction;
import de.topobyte.util.ListUtil;

public class ToPolygonAction extends BasicAction
{

	private static final long serialVersionUID = -7826180655312955433L;

	private GeometryEditPane editPane;

	public ToPolygonAction(GeometryEditPane editPane)
	{
		super("to polygon", "Convert to polygon",
				"res/images/24x24/multipolygon.png");
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		List<Chain> chains = editPane.getCurrentChains();
		// Identify shell
		Chain shell = null;
		shells: for (int i = 0; i < chains.size(); i++) {
			Chain chain = chains.get(i);
			holes: for (int j = 0; j < chains.size(); j++) {
				if (i == j) {
					continue holes;
				}
				Chain other = chains.get(j);
				boolean ok = CrossingsTestHelper.covers(chain, other);
				if (!ok) {
					continue shells;
				}
			}
			shell = chain;
		}

		if (shell == null) {
			return;
		}

		List<Chain> holes = ListUtil.copy(chains);
		holes.remove(shell);
		Polygon polygon = new Polygon(shell, holes);

		Content content = editPane.getContent();
		content.removeChain(shell);
		editPane.removeCurrentChain(shell);
		for (Chain chain : holes) {
			content.removeChain(chain);
			editPane.removeCurrentChain(chain);
		}

		content.addPolygon(polygon);
		content.fireContentChanged();
	}

}

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
package de.topobyte.livecg.core.ui.geometryeditor.action;

import java.awt.event.ActionEvent;
import java.util.List;

import de.topobyte.livecg.core.geometry.geom.Chain;
import de.topobyte.livecg.core.geometry.geom.CloseabilityException;
import de.topobyte.livecg.core.ui.geometryeditor.GeometryEditPane;

public class OpenCloseRingAction extends BasicAction
{

	private static final long serialVersionUID = -7826180655312955433L;

	private GeometryEditPane editPane;

	public OpenCloseRingAction(GeometryEditPane editPane)
	{
		super("Open / Close Ring", "Open / Close a ring",
				"org/freedesktop/tango/22x22/actions/document-new.png");
		this.editPane = editPane;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		List<Chain> chains = editPane.getCurrentChains();
		if (chains.size() != 1) {
			return;
		}
		Chain chain = chains.iterator().next();
		try {
			boolean closed = !chain.isClosed();
			chain.setClosed(closed);
			editPane.getContent().fireContentChanged();
		} catch (CloseabilityException e1) {
			// ignore silently
		}
	}

}
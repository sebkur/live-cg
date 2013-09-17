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
package de.topobyte.livecg.geometry.ui.geometryeditor.object;

import java.awt.Window;

import javax.swing.JDialog;

import de.topobyte.livecg.geometry.ui.geom.Editable;
import de.topobyte.livecg.geometry.ui.geometryeditor.Content;
import de.topobyte.livecg.geometry.ui.geometryeditor.ContentChangedListener;

public class ObjectDialog extends JDialog
{

	private static final long serialVersionUID = -9016694962587077670L;

	private Content content;

	public ObjectDialog(Window window, Content content)
	{
		super(window, "Object");
		this.content = content;

		setContentPane(new NothingPanel());

		content.addContentChangedListener(new ContentChangedListener() {

			@Override
			public void contentChanged()
			{
				update();
			}
		});
	}

	private Editable current = null;
	private PolygonalChainPanel pcp = null;

	protected void update()
	{
		Editable active = content.getEditingLine();
		if (active == null) {
			if (current != null) {
				current = null;
				setContentPane(new NothingPanel());
			}
		} else {
			if (current != active) {
				current = active;
				pcp = new PolygonalChainPanel(content, active);
				setContentPane(pcp);
			} else {
				pcp.update();
			}
		}
		validate();
	}
}

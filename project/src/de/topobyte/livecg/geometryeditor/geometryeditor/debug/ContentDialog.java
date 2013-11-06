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
package de.topobyte.livecg.geometryeditor.geometryeditor.debug;

import java.awt.Window;

import javax.swing.JDialog;

import de.topobyte.livecg.geometryeditor.geometryeditor.ContentChangedListener;
import de.topobyte.livecg.geometryeditor.geometryeditor.ContentReferenceChangedListener;
import de.topobyte.livecg.geometryeditor.geometryeditor.GeometryEditPane;

public class ContentDialog extends JDialog
{

	private static final long serialVersionUID = 7538623430943863923L;

	private GeometryEditPane editPane;

	private MultiplePanel mp = null;

	public ContentDialog(Window window, GeometryEditPane editPane)
	{
		super(window, "Content");
		this.editPane = editPane;

		mp = new MultiplePanel(editPane);
		setContentPane(mp);

		editPane.addContentReferenceChangedListener(new ContentReferenceChangedListener() {

			@Override
			public void contentReferenceChanged()
			{
				initForContent();
			}
		});

		initForContent();
	}

	protected void initForContent()
	{
		editPane.getContent().addContentChangedListener(
				new ContentChangedListener() {

					@Override
					public void contentChanged()
					{
						update();
					}

					@Override
					public void dimensionChanged()
					{
						// ignore
					}

				});
	}

	protected void update()
	{
		mp.update();
	}
}

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

package de.topobyte.livecg.geometryeditor.geometryeditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

import de.topobyte.livecg.geometryeditor.geometryeditor.scale.Scale;
import de.topobyte.livecg.geometryeditor.geometryeditor.scale.ScaleX;
import de.topobyte.livecg.geometryeditor.geometryeditor.scale.ScaleY;
import de.topobyte.swing.layout.GridBagHelper;

public class GeometryEditor extends JPanel
{

	private static final long serialVersionUID = 8780613881909508056L;

	private GeometryEditPane editPane;

	public GeometryEditor()
	{
		editPane = new GeometryEditPane();
		Scale scaleX = new ScaleX();
		Scale scaleY = new ScaleY();
		JScrollBar scrollerH = new JScrollBar(JScrollBar.HORIZONTAL);
		JScrollBar scrollerV = new JScrollBar(JScrollBar.VERTICAL);

		SceneBoundedRangeModel rangeH = new SceneBoundedRangeModel(editPane,
				true);
		scrollerH.setModel(rangeH);

		SceneBoundedRangeModel rangeV = new SceneBoundedRangeModel(editPane,
				false);
		scrollerV.setModel(rangeV);

		ScaleMouseListener scaleMouseListener = new ScaleMouseListener(scaleX,
				scaleY);
		editPane.addMouseMotionListener(scaleMouseListener);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		GridBagHelper.setGxGy(c, 1, 1);
		GridBagHelper.setWxWyF(c, 1.0, 1.0, GridBagConstraints.BOTH);
		add(editPane, c);

		GridBagHelper.setGxGy(c, 1, 0);
		GridBagHelper.setWxWyF(c, 1.0, 0.0, GridBagConstraints.BOTH);
		add(scaleX, c);

		GridBagHelper.setGxGy(c, 0, 1);
		GridBagHelper.setWxWyF(c, 0.0, 1.0, GridBagConstraints.BOTH);
		add(scaleY, c);

		GridBagHelper.setGxGy(c, 1, 2);
		GridBagHelper.setWxWyF(c, 1.0, 0.0, GridBagConstraints.BOTH);
		add(scrollerH, c);

		GridBagHelper.setGxGy(c, 2, 1);
		GridBagHelper.setWxWyF(c, 0.0, 1.0, GridBagConstraints.BOTH);
		add(scrollerV, c);
	}

	public GeometryEditPane getEditPane()
	{
		return editPane;
	}
}

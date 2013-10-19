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
package de.topobyte.livecg.geometryeditor.geometryeditor.object.single;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;

import de.topobyte.livecg.core.geometry.geom.Polygon;
import de.topobyte.livecg.geometryeditor.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.geometryeditor.geometryeditor.object.action.ConvexHullAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.object.action.ToRingsAction;
import de.topobyte.swing.layout.GridBagHelper;

public class PolygonActionPanel extends JPanel
{

	private static final long serialVersionUID = -2477166078999975171L;

	private JButton toRings;

	public PolygonActionPanel(GeometryEditPane editPane, Polygon polygon)
	{
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		ToRingsAction toRingsAction = new ToRingsAction(editPane, polygon);
		toRings = new JButton(toRingsAction);
		toRings.setMargin(new Insets(0, 0, 0, 0));
		toRings.setText(null);

		ConvexHullAction chAction = new ConvexHullAction(editPane);
		JButton convexHull = new JButton(chAction);
		convexHull.setMargin(new Insets(0, 0, 0, 0));
		convexHull.setText(null);

		c.fill = GridBagConstraints.BOTH;
		GridBagHelper.setGxGy(c, GridBagConstraints.RELATIVE, 0);
		add(toRings, c);
		add(convexHull, c);
	}

}

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
package de.topobyte.livecg.geometry.ui.geometryeditor.object.single;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.topobyte.livecg.geometry.geom.Polygon;
import de.topobyte.livecg.geometry.ui.geometryeditor.GeometryEditPane;
import de.topobyte.livecg.geometry.ui.geometryeditor.object.action.ToRingsAction;
import de.topobyte.swing.layout.GridBagHelper;

public class PolygonPanel extends JPanel
{

	private static final long serialVersionUID = 5640771403274002420L;

	private Polygon polygon;
	private JLabel label;
	private JButton toRings;

	public PolygonPanel(GeometryEditPane editPane, Polygon polygon)
	{
		this.polygon = polygon;
		setLayout(new GridBagLayout());
		label = new JLabel();

		toRings = new JButton("to polygon");
		toRings.setAction(new ToRingsAction(editPane, polygon));

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.LINE_START;

		GridBagHelper.setGxGy(c, 0, 0);
		add(label, c);
		GridBagHelper.setGxGy(c, 0, 1);
		add(toRings, c);
		
		GridBagHelper.setGxGy(c, 0, 2);
		GridBagHelper.setWxWyF(c, 1.0, 1.0, GridBagConstraints.BOTH);
		add(new JPanel(), c);

		update();
	}

	public void update()
	{
		label.setText(getLabelText());
	}

	private String getLabelText()
	{
		return "Object: polygon with " + polygon.getShell().getNumberOfNodes()
				+ " nodes";
	}

}

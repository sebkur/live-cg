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

package de.topobyte.livecg.geometry.ui.geometryeditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import de.topobyte.livecg.geometry.ui.geometryeditor.action.MouseAction;
import de.topobyte.livecg.geometry.ui.geometryeditor.action.SelectAllAction;
import de.topobyte.livecg.geometry.ui.geometryeditor.mousemode.MouseMode;
import de.topobyte.livecg.geometry.ui.geometryeditor.scale.Scale;
import de.topobyte.livecg.geometry.ui.geometryeditor.scale.ScaleX;
import de.topobyte.livecg.geometry.ui.geometryeditor.scale.ScaleY;

public class GeometryEditor extends JPanel
{

	private static final long serialVersionUID = 8780613881909508056L;

	private GeometryEditPane editPane;

	public GeometryEditor()
	{
		editPane = new GeometryEditPane();
		Scale scaleX = new ScaleX();
		Scale scaleY = new ScaleY();

		ScaleMouseListener scaleMouseListener = new ScaleMouseListener(scaleX,
				scaleY);
		editPane.addMouseMotionListener(scaleMouseListener);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		add(editPane, c);

		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 0.0;
		add(scaleX, c);

		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.0;
		c.weighty = 1.0;
		add(scaleY, c);

		/*
		 * setup actions
		 */

		InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = getActionMap();

		inputMap.put(KeyStroke.getKeyStroke('a'), "a");
		inputMap.put(KeyStroke.getKeyStroke('s'), "s");
		inputMap.put(KeyStroke.getKeyStroke('d'), "d");
		inputMap.put(KeyStroke.getKeyStroke('f'), "f");

		inputMap.put(
				KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK),
				"ctrl a");

		MouseAction selectAction = new MouseAction(null, null,
				MouseMode.SELECT_MOVE, editPane);
		MouseAction editAction = new MouseAction(null, null, MouseMode.EDIT,
				editPane);
		MouseAction deleteAction = new MouseAction(null, null,
				MouseMode.DELETE, editPane);

		actionMap.put("a", selectAction);
		actionMap.put("s", editAction);
		actionMap.put("d", deleteAction);
		
		actionMap.put("ctrl a", new SelectAllAction(editPane));
	}

	public GeometryEditPane getEditPane()
	{
		return editPane;
	}
}

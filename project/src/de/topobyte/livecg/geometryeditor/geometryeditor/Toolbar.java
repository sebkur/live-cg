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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import de.topobyte.livecg.geometryeditor.geometryeditor.action.MouseAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.NewAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.OpenAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.action.SaveAction;
import de.topobyte.livecg.geometryeditor.geometryeditor.mousemode.MouseMode;
import de.topobyte.livecg.geometryeditor.geometryeditor.mousemode.MouseModeDescriptions;
import de.topobyte.livecg.geometryeditor.geometryeditor.mousemode.MouseModeProvider;

public class Toolbar extends JToolBar
{

	private static final long serialVersionUID = 8604389649262908523L;

	private GeometryEditPane editPane;
	private JTextField zoom;

	public Toolbar(GeometryEditPane editPane,
			MouseModeProvider mouseModeProvider)
	{
		this.editPane = editPane;
		NewAction newAction = new NewAction(editPane);
		OpenAction openAction = new OpenAction(this, editPane);
		SaveAction saveAction = new SaveAction(this, editPane);

		add(newAction);
		add(openAction);
		add(saveAction);
		addSeparator();

		for (MouseMode mode : new MouseMode[] { MouseMode.SELECT_MOVE,
				MouseMode.ROTATE, MouseMode.SCALE,
				MouseMode.SELECT_RECTANGULAR, MouseMode.EDIT, MouseMode.DELETE }) {
			MouseAction mouseAction = new MouseAction(null,
					MouseModeDescriptions.getShort(mode), mode,
					mouseModeProvider);
			JToggleButton button = new JToggleButton(mouseAction);
			add(button);
		}

		zoom = new JTextField("100%");
		addSeparator();
		add(zoom);

		zoom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				tryUpdateZoom();
			}
		});
		
		editPane.addViewportListener(new ViewportListener() {
			
			@Override
			public void zoomChanged()
			{
				revertZoom();
			}
			
			@Override
			public void viewportChanged()
			{
				// ignore	
			}
		});
	}

	protected void tryUpdateZoom()
	{
		String text = zoom.getText();
		String trimmed = text.trim();
		if (!trimmed.endsWith("%")) {
			revertZoom();
			return;
		}
		String number = trimmed.substring(0, trimmed.length() - 1);
		try {
			double value = Double.parseDouble(number);
			updateZoom(value);
		} catch (NumberFormatException e) {
			revertZoom();
		}
	}

	private void revertZoom()
	{
		double percent = editPane.getZoom() * 100;
		String text;
		if (Math.abs(percent - (int) percent) < 0.0001) {
			text = String.format("%d%%", (int) percent);
		} else {
			text = String.format("%.2f%%", percent);
		}
		zoom.setText(text);
	}

	private void updateZoom(double value)
	{
		editPane.setZoom(value / 100.0);
		editPane.repaint();
	}
}

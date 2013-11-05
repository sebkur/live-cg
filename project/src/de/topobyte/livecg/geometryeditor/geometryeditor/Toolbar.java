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

import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
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
	private JComboBox zoom;

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

		String[] values = { "50%", "80%", "100%", "120%", "200%", "400%" };

		zoom = new JComboBox(values);
		zoom.setEditable(true);
		zoom.setSelectedIndex(2);
		addSeparator();
		add(zoom);

		zoom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				tryUpdateZoom();
				KeyboardFocusManager manager = KeyboardFocusManager
						.getCurrentKeyboardFocusManager();
				manager.focusNextComponent();
			}
		});

		zoom.getEditor().getEditorComponent()
				.addKeyListener(new ZoomKeyAdapter());

		zoom.setMaximumSize(new Dimension(zoom.getPreferredSize().width, zoom
				.getMaximumSize().height));

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
		String text = (String) zoom.getSelectedItem();
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
		zoom.setSelectedItem(text);
	}

	private void updateZoom(double value)
	{
		editPane.setZoomCentered(value / 100.0);
		editPane.repaint();
	}

	private class ZoomKeyAdapter extends KeyAdapter
	{
		@Override
		public void keyTyped(KeyEvent e)
		{
			// consume(e);
		}

		@Override
		public void keyPressed(KeyEvent e)
		{
			consume(e);
		}

		@Override
		public void keyReleased(KeyEvent e)
		{
			// consume(e);
		}

		private void consume(KeyEvent e)
		{
			if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				return;
			}
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				return;
			}
			if (e.getKeyCode() == KeyEvent.VK_DELETE) {
				return;
			}
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				return;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				return;
			}
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				return;
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				return;
			}
			if (e.getKeyCode() == KeyEvent.VK_HOME) {
				return;
			}
			if (e.getKeyCode() == KeyEvent.VK_END) {
				return;
			}
			e.consume();
		}
	}
}

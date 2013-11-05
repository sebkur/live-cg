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
package de.topobyte.livecg.util;

import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;

import de.topobyte.livecg.core.scrolling.Viewport;
import de.topobyte.livecg.core.scrolling.ViewportListener;

public class ZoomInput extends JComboBox
{

	private static final long serialVersionUID = 6856865390726849784L;

	private Viewport viewport;

	public ZoomInput(Viewport viewport)
	{
		this(viewport, new String[] { "50%", "80%", "100%", "120%", "150%",
				"200%", "400%" }, 2);
	}

	public ZoomInput(Viewport viewport, String[] values, int selectedIndex)
	{
		super(values);

		this.viewport = viewport;

		setEditable(true);
		setSelectedIndex(selectedIndex);

		setMaximumSize(new Dimension(getPreferredSize().width,
				getMaximumSize().height));

		getEditor().getEditorComponent().addKeyListener(new ZoomKeyAdapter());

		addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				tryUpdateZoom();
				if (getEditor().getEditorComponent().hasFocus()) {
					KeyboardFocusManager manager = KeyboardFocusManager
							.getCurrentKeyboardFocusManager();
					manager.focusNextComponent();
				}
			}
		});

		viewport.addViewportListener(new ViewportListener() {

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

			@Override
			public void complexChange()
			{
				// ignore
			}
		});
	}

	protected void tryUpdateZoom()
	{
		String text = (String) getSelectedItem();
		String trimmed = text.trim();
		if (!trimmed.endsWith("%")) {
			revertZoom();
			return;
		}
		String number = trimmed.substring(0, trimmed.length() - 1);
		try {
			double value = Double.parseDouble(number);
			viewport.setZoom(value / 100);
		} catch (NumberFormatException e) {
			revertZoom();
		}
	}

	private void revertZoom()
	{
		double percent = viewport.getZoom() * 100;
		String text;
		if (Math.abs(percent - (int) percent) < 0.0001) {
			text = String.format("%d%%", (int) percent);
		} else {
			text = String.format("%.2f%%", percent);
		}
		setSelectedItem(text);
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
